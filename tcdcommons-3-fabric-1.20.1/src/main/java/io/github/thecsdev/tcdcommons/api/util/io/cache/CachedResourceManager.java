package io.github.thecsdev.tcdcommons.api.util.io.cache;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.CACHED_RESOURCE_SERIALIZER;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;

import io.github.thecsdev.tcdcommons.api.registry.TRegistries;
import net.minecraft.util.Identifier;

/**
 * Caching API for asynchronous fetching and caching of external resources.
 */
public final class CachedResourceManager
{
	// ==================================================
	private static final String THREAD_NAME;
	private static final ExecutorService THREAD_SCHEDULER;
	private static final ScheduledExecutorService MAID_SCHEDULER;
	// --------------------------------------------------
	/**
	 * Holds {@link CachedResource}s that are currently cached in RAM.
	 * Used to prevent having to fetch the resource every time you need it.
	 */
	private static final Cache<Identifier, CachedResource<?>> RESOURCE_CACHE;
	
	/**
	 * Holds information about any recently raised {@link Exception}s
	 * while resources were being fetched. Used to prevent "spamming"
	 * of fetching resources that cannot be fetched.
	 */
	private static final Cache<Identifier, Exception> RECENT_EXCEPTIONS;
	
	/**
	 * Holds information about currently ongoing {@link IResourceFetchTask}s.
	 * Prevents concurrent attempts to fetch the same resource by putting
	 * {@link IResourceFetchTask}s into a {@link LinkedBlockingDeque}.
	 */
	private static final Cache<Identifier, LinkedBlockingDeque<IResourceFetchTask<?>>> CURRENT_TASKS;
	// ==================================================
	static
	{
		//prepare threading
		THREAD_NAME = getModID() + ":" + CachedResourceManager.class.getSimpleName().toLowerCase();
		THREAD_SCHEDULER = Executors.newCachedThreadPool(task ->
		{
			final Thread thread = new Thread(task, THREAD_NAME);
			thread.setDaemon(true);
			return thread;
		});
		
		MAID_SCHEDULER = Executors.newScheduledThreadPool(0, task ->
		{
			final Thread thread = new Thread(task, THREAD_NAME + "/maid");
			thread.setDaemon(true);
			return thread;
		});
		MAID_SCHEDULER.scheduleAtFixedRate(() -> cleanUp(), 1, 5, TimeUnit.MINUTES);
		
		//prepare ram cache data
		RESOURCE_CACHE = CacheBuilder.newBuilder()
				//.maximumSize(256) -- don't cache too many entries in RAM, but not too little either; can't have both
				.maximumWeight(1024 * 100) //cache up to around 100mb in RAM
				.expireAfterWrite(1, TimeUnit.HOURS) //in RAM, can last up to an hour
				.weigher((Weigher<Identifier, CachedResource<?>>)
						(k, v) -> (int) Math.min(v.getResourceSizeB(), Integer.MAX_VALUE))
				.build();
		RECENT_EXCEPTIONS = CacheBuilder.newBuilder()
				.maximumSize(256)
				.expireAfterWrite(5, TimeUnit.MINUTES)
				.build();
		CURRENT_TASKS = CacheBuilder.newBuilder()
				.expireAfterWrite(1, TimeUnit.MINUTES)
				.build();
		/*TaskScheduler.schedulePeriodicCacheCleanup(RESOURCE_CACHE);
		TaskScheduler.schedulePeriodicCacheCleanup(RECENT_EXCEPTIONS); -- handled by the maid
		TaskScheduler.schedulePeriodicCacheCleanup(CURRENT_TASKS);*/
		
		//initialize serializer
		CachedResourceSerializer.init();
	}
	// ==================================================
	/**
	 * Asynchronously retrieves or fetches a given external resource and loads it.
	 * @param <R> The type of the resource being obtained.
	 * @param resourceId The unique {@link Identifier} of the resource being obtained.
	 * @param task The {@link IResourceFetchTask} that will be used to obtain and handle the resource.
	 * @throws NullPointerException If an argument is {@code null}, or a method in
	 * {@link IResourceFetchTask} returns {@code null}.
	 */
	public static final synchronized <R> void getResourceAsync(
			final Identifier resourceId,
			final IResourceFetchTask<R> task) throws NullPointerException
	{
		//null checks and variable obtaining
		Objects.requireNonNull(resourceId);
		Objects.requireNonNull(task);
		final var rt = Objects.requireNonNull(task.getResourceType());
		final var mc = Objects.requireNonNull(task.getMinecraftClientOrServer());
		
		//check for any recent exceptions or cached resources related to this request
		{
			//check for any recent errors with the request
			final @Nullable Exception recentException = RECENT_EXCEPTIONS.getIfPresent(resourceId);
			if(recentException != null)
			{
				//if there was a recent error, avoid spamming more requests to
				//try and load the resource, by executing the error handler instead
				mc.executeSync(() -> task.onError(recentException));
				return;
			}
			
			//check for any cached resource
			final @Nullable CachedResource<?> cachedResource = RESOURCE_CACHE.getIfPresent(resourceId);
			if(cachedResource != null && cachedResource.getResource() != null && //null checks
					Objects.equals(rt, cachedResource.getResource().getClass()) && //type checks
					Instant.now().isBefore(cachedResource.getExpirationDate())) //expiration checks
			{
				final @SuppressWarnings("unchecked") R castedRs = (R)cachedResource.getResource();
				mc.executeSync(() -> task.onReady(castedRs));
				return;
			}
		}
		
		//handle tasks
		// - this is done to prevent "concurrent spam fetching" of the same resource
		final var currentTaskQueue = new AtomicReference<>(CURRENT_TASKS.getIfPresent(resourceId));
		if(currentTaskQueue.get() == null)
		{
			final var newTask = new LinkedBlockingDeque<IResourceFetchTask<?>>(Integer.MAX_VALUE);
			CURRENT_TASKS.put(resourceId, newTask);
			currentTaskQueue.set(newTask);
			newTask.add(task);
		}
		else { currentTaskQueue.get().add(task); return; }
		
		//do the fetch (the hardest part; gone wrong; exceptions raised; must see; watch till the end to see crazy results;)
		THREAD_SCHEDULER.execute(() ->
		{
			//prepare to fetch
			final AtomicReference<CachedResource<R>> result = new AtomicReference<>(null);
			final AtomicReference<Exception> error = new AtomicReference<>(null);
			
			//try to fetch the resource
			// - exceptions are handled as usual
			// - errors are handled by throwing them at the game's main thread
			try
			{
				//ensure the resource type is supported and has a serializer
				// - technically arbitrary, but enforces proper ram usage and caching file support
				try { Objects.requireNonNull(getResourceSerializer(rt)); }
				catch(NullPointerException npe)
				{
					throw new UnsupportedOperationException(
							String.format("Resource type '%s' does not have a %s.",
									rt.getName(),
									CachedResourceSerializer.class.getSimpleName()),
							npe);
				}
				
				//fetch
				final var fetched = Objects.requireNonNull(task.fetchResourceSync());
				//enforce type-check
				if(!Objects.equals(rt, fetched.getResourceType()))
					throw new ClassCastException("Resource fetching returned an illegal type.");
				//assign result
				result.set((CachedResource<R>)fetched);
			}
			catch(Exception exc) { error.set(exc); }
			catch(Error err)
			{
				mc.executeSync(() -> { throw err; });
				CURRENT_TASKS.invalidate(resourceId);
				return;
			}
			
			//at this point, the results are ready;
			// - first cache if possible,
			// - then invalidate the current task
			// - then, notify listeners
			if(result.get() != null) RESOURCE_CACHE.put(resourceId, result.get());
			else if(error.get() != null) RECENT_EXCEPTIONS.put(resourceId, error.get());
			CURRENT_TASKS.invalidate(resourceId); //must invalidate before iterating
			
			//notify listeners, on the main thread
			__broadcastResult(currentTaskQueue.get().stream().toList(), result.get(), error.get());
		});
	}
	
	/**
	 * After a {@link CachedResource} fetching task is done, this is called
	 * to broadcast the result to all scheduled {@link IResourceFetchTask}s.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final @Internal void __broadcastResult(
			final Collection<IResourceFetchTask<?>> listeners,
			final @Nullable CachedResource<?> result,
			final @Nullable Exception error)
	{
		//safe null checks, just in case
		if(result == null && error == null) return;
		
		//iterate all tasks that are awaiting results
		listeners.forEach(task ->
		{
			//null check, just in case
			final var mc = task.getMinecraftClientOrServer();
			if(mc == null) return;
			
			//execute task results
			mc.executeSync(() ->
			{
				//broadcast any errors
				if(error != null) task.onError(error);
				//before broadcasting result, enforce type-checks
				else if(!Objects.equals(task.getResourceType(), result.getResourceType()))
					task.onError(new ClassCastException("Fetching returned an illegal resource type."));
				//finally, broadcast successful results
				else ((IResourceFetchTask)task).onReady(result.getResource());
			});
		});
	}
	// --------------------------------------------------
	/**
	 * Obtains the {@link CachedResourceSerializer} for a given {@link CachedResource} type.
	 * @param resourceType A {@link Class} instance representing the type of resource.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @see TRegistries#CACHED_RESOURCE_SERIALIZER
	 */
	public static final @Nullable <T> CachedResourceSerializer<T> getResourceSerializer(
			Class<T> resourceType) throws NullPointerException
	{
		Objects.requireNonNull(resourceType);
		
		//synchronize the registry
		synchronized(CACHED_RESOURCE_SERIALIZER)
		{
			//iterate all registered serializers, and attempt to find the right one
			for(final var entry : CACHED_RESOURCE_SERIALIZER)
			{
				//obtain the serializer
				final var crs = entry.getValue();
				
				//type-check the serializer
				if(!Objects.equals(resourceType, crs.getResourceType()))
					continue;
				
				//cast and return the serializer
				@SuppressWarnings("unchecked")
				final var crsCast = (CachedResourceSerializer<T>)crs;
				return crsCast;
			}
		}
		
		//return null if nothing was found
		return null;
	}
	// ==================================================
	/**
	 * Cleans up cache by calling {@link Cache#cleanUp()} and clears expired {@link CachedResource}s.<br/>
	 * Invoked automatically by the "maid" {@link Thread}.
	 * @see #MAID_SCHEDULER
	 */
	private static final void cleanUp()
	{
		//clean up caches
		RESOURCE_CACHE.cleanUp();
		RECENT_EXCEPTIONS.cleanUp();
		CURRENT_TASKS.cleanUp();
		
		//clean up expired resource cache
		synchronized(RESOURCE_CACHE)
		{
			final var now = Instant.now();
			RESOURCE_CACHE.asMap().entrySet().stream()
					.filter(entry -> now.isAfter(entry.getValue().getExpirationDate()))
					.map(entry -> entry.getKey())
					.toList()
					.forEach(expiredId -> RESOURCE_CACHE.invalidate(expiredId));
		}
	}
	// ==================================================
}
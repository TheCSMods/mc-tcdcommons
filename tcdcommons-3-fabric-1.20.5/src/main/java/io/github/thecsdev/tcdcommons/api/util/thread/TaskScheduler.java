package io.github.thecsdev.tcdcommons.api.util.thread;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.lang.ref.WeakReference;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus.Experimental;

import com.google.common.cache.Cache;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import io.github.thecsdev.tcdcommons.api.util.collections.IdealList;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * A utility that provides methods for scheduling tasks that can
 * then be executed later on Minecraft's main {@link Thread}.
 */
public final class TaskScheduler
{
	// ==================================================
	private static final String THREAD_NAME = getModID() + ":task_scheduler";
	private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(
			1,
			runnable ->
			{
				final var thread = new Thread(runnable, THREAD_NAME);
				thread.setDaemon(true);
				return thread;
			});
	//
	private static final Queue<Map.Entry<Runnable, BooleanSupplier>> CONDITIONAL_TASK_QUEUE =
			Queues.newConcurrentLinkedQueue();
	// --------------------------------------------------
	/**
	 * A {@link Set} of {@link WeakReference}s to {@link Cache} objects that are
	 * to be "cleaned up" from time to time. Use this {@link Set} if you wish to
	 * have your {@link Cache} cleaned up periodically. Usually every 10ish minutes.
	 * @see Cache#cleanUp()
	 */
	private static final Set<WeakReference<Cache<?, ?>>> CACHE_CLEANUP = Sets.newConcurrentHashSet();
	// ==================================================
	private TaskScheduler() {}
	static
	{
		// ---------- CONDITIONAL ENTRIES EXECUTOR LOGIC
		//list has to be IdealList because that one supports concurrent modifications
		final var conditionalEntries = new IdealList<Map.Entry<Runnable, BooleanSupplier>>();
		final Runnable conditionalTaskExecutor = () ->
		{
			//add queue entries to the list of entries
			synchronized(CONDITIONAL_TASK_QUEUE)
			{
				//keep adding while there are entries
				while(!CONDITIONAL_TASK_QUEUE.isEmpty())
				{
					final var next = CONDITIONAL_TASK_QUEUE.poll();
					if(next != null) conditionalEntries.add(next);
				}
			}
			
			//now, time to iterate and execute
			for(final var entry : conditionalEntries)
			{
				//check if the condition finally applies, and continue if not
				try { if(!entry.getValue().getAsBoolean()) continue; }
				catch(Exception e)
				{
					//if a condition result supplier raises an exception; remove its task
					new RuntimeException("Failed to execute scheduled task.", e).printStackTrace();
					conditionalEntries.remove(entry);
					continue;
				}
				
				//if the condition finally applies;
				//- remove the task, and then
				//- execute the task
				conditionalEntries.remove(entry);
				entry.getKey().run();
			}
		};
		SCHEDULER.scheduleAtFixedRate(conditionalTaskExecutor, 1, 1, TimeUnit.SECONDS);
		
		// ---------- PERIODIC CACHE CLEANER
		final Runnable cacheCleaningTask = () ->
		{
			//synchronize the cache set, so as to avoid threading issues
			synchronized(CACHE_CLEANUP)
			{
				//obtain a list of caches that need a cleanup
				final List<Cache<?, ?>> caches = CACHE_CLEANUP.stream()
						.map(entry -> entry.get())
						.filter(entry -> entry != null)
						.collect(Collectors.toList());
				
				//clear any "expired" entries that have been garbage collected
				CACHE_CLEANUP.removeIf(entry -> entry.get() == null);
				
				//clean the caches
				caches.forEach(cache -> { synchronized(cache) { cache.cleanUp(); } });
			}
		};
		SCHEDULER.scheduleAtFixedRate(cacheCleaningTask, 5, 10, TimeUnit.MINUTES);
	}
	// ==================================================
	/**
	 * Schedules a task that periodically cleans up a {@link Cache} object.
	 * @param cache The {@link Cache} object to start cleaning up periodically.
	 * @return The {@link Cache} object that was passed as the argument.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @see #cancelPeriodicCacheCleanup(Cache)
	 * @see Cache#cleanUp()
	 */
	public static <K, V> Cache<K, V> schedulePeriodicCacheCleanup(Cache<K, V> cache) throws NullPointerException
	{
		Objects.requireNonNull(cache);
		synchronized(CACHE_CLEANUP)
		{
			CACHE_CLEANUP.removeIf(entry -> entry.refersTo(cache));
			CACHE_CLEANUP.add(new WeakReference<Cache<?,?>>(cache));
		}
		return cache;
	}
	
	/**
	 * If you have scheduled a task to periodically clean up a {@link Cache}
	 * object, you can cancel that task here.
	 * @param cache The {@link Cache} object to stop cleaning up periodically.
	 * @see #schedulePeriodicCacheCleanup(Cache)
	 */
	public static void cancelPeriodicCacheCleanup(Cache<?, ?> cache)
	{
		synchronized(CACHE_CLEANUP) { CACHE_CLEANUP.removeIf(entry -> entry.refersTo(cache)); }
	}
	// --------------------------------------------------
	/**
	 * Schedules a task to be executed on the {@link ReentrantThreadExecutor} some time in the future.
	 * @apiNote Note that the {@link ReentrantThreadExecutor} may shut down before the task gets to execute.
	 * @apiNote Also note that this system does not track game's ticks, aka it uses a time-frame independent from ticks.
	 */
	public static void scheduleTask(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final long delay,
			final TimeUnit unit,
			final Runnable command) throws NullPointerException
	{
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(unit);
		Objects.requireNonNull(command);
		SCHEDULER.schedule(() -> minecraftClientOrServer.executeSync(command), delay, unit);
	}
	
	/**
	 * Schedules a task to be executed on the {@link ReentrantThreadExecutor} once a given condition is
	 * met, aka once the {@link BooleanSupplier} returns {@code true}.
	 * Once the task is executed, it will never be executed again.
	 * @apiNote Note that the {@link ReentrantThreadExecutor} may shut down before the task gets to execute.
	 * @apiNote Also note that this system does not track game's ticks, aka it uses a time-frame independent from ticks.
	 * @apiNote Submitting tasks whose conditions cannot be met will result in a memory leak.
	 */
	@Experimental
	public static void executeOnce(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final BooleanSupplier condition,
			final Runnable command) throws NullPointerException
	{
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(command);
		CONDITIONAL_TASK_QUEUE.add(new SimpleEntry<>(() -> minecraftClientOrServer.executeSync(command), condition));
	}
	// --------------------------------------------------
	/**
	 * Executes a {@link ProgressiveTask} asynchronously.
	 * @param task The {@link ProgressiveTask} to execute.
	 * @throws IllegalStateException If the {@link ProgressiveTask} is already being executed.
	 */
	public static void executeProgressiveTask(ProgressiveTask<?> task) throws IllegalStateException
	{
		//handle illegal states
		if(task.isRunning())
			throw new IllegalStateException("Task already running.");
		
		//execute the task async
		SCHEDULER.schedule(() -> task.executeSync(), 0, TimeUnit.MILLISECONDS);
	}
	// ==================================================
}
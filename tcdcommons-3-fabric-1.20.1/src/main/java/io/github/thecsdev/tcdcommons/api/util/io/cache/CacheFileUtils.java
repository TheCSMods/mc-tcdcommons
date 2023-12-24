package io.github.thecsdev.tcdcommons.api.util.io.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.math.Tuple2;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import net.minecraft.util.Identifier;

/**
 * {@link Internal} utilities for saving and loading cached data to and from the user's drive.
 */
@Internal final class CacheFileUtils
{
	// ==================================================
	private static final String FS = System.getProperty("file.separator"); //file separator
	private static final String CR = System.getProperty("user.home") + //cache root
			String.format("%1$s.cache%1$sthecsdev%1$smc-tcdcommons", FS);
	// --------------------------------------------------
	private static final String META_KEY_EXPIRATION = "expiration_date";
	// --------------------------------------------------
	private static final Cache<Identifier, ReentrantLock> RESOURCE_LOCKS;
	// ==================================================
	static
	{
		RESOURCE_LOCKS = CacheBuilder.newBuilder()
				.expireAfterAccess(25, TimeUnit.MINUTES)
				.build();
		TaskScheduler.schedulePeriodicCacheCleanup(RESOURCE_LOCKS);
	}
	// --------------------------------------------------
	/**
	 * Handles entry removals for the {@link CachedResourceManager#RESOURCE_CACHE}.
	 */
	static final void resourceCacheRemovalListener(RemovalNotification<Identifier, CachedResource<?>> notification)
	{
		//obtain the key, and null-check it for some paranoid reason
		final var key = notification.getKey();
		if(key == null) return;
		
		//handle the removal based on the cause
		switch(notification.getCause())
		{
			//if the cached entry's expiration date had come, delete the cache
			case EXPIRED: tryDeleteCacheFile(key); break;
			//for any unhandled possibilities, ignore them
			default: return;
		}
	}
	// ==================================================
	/**
	 * Returns the {@link ReentrantLock} that corresponds to a given resource {@link Identifier}.
	 * @apiNote Used to synchronize IO operations of {@link CachedResource}s.
	 */
	private static final ReentrantLock getResourceLock(Identifier resourceId) throws NullPointerException
	{
		Objects.requireNonNull(resourceId);
		synchronized(RESOURCE_LOCKS)
		{
			@Nullable var lock = RESOURCE_LOCKS.getIfPresent(resourceId);
			if(lock == null) RESOURCE_LOCKS.put(resourceId, lock = new ReentrantLock());
			return lock;
		}
	}
	// --------------------------------------------------
	/**
	 * Obtains the cache {@link File}s that should correspond to a given
	 * {@link CachedResource}'s unique {@link Identifier}.<br/>
	 * @apiNote The first {@link File} is where the cache data goes, and
	 * @apiNote the second {@link File} is there the cache metadata goes.
	 */
	public static final Tuple2<File, File> getCacheFileForResource(Identifier resourceId) throws NullPointerException
	{
		final String fileName = CR + FS + resourceId.getNamespace() + FS + resourceId.getPath();
		return new Tuple2<File, File>(new File(fileName), new File(fileName + ".meta"));
	}
	
	/**
	 * Returns true if the cache {@link File}s that should correspond to a given
	 * {@link CachedResource} exist on the user's drive.
	 */
	public static final boolean cacheFileExistsForResource(Identifier resourceId) throws NullPointerException
	{
		final var files = getCacheFileForResource(resourceId);
		try { return files.Item1.exists() && files.Item2.exists(); } catch(Exception e) { return false; }
	}
	// ==================================================
	/**
	 * Attempts to delete the cache {@link File}s that correspond to a given
	 * {@link CachedResource}'s unique {@link Identifier}.
	 */
	public static final boolean tryDeleteCacheFile(Identifier resourceId) throws NullPointerException
	{
		final var lock = getResourceLock(resourceId);
		lock.lock();
		try
		{
			final var files = getCacheFileForResource(resourceId);
			try { return files.Item1.delete() | files.Item2.delete(); } catch(Exception e) { return false; }
		}
		finally { lock.unlock(); }
	}
	// --------------------------------------------------
	/**
	 * Tries to save a {@link CachedResource} to the user's drive.
	 * @apiNote {@code synchronized} to avoid concurrent modifications of {@link File}s.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final boolean trySaveCachedResource(Identifier resourceId, CachedResource<?> resource)
		throws NullPointerException
	{
		final var lock = getResourceLock(resourceId);
		lock.lock();
		try
		{
			final var files = getCacheFileForResource(resourceId);
			Objects.requireNonNull(resource);
			try
			{
				//first try and delete old cache files and prepare for new ones
				files.Item1.delete();
				files.Item2.delete();
				files.Item1.getParentFile().mkdirs();
				files.Item2.getParentFile().mkdirs();
				
				//write metadata
				final var metaJson = new JsonObject();
				metaJson.addProperty(META_KEY_EXPIRATION, resource.getExpirationDate().toString());
				FileUtils.writeStringToFile(files.Item2, new Gson().toJson(metaJson), StandardCharsets.UTF_16);
				
				//write data
				final var serializer = (CachedResourceSerializer)Objects.requireNonNull(
						CachedResourceManager.getResourceSerializer(resource.getResourceType()));
				final var fos = new FileOutputStream(files.Item1);
				try { serializer.serialize(resource.getResource(), fos); } finally { fos.close(); }
				
				return true;
			}
			catch(Exception e) { return false; }
		}
		finally { lock.unlock(); }
	}
	
	/**
	 * Tries to load a {@link CachedResource} from the user's drive.
	 * @apiNote {@code synchronized} to avoid concurrent modifications of {@link File}s.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final @Nullable <R> CachedResource<R> tryLoadCachedResource(
			Identifier resourceId, Class<R> resourceType) throws NullPointerException
	{
		final var lock = getResourceLock(resourceId);
		lock.lock();
		try
		{
			final var files = getCacheFileForResource(resourceId);
			Objects.requireNonNull(resourceType);
			try
			{
				//prepare
				final var serializer = (CachedResourceSerializer)Objects.requireNonNull(
						CachedResourceManager.getResourceSerializer(resourceType));
				
				//first try to read the metadata
				final String metaStr = FileUtils.readFileToString(files.Item2, StandardCharsets.UTF_16);
				final JsonObject metaJson = new Gson().fromJson(metaStr, JsonObject.class);
				final Instant expirationDate = Instant.parse(metaJson.get(META_KEY_EXPIRATION).getAsString());
				if(Instant.now().isAfter(expirationDate))
					throw new DateTimeException("Resource expired.");
				
				//now try to read the resource data
				final var iStream = new CountingInputStream(new FileInputStream(files.Item1));
				Object resource = null; long resourceSize = 0;
				try
				{
					resource = Objects.requireNonNull(serializer.deserialize(iStream));
					resourceSize = iStream.getByteCount();
				}
				finally { iStream.close(); }
				
				//create cached resource and return it
				if(!Objects.equals(resourceType, resource.getClass())) //enforce type-checks
					throw new ClassCastException("Deserialization returned an illegal type.");
				return (CachedResource<R>) new CachedResource(resource, resourceSize, expirationDate);
			}
			catch(Exception e)
			{
				//at this stage, the files could be broken, malformed, corrupted,
				//or something else could be an issue; delete them, just in case
				tryDeleteCacheFile(resourceId);
				return null;
			}
		}
		finally { lock.unlock(); }
	}
	// ==================================================
}
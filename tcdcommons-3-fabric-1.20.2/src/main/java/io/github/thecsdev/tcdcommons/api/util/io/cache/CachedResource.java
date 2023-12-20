package io.github.thecsdev.tcdcommons.api.util.io.cache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Provides information about a resource that has successfully been fetched
 * via {@link IResourceFetchTask#fetchResourceSync()}.
 */
public final class CachedResource<R>
{
	// ==================================================
	private final R resource;
	private final long resourceSizeB;
	private final Instant expirationDate;
	// ==================================================
	/**
	 * Creates an instance of {@link CachedResource}.
	 * @param resource The resource that is being cached.
	 * @param resourceSizeB The amount of space the resource takes up in RAM, in bytes.
	 * @param expirationDate The time in the future, at which the resource will expire.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public CachedResource(R resource, long resourceSizeB, Instant expirationDate) throws NullPointerException
	{
		this.resource = Objects.requireNonNull(resource);
		this.resourceSizeB = Math.max(resourceSizeB, 0);
		this.expirationDate = clampExpirationDate(expirationDate);
	}
	// ==================================================
	/**
	 * Returns the {@link Class} instance representing the type of the {@link CachedResource}.
	 */
	@SuppressWarnings("unchecked")
	public final Class<R> getResourceType() { return (Class<R>) this.resource.getClass(); }
	
	/**
	 * Returns the fetched resource itself.
	 */
	public final R getResource() { return resource; }
	
	/**
	 * Returns the "size" of the resource, aka the amount of RAM memory
	 * the resource takes up. Measured in bytes.
	 */
	public final long getResourceSizeB() { return resourceSizeB; }
	
	/**
	 * Returns the time at which this {@link CachedResource} will be considered as "expired".
	 */
	public final Instant getExpirationDate() { return this.expirationDate; }
	// ==================================================
	/**
	 * Creates a {@link CachedResource} instance for {@link String}s.
	 * @param text The {@link String} representing the resource.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final CachedResource<String> ofString(
			final String text, final Instant expirationDate) throws NullPointerException
	{
		Objects.requireNonNull(text);
		Objects.requireNonNull(expirationDate);
		final int textSize = 40 + text.getBytes(StandardCharsets.UTF_16).length;
		return new CachedResource<>(text, textSize, expirationDate);
	}
	
	/**
	 * Creates a {@link CachedResource} instance for {@link Byte} arrays.
	 * @param bytes The {@link Byte} array representing the resource.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final CachedResource<byte[]> ofBytes(
			final byte[] bytes, final Instant expirationDate) throws NullPointerException
	{
		Objects.requireNonNull(bytes);
		Objects.requireNonNull(expirationDate);
		final int size = 16 + bytes.length;
		return new CachedResource<>(bytes, size, expirationDate);
	}
	// ==================================================
	/**
	 * Ensures a given {@link Instant} is within the allowed time-frame.
	 * @param expirationDate The {@link Instant} to clamp.
	 */
	private static final Instant clampExpirationDate(Instant expirationDate)
	{
		//prepare
		Objects.requireNonNull(expirationDate);
		final Instant now = Instant.now();
		
		//check if too early
		if(expirationDate.isBefore(now.plusSeconds(60 * 4)))
			return now.plusSeconds(60 * 5);
		
		//check if too late
		else if(expirationDate.isAfter(now.plus(Duration.ofDays(32))))
			return now.plus(Duration.ofDays(31));
		
		//return as usual
		return expirationDate;
	}
	// ==================================================
}
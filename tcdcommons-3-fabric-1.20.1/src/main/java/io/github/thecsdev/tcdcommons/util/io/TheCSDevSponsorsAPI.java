package io.github.thecsdev.tcdcommons.util.io;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * Utility methods for fetching information from the 'TheCSDev/sponsors' repository.
 */
public final class TheCSDevSponsorsAPI
{
	// ==================================================
	private static final URI FEATURED_SPONSORS, SPECIAL_THANKS;
	// ==================================================
	private TheCSDevSponsorsAPI() {}
	public static final void init() {/*calls 'static'*/}
	static
	{
		try
		{
			FEATURED_SPONSORS = new URI("https://thecsdev.github.io/sponsors/api/v1/featured_sponsors.json");
			SPECIAL_THANKS = new URI("https://thecsdev.github.io/sponsors/api/v1/special_thanks.json");
		}
		catch(Exception e) { throw new ExceptionInInitializerError(e); }
	}
	// ==================================================
	/**
	 * Asynchronously fetches a {@link JsonArray} of people listed on the "featured sponsors" list.
	 */
	public static final void getFeaturedSponsorsAsync(
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonArray> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		getPeopleAsync(FEATURED_SPONSORS, minecraftClientOrServer, onReady, onError);
	}

	/**
	 * Asynchronously fetches a {@link JsonArray} of people listed on the "special thanks" list.
	 */
	public static final void getSpecialThanksAsync(
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonArray> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		getPeopleAsync(SPECIAL_THANKS, minecraftClientOrServer, onReady, onError);
	}
	
	/**
	 * Asynchronously and {@link Internal}ly fetches an "array of people" in the {@link JsonArray} format.
	 */
	private static final @Internal void getPeopleAsync(
			URI endpoint,
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonArray> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		CachedResourceManager.getResourceAsync(
			endpointUriToId(endpoint),
			new IResourceFetchTask<String>()
			{
				public Class<String> getResourceType() { return String.class; }
				public ThreadExecutor<?> getMinecraftClientOrServer() { return minecraftClientOrServer; }
				public CachedResource<String> fetchResourceSync() throws Exception
				{
					final var response = HttpUtils.httpGetSyncS(endpoint);
					final var expiration = Instant.now().plus(Duration.ofHours(2));
					return CachedResource.ofString(response, expiration);
				}
				public void onReady(String resource)
				{
					try { onReady.accept(new Gson().fromJson(resource, JsonArray.class)); }
					catch(Exception e) { onError.accept(e); }
				}
				public void onError(Exception exception) { onError.accept(exception); }
			});
	}
	// --------------------------------------------------
	/**
	 * Converts a {@link URI} to an {@link Identifier}.
	 * @apiNote Only compatible with 'thecsdev.github.io/sponsors'.
	 * Other {@link URI}s will cause issues if they contain illegal characters.
	 */
	private static final Identifier endpointUriToId(URI uri) throws NullPointerException, InvalidIdentifierException
	{
		Objects.requireNonNull(uri);
		final String host = Objects.requireNonNull(uri.getHost()).toLowerCase();
		String path = Objects.requireNonNull(uri.getPath()).toLowerCase();
		if(path.startsWith("/")) path = path.substring(1);
		return new Identifier(host, path);
	}
	// ==================================================
}
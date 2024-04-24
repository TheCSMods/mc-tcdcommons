package io.github.thecsdev.tcdcommons.util.io.http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * TheCSDev's utility methods for making HTTP requests to TheCSDev's API endpoints.
 * @author TheCSDev
 */
public final @Internal class TcdWebApi
{
	// ==================================================
	private TcdWebApi() {}
	// ==================================================
	public static final Gson GSON = new Gson();
	
	/**
	 * A {@link URL} endpoint that contains a list of webhook URLs in JSON form.
	 */
	public static final URL WEBHOOKS_URL = safeURL("https://thecsdev.github.io/sponsors/api/v2/webhooks.json");
	
	/**
	 * The unique {@link Identifier} for the {@link CachedResource}
	 * containing information about {@link #WEBHOOKS_URL}s.
	 */
	public static final Identifier WEBHOOKS_URL_RESOURCE_ID = new Identifier("tcdcommons", "webhooks.json");
	// ==================================================
	/**
	 * Asynchronously fetches and retrieves a {@link JsonObject}
	 * containing the webhook {@link URL}s.
	 * @param minecraftClientOrServer The Minecraft client or server instance.
	 * @param onReady Invoked when the retrieval succeeds.
	 * @param onError Invoked when the retrieval fails.
	 */
	public static final void getWebhookUrlsAsync(
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonObject> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		//requirements
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		
		//fetch
		CachedResourceManager.getResourceAsync(
			WEBHOOKS_URL_RESOURCE_ID,
			new IResourceFetchTask<JsonObject>()
			{
				public Class<JsonObject> getResourceType() { return JsonObject.class; }
				public ThreadExecutor<?> getMinecraftClientOrServer() { return minecraftClientOrServer; }
				public void onReady(JsonObject resource) { onReady.accept(resource); }
				public void onError(Exception exception) { onError.accept(exception); }
				public CachedResource<JsonObject> fetchResourceSync() throws Exception
				{
					final var response = HttpUtils.httpGetSyncS(WEBHOOKS_URL.toURI());
					final var responseJson = GSON.fromJson(response, JsonObject.class);
					final var expiration = Instant.now().plus(Duration.ofHours(3));
					return new CachedResource<JsonObject>(responseJson, 40 + response.length(), expiration);
				}
			});
	}
	// ==================================================
	/**
	 * Intended for constructing {@link URL} instances in static initializers.
	 * @param url The {@link String} representation of a {@link URL}.
	 */
	private static final URL safeURL(String url)
	{
		try { return URI.create(url).toURL(); }
		catch(MalformedURLException e) { throw new ExceptionInInitializerError(e); }
	}
	// --------------------------------------------------
	/**
	 * Converts a {@link URL} instance a unique
	 * {@link Identifier} that represents said {@link URL}.
	 * @param url The {@link URL} instance.
	 * @throws InvalidIdentifierException If the {@link URL} is unsupported.
	 */
	public static final Identifier getIdFromUrl(URL url) throws InvalidIdentifierException
	{
		//require not null
		Objects.requireNonNull(url);
		
		//---------- construct the namespace
		final var namespace = safeIdentifierStr(url.getProtocol());
		
		//---------- construct the path
		//paths start with the host name
		var path = safeIdentifierStr(url.getHost());
		
		//append url path if one is present
		if(!StringUtils.isBlank(url.getPath()))
		{
			path += safeIdentifierStr(url.getPath());
			
			//append url query if one is present
			if(!StringUtils.isBlank(url.getQuery()))
			{
				//an algorithm to ensure all possible arrangements for a given set
				//of query parameters all get the same identifier path
				final String query = Arrays.stream(safeIdentifierStr(url.getQuery()).split("&"))
						.filter(entry -> !StringUtils.isBlank(entry))
						.sorted()
						.map(entry -> entry.replace('=', '/'))
						.collect(Collectors.joining("/"));
				path += "/__query/" + query;
			}
		}
		
		//---------- construct and return the identifier
		return new Identifier(namespace, path);
	}
	
	private static final String safeIdentifierStr(String in)
	{
		return ("" + in).toLowerCase()
				.replace('-', '_').replace(' ', '_')
				.replace(':', '_');
	}
	// ==================================================
}
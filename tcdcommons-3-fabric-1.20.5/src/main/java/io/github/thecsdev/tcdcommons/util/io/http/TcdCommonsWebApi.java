package io.github.thecsdev.tcdcommons.util.io.http;

import static io.github.thecsdev.tcdcommons.util.io.http.TcdWebApi.GSON;
import static io.github.thecsdev.tcdcommons.util.io.http.TcdWebApi.getWebhookUrlsAsync;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

public final class TcdCommonsWebApi
{
	// ==================================================
	private TcdCommonsWebApi() {}
	// ==================================================
	/**
	 * Asynchronously fetches and retrieves a {@link JsonArray}
	 * containing {@link TcdWebApiPerson}s that are given a "special thanks".
	 * @param minecraftClientOrServer The Minecraft client or server instance.
	 * @param onReady Invoked when the retrieval succeeds.
	 * @param onError Invoked when the retrieval fails.
	 */
	public static final void getSpecialThanksAsync(
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonArray> onReady,
			final Consumer<Exception> onError)
	{
		//requirements
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		
		//define the webhooks handler
		final Consumer<JsonObject> webhooksHandler = webhooks ->
		{
			//try to obtain the "special thanks" url, and fetch data from it
			try
			{
				//obtain the url
				final var stUrl = URI.create(webhooks.get("tcdcommons:special_thanks").getAsString()).toURL();
				
				//fetch data from the url
				CachedResourceManager.getResourceAsync(
					new Identifier("tcdcommons", "special_thanks.json"),
					new IResourceFetchTask<JsonArray>()
					{
						public Class<JsonArray> getResourceType() { return JsonArray.class; }
						public ThreadExecutor<?> getMinecraftClientOrServer() { return minecraftClientOrServer; }
						public void onReady(JsonArray resource) { onReady.accept(resource); }
						public void onError(Exception exception) { onError.accept(exception); }
						public CachedResource<JsonArray> fetchResourceSync() throws Exception
						{
							final var response = HttpUtils.httpGetSyncS(stUrl.toURI());
							final var responseJson = GSON.fromJson(response, JsonArray.class);
							final var expiration = Instant.now().plus(Duration.ofDays(7));
							return new CachedResource<JsonArray>(responseJson, 40 + response.length(), expiration);
						}
					});
			}
			catch(Exception e) { onError.accept(e); }
		};
		
		//begin the process: fetch webhooks, and then fetch special thanks people
		getWebhookUrlsAsync(minecraftClientOrServer, webhooksHandler, onError);
	}
	
	/**
	 * Asynchronously fetches and retrieves a {@link JsonArray}
	 * containing {@link TcdWebApiPerson}s that are "featured sponsors" of TheCSDev.
	 * @param minecraftClientOrServer The Minecraft client or server instance.
	 * @param onReady Invoked when the retrieval succeeds.
	 * @param onError Invoked when the retrieval fails.
	 */
	public static final void getFeaturedSponsorsAsync(
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonArray> onReady,
			final Consumer<Exception> onError)
	{
		//requirements
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		
		//define the webhooks handler
		final Consumer<JsonObject> webhooksHandler = webhooks ->
		{
			//try to obtain the "special thanks" url, and fetch data from it
			try
			{
				//obtain the url
				final var stUrl = URI.create(webhooks.get("tcdcommons:featured_sponsors").getAsString()).toURL();
				
				//fetch data from the url
				CachedResourceManager.getResourceAsync(
					new Identifier("tcdcommons", "featured_sponsors.json"),
					new IResourceFetchTask<JsonArray>()
					{
						public Class<JsonArray> getResourceType() { return JsonArray.class; }
						public ThreadExecutor<?> getMinecraftClientOrServer() { return minecraftClientOrServer; }
						public void onReady(JsonArray resource) { onReady.accept(resource); }
						public void onError(Exception exception) { onError.accept(exception); }
						public CachedResource<JsonArray> fetchResourceSync() throws Exception
						{
							final var response = HttpUtils.httpGetSyncS(stUrl.toURI());
							final var responseJson = GSON.fromJson(response, JsonArray.class);
							final var expiration = Instant.now().plus(Duration.ofDays(1));
							return new CachedResource<JsonArray>(responseJson, 40 + response.length(), expiration);
						}
					});
			}
			catch(Exception e) { onError.accept(e); }
		};
		
		//begin the process: fetch webhooks, and then fetch special thanks people
		getWebhookUrlsAsync(minecraftClientOrServer, webhooksHandler, onError);
	}
	// ==================================================
}
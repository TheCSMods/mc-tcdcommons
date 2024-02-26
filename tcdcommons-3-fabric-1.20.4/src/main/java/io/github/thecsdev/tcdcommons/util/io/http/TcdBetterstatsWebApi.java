package io.github.thecsdev.tcdcommons.util.io.http;

import static io.github.thecsdev.tcdcommons.util.io.http.TcdWebApi.GSON;
import static io.github.thecsdev.tcdcommons.util.io.http.TcdWebApi.getWebhookUrlsAsync;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

public final class TcdBetterstatsWebApi
{
	// ==================================================
	private TcdBetterstatsWebApi() {}
	// ==================================================
	/**
	 * Asynchronously fetches the {@link JsonObject} containing "phrase"
	 * texts for a given {@link StatType} of {@link EntityType}.
	 * @param statType The {@link StatType} in question.
	 * @param minecraftClientOrServer The Minecraft client or server instance.
	 * @param onReady Invoked when the retrieval succeeds.
	 * @param onError Invoked when the retrieval fails.
	 */
	public static final void getMobStatPhrasesAsync(
			StatType<?> statType,
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonObject> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		//requirements
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		final Identifier statTypeId = Objects.requireNonNull(Registries.STAT_TYPE.getId(statType));
		
		//define the webhooks handler
		final Consumer<JsonObject> webhooksHandler = webhooks ->
		{
			//try to obtain the "special thanks" url, and fetch data from it
			try
			{
				//obtain the url
				final var stUrlStr = webhooks.get("betterstats:mob_stat_phrases").getAsString() +
						"?id=" + statTypeId.toString();
				final var stUrl = new URL(stUrlStr);
				
				//fetch data from the url
				CachedResourceManager.getResourceAsync(
					new Identifier(
							"betterstats",
							"mob_stat_phrases/" + statTypeId.toString().replace(':', '/') + ".json"),
					new IResourceFetchTask<JsonObject>()
					{
						public Class<JsonObject> getResourceType() { return JsonObject.class; }
						public ThreadExecutor<?> getMinecraftClientOrServer() { return minecraftClientOrServer; }
						public void onReady(JsonObject resource) { onReady.accept(resource); }
						public void onError(Exception exception) { onError.accept(exception); }
						public CachedResource<JsonObject> fetchResourceSync() throws Exception
						{
							final var response = HttpUtils.httpGetSyncS(stUrl.toURI());
							final var responseJson = GSON.fromJson(response, JsonObject.class);
							final var expiration = Instant.now().plus(Duration.ofDays(30));
							return new CachedResource<JsonObject>(responseJson, 40 + response.length(), expiration);
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
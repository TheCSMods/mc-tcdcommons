package io.github.thecsdev.tcdcommons.util.io.http;

import static io.github.thecsdev.tcdcommons.util.io.http.TcdWebApi.GSON;
import static io.github.thecsdev.tcdcommons.util.io.http.TcdWebApi.getWebhookUrlsAsync;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * @apiNote Used by `betterstats`. Do not rename or remove any class members.
 */
public final class TcdBetterstatsWebApi
{
	// ==================================================
	/**
	 * An {@link Internal} container for {@link StatType} phrases
	 * that have previously been fetched.
	 */
	private static final @Internal Map<StatType<?>, JsonObject> STP_CONTAINER = new HashMap<>();
	// ==================================================
	private TcdBetterstatsWebApi() {}
	// ==================================================
	/**
	 * Asynchronously fetches the {@link JsonObject} containing "phrase"
	 * texts for a given {@link StatType}.
	 * @param statType The {@link StatType} in question.
	 * @param minecraftClientOrServer The Minecraft client or server instance.
	 * @param onReady Invoked when the retrieval succeeds.
	 * @param onError Invoked when the retrieval fails.
	 */
	public static final void getStatTypePhrasesAsync(
			StatType<?> statType,
			final ThreadExecutor<?> minecraftClientOrServer,
			final Consumer<JsonObject> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		//requirements
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		
		//check the container
		{
			final @Nullable var c = STP_CONTAINER.get(statType);
			if(c != null) { minecraftClientOrServer.executeSync(() -> onReady.accept(c)); return; }
		}
		
		//another requirement
		final Identifier statTypeId = Objects.requireNonNull(Registries.STAT_TYPE.getId(statType));
		
		//define the webhooks handler
		final Consumer<JsonObject> webhooksHandler = webhooks ->
		{
			//try to obtain the "special thanks" url, and fetch data from it
			try
			{
				//obtain the url
				final var stUrlStr = webhooks.get("betterstats:stattype_phrases").getAsString() +
						"?id=" + statTypeId.toString();
				final var stUrl = URI.create(stUrlStr).toURL();
				
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
							
							STP_CONTAINER.put(statType, responseJson);
							return new CachedResource<JsonObject>(responseJson, 40 + response.length(), expiration);
						}
					});
			}
			catch(Exception e) { onError.accept(e); }
		};
		
		//begin the process: fetch webhooks, and then everything else
		getWebhookUrlsAsync(minecraftClientOrServer, webhooksHandler, onError);
	}
	// ==================================================
	/**
	 * Returns a fetched {@link StatType} phrase from the
	 * {@link #STP_CONTAINER}, if one exists.
	 * @param statType The {@link StatType} in question.
	 */
	public static final @Nullable String getStpFromContainer(StatType<?> statType)
	{
		//obtain the json
		final @Nullable var json = STP_CONTAINER.get(statType);
		if(json == null) return null;
		
		//obtain the phrase for the current language
		String lang = TextUtils.translatable("language.code").getString();
		if(!json.has(lang)) lang = "en_us";
		
		//obtain the stat phrase
		if(json.has(lang))
			try { return json.get(lang).getAsString(); } catch(Exception e) {}
		return null;
	}
	// ==================================================
}
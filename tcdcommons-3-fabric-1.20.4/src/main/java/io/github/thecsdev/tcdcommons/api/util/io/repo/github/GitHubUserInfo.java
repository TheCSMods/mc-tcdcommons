package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.getInfoAsync;
import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.httpGetStringSync;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.http.client.ClientProtocolException;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * A {@link RepositoryUserInfo} for GitHub accounts.
 */
public final class GitHubUserInfo extends RepositoryUserInfo
{
	// ==================================================
	/**
	 * Caching is used to optimize network bandwidth usage as well as
	 * minimizing the effects of any rate limits that may apply.
	 */
	private static final Cache<String, GitHubUserInfo> CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(6, TimeUnit.HOURS)
			.build();
	static { TaskScheduler.schedulePeriodicCacheCleanup(CACHE); }
	// ==================================================
	private final String id;
	private final String login;
	private final Text name;
	private final @Nullable Text bio;
	private final @Nullable String avatar_url;
	// ==================================================
	GitHubUserInfo(JsonObject json) throws NullPointerException
	{
		this.id = Long.toString(json.get("id").getAsLong());
		this.login = json.get("login").getAsString();
		if(json.has("name") && !json.get("name").isJsonNull())
			this.name = literal(json.get("name").getAsString());
		else this.name = literal(this.login);
		if(json.has("bio") && !json.get("bio").isJsonNull())
			this.bio = literal(json.get("bio").getAsString());
		else this.bio = null;
		if(json.has("avatar_url") && !json.get("avatar_url").isJsonNull())
			this.avatar_url = json.get("avatar_url").getAsString();
		else this.avatar_url = null;
		
		//cache
		CACHE.put(("https://api.github.com/users/" + this.login).toLowerCase(), this);
	}
	// ==================================================
	public final @Override String getID() { return this.id; }
	// --------------------------------------------------
	public final @Override String getName() { return this.login; }
	public final @Override Text getDisplayName() { return this.name; }
	public final @Nullable @Override Text getDescription() { return this.bio; }
	// --------------------------------------------------
	public final @Nullable @Override String getAvatarURL() { return this.avatar_url; }
	// ==================================================
	/**
	 * Asynchronously obtains information about a given GitHub account.
	 * @param userId The unique user integer identifier. Not the unique "account name".
	 * @see RepositoryInfoProvider#getInfoAsync(ReentrantThreadExecutor, Consumer, Consumer, java.util.concurrent.Callable)
	 */
	public static final void getUserInfoAsync(
			final BigInteger userId,
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final Consumer<GitHubUserInfo> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		Objects.requireNonNull(userId);
		getInfoAsync(minecraftClientOrServer, onReady, onError, () -> fetchUserInfoSync(userId));
	}
	
	/**
	 * Asynchronously obtains information about a given GitHub account.
	 * @param accountName The unique user "account name". Not the unique user integer identifier.
	 * @see RepositoryInfoProvider#getInfoAsync(ReentrantThreadExecutor, Consumer, Consumer, java.util.concurrent.Callable)
	 */
	public static final void getUserInfoAsync(
			final String accountName,
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final Consumer<GitHubUserInfo> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		Objects.requireNonNull(accountName);
		getInfoAsync(minecraftClientOrServer, onReady, onError, () -> fetchUserInfoSync(accountName));
	}
	
	/**
	 * Synchronously obtains information about a given GitHub account.
	 * @param userId The unique user integer identifier. Not the unique "account name".
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final GitHubUserInfo fetchUserInfoSync(BigInteger userId)
			throws NullPointerException, ClientProtocolException, URISyntaxException, IOException
	{
		//prepare and handle cache
		final var apiEndpoint = String.format(
				"https://api.github.com/user/%s",
				Objects.requireNonNull(userId).toString());
		final var cached = CACHE.getIfPresent(apiEndpoint.toLowerCase());
		if(cached != null) return cached;
		
		//perform HTTP GET
		final String content = httpGetStringSync(apiEndpoint);
		
		//parse JSON
		try { return new GitHubUserInfo(new Gson().fromJson(content, JsonObject.class)); }
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	
	/**
	 * Synchronously obtains information about a given GitHub account.
	 * @param accountName The unique user "account name". Not the unique user integer identifier.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final GitHubUserInfo fetchUserInfoSync(String accountName)
			throws NullPointerException, ClientProtocolException, URISyntaxException, IOException
	{
		//prepare and handle cache
		final var apiEndpoint = String.format(
				"https://api.github.com/users/%s",
				Objects.requireNonNull(accountName));
		final var cached = CACHE.getIfPresent(apiEndpoint.toLowerCase());
		if(cached != null) return cached;
		
		//perform HTTP GET
		final String content = httpGetStringSync(apiEndpoint);
		
		//parse JSON
		try { return new GitHubUserInfo(new Gson().fromJson(content, JsonObject.class)); }
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	// ==================================================
}
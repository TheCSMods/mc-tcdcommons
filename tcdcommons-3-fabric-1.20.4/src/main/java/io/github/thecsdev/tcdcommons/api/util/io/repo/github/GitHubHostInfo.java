package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryHostInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.github.ugc.GitHubRepositoryInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * {@link RepositoryHostInfo} for <code>github.com</code>.
 */
public final class GitHubHostInfo extends RepositoryHostInfo
{
	// ==================================================
	private static final GitHubHostInfo INSTANCE = new GitHubHostInfo();
	// --------------------------------------------------
	private static final String ID;
	private static final URL HTML_URL, API_URL;
	private static final String DISPLAY_NAME;
	// --------------------------------------------------
	private static final String CACHE_ID_BASE = "api.github.com:"; //Note: Unformatted!
	// ==================================================
	private GitHubHostInfo() {}
	static
	{
		try
		{
			ID = "github.com";
			HTML_URL = new URL("https://github.com/");
			API_URL = new URL("https://api.github.com/");
			DISPLAY_NAME = "GitHub";
		}
		catch (MalformedURLException e) { throw new ExceptionInInitializerError(e); }
	}
	// --------------------------------------------------
	public static final GitHubHostInfo getInstance() { return INSTANCE; }
	// ==================================================
	public final @Override String getID() { return ID; }
	public final @Override URL getURL() { return HTML_URL; }
	public final @Override URL getApiURL() { return API_URL; }
	// --------------------------------------------------
	public final @Override String getDisplayName() { return DISPLAY_NAME; }
	// ==================================================
	public final @Override RepositoryUserInfo fetchUserInfoByIdSync(String userId)
			throws NullPointerException, IOException
	{
		//perform cache-based fetching
		Objects.requireNonNull(userId);
		final AtomicReference<String> jsonStr = new AtomicReference<>();
		final AtomicReference<Exception> fetchErr = new AtomicReference<>();
		CachedResourceManager.getResourceSync(
			new Identifier(CACHE_ID_BASE + "user/" + userId),
			new IResourceFetchTask<String>()
			{
				public Class<String> getResourceType() { return String.class; }
				public ThreadExecutor<?> getMinecraftClientOrServer() { return null; }
				public void onReady(String resource) { jsonStr.set(resource); }
				public void onError(Exception exception) { fetchErr.set(exception); }
				public CachedResource<String> fetchResourceSync() throws Exception
				{
					return CachedResource.ofString(
							HttpUtils.httpGetSyncS(new URI("https://api.github.com/user/" + userId)),
							Instant.now().plus(Duration.ofDays(3)));
				}
			});
		
		//check for errors
		if(fetchErr.get() != null)
			throw new IOException(String.format("Failed to fetch user data for '%s'.", userId), fetchErr.get());
		
		//parse and return the results
		try { return new GitHubUserInfo(new Gson().fromJson(Objects.requireNonNull(jsonStr.get()), JsonObject.class)); }
		catch(Exception exc)
		{
			final String msg = String.format("Failed to parse user data JSON for '%s'.", userId);
			throw new IOException(msg, exc);
		}
	} 
	// --------------------------------------------------
	public final @Override RepositoryInfo fetchRepoInfoByIdSync(String repoId)
			throws NullPointerException, IOException
	{
		//perform cache-based fetching
		Objects.requireNonNull(repoId);
		final AtomicReference<String> jsonStr = new AtomicReference<>();
		final AtomicReference<Exception> fetchErr = new AtomicReference<>();
		CachedResourceManager.getResourceSync(
			new Identifier(CACHE_ID_BASE + "repositories/" + repoId),
			new IResourceFetchTask<String>()
			{
				public Class<String> getResourceType() { return String.class; }
				public ThreadExecutor<?> getMinecraftClientOrServer() { return null; }
				public void onReady(String resource) { jsonStr.set(resource); }
				public void onError(Exception exception) { fetchErr.set(exception); }
				public CachedResource<String> fetchResourceSync() throws Exception
				{
					return CachedResource.ofString(
							HttpUtils.httpGetSyncS(new URI("https://api.github.com/repositories/" + repoId)),
							Instant.now().plus(Duration.ofDays(2)));
				}
			});
		
		//check for errors
		if(fetchErr.get() != null)
			throw new IOException(String.format("Failed to fetch repository data for '%s'.", repoId), fetchErr.get());
		
		//parse and return the results
		try { return new GitHubRepositoryInfo(new Gson().fromJson(Objects.requireNonNull(jsonStr.get()), JsonObject.class)); }
		catch(Exception exc)
		{
			final String msg = String.format("Failed to parse repository data JSON for '%s'.", repoId);
			throw new IOException(msg, exc);
		}
	}
	// ==================================================
}
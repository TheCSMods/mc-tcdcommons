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
	private static final int CACHE_USER_DURATION = 7, CACHE_REPO_DURATION = 5;
	// ==================================================
	private GitHubHostInfo() {}
	static
	{
		try
		{
			ID = "github.com";
			HTML_URL = URI.create("https://github.com/").toURL();
			API_URL = URI.create("https://api.github.com/").toURL();
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
	public final @Override GitHubUserInfo fetchUserInfoByIdSync(String userId)
			throws NullPointerException, IOException
	{
		//perform cache-based fetching
		Objects.requireNonNull(userId);
		final AtomicReference<String> jsonStr = new AtomicReference<>();
		final AtomicReference<Exception> fetchErr = new AtomicReference<>();
		CachedResourceManager.getResourceSync(
			new Identifier(CACHE_ID_BASE + "user/" + userId + ".json"),
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
							Instant.now().plus(Duration.ofDays(CACHE_USER_DURATION)));
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
	
	/**
	 * Synchronously fetches {@link RepositoryUserInfo} about a given
	 * user, using the user's unique account name.
	 * @param accountName A {@link String} representing the user's unique account name.
	 */
	public final GitHubUserInfo fetchUserInfoByNameSync(String accountName)
		throws NullPointerException, IOException
	{
		//perform cache-based fetching
		Objects.requireNonNull(accountName);
		final AtomicReference<String> jsonStr = new AtomicReference<>();
		final AtomicReference<Exception> fetchErr = new AtomicReference<>();
		CachedResourceManager.getResourceSync(
			new Identifier(CACHE_ID_BASE + "users/" + accountName.toLowerCase() + ".json"),
			new IResourceFetchTask<String>()
			{
				public Class<String> getResourceType() { return String.class; }
				public ThreadExecutor<?> getMinecraftClientOrServer() { return null; }
				public void onReady(String resource) { jsonStr.set(resource); }
				public void onError(Exception exception) { fetchErr.set(exception); }
				public CachedResource<String> fetchResourceSync() throws Exception
				{
					return CachedResource.ofString(
							HttpUtils.httpGetSyncS(new URI("https://api.github.com/users/" + accountName)),
							Instant.now().plus(Duration.ofDays(CACHE_USER_DURATION)));
				}
			});
		
		//check for errors
		if(fetchErr.get() != null)
			throw new IOException(String.format("Failed to fetch user data for '%s'.", accountName), fetchErr.get());
		
		//parse and return the results
		try { return new GitHubUserInfo(new Gson().fromJson(Objects.requireNonNull(jsonStr.get()), JsonObject.class)); }
		catch(Exception exc)
		{
			final String msg = String.format("Failed to parse user data JSON for '%s'.", accountName);
			throw new IOException(msg, exc);
		}
	}
	// --------------------------------------------------
	public final @Override GitHubRepositoryInfo fetchRepoInfoByIdSync(String repoId)
			throws NullPointerException, IOException
	{
		//perform cache-based fetching
		Objects.requireNonNull(repoId);
		final AtomicReference<String> jsonStr = new AtomicReference<>();
		final AtomicReference<Exception> fetchErr = new AtomicReference<>();
		CachedResourceManager.getResourceSync(
			new Identifier(CACHE_ID_BASE + "repositories/" + repoId + ".json"),
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
							Instant.now().plus(Duration.ofDays(CACHE_REPO_DURATION)));
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
	
	/**
	 * Synchronously fetches {@link RepositoryInfo} about a give repository,
	 * using the repository's unique name and author account name.
	 * @param authorAccountName The unique name of the author's account.
	 * @param repoName The unique name of the repository.
	 */
	public final GitHubRepositoryInfo fetchRepoInfoByNameSync(String authorAccountName, String repoName)
			throws NullPointerException, IOException
	{
		//perform cache-based fetching
		Objects.requireNonNull(authorAccountName);
		Objects.requireNonNull(repoName);
		final var fullName = authorAccountName.toLowerCase() + "/" + repoName.toLowerCase();
		
		final AtomicReference<String> jsonStr = new AtomicReference<>();
		final AtomicReference<Exception> fetchErr = new AtomicReference<>();
		CachedResourceManager.getResourceSync(
			new Identifier(CACHE_ID_BASE + "repos/" + fullName + ".json"),
			new IResourceFetchTask<String>()
			{
				public Class<String> getResourceType() { return String.class; }
				public ThreadExecutor<?> getMinecraftClientOrServer() { return null; }
				public void onReady(String resource) { jsonStr.set(resource); }
				public void onError(Exception exception) { fetchErr.set(exception); }
				public CachedResource<String> fetchResourceSync() throws Exception
				{
					return CachedResource.ofString(
							HttpUtils.httpGetSyncS(new URI("https://api.github.com/repos/" + fullName)),
							Instant.now().plus(Duration.ofDays(CACHE_REPO_DURATION)));
				}
			});
		
		//check for errors
		if(fetchErr.get() != null)
			throw new IOException(String.format("Failed to fetch repository data for '%s'.", fullName), fetchErr.get());
		
		//parse and return the results
		try { return new GitHubRepositoryInfo(new Gson().fromJson(Objects.requireNonNull(jsonStr.get()), JsonObject.class)); }
		catch(Exception exc)
		{
			final String msg = String.format("Failed to parse repository data JSON for '%s'.", fullName);
			throw new IOException(msg, exc);
		}
	}
	// ==================================================
}
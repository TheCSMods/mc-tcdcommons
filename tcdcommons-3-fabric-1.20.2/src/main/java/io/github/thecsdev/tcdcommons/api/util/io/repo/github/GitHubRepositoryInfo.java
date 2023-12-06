package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.httpGetSync;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;
import net.minecraft.text.Text;

public final class GitHubRepositoryInfo extends RepositoryInfo
{
	// ==================================================
	/**
	 * Caching is used to optimize network bandwidth usage as well as
	 * minimizing the effects of any rate limits that may apply.
	 */
	private static final Cache<String, GitHubRepositoryInfo> CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(30, TimeUnit.MINUTES)
			.build();
	// ==================================================
	private final GitHubUserInfo owner;
	// ---------------------------------------------------
	private final String id;
	private final String owner_id;
	final String full_name, description;
	private final Text[] topics;
	private final boolean has_issues, allow_forking;
	private final int open_issues_count, forks;
	private final Instant created_at, updated_at;
	// ==================================================
	GitHubRepositoryInfo(JsonObject json)
	{
		this.owner = new GitHubUserInfo(json.get("owner").getAsJsonObject());
		this.cachedAuthorUserInfo = this.owner;
		
		this.id = Long.toString(json.get("id").getAsLong());
		this.owner_id = this.owner.getID();
		this.full_name = json.get("full_name").getAsString();
		this.description = json.get("description").getAsString();
		List<Text> topics = new ArrayList<>();
		if(json.has("topics") && !json.get("topics").isJsonNull())
			json.get("topics").getAsJsonArray().forEach(topic -> topics.add(literal(topic.getAsString())));
		this.topics = topics.toArray(new Text[] {});
		this.has_issues = json.get("has_issues").getAsBoolean(); 
		this.allow_forking = json.get("allow_forking").getAsBoolean();
		if(this.has_issues) this.open_issues_count = json.get("open_issues_count").getAsInt();
		else this.open_issues_count = 0;
		if(this.allow_forking) this.forks = json.get("forks").getAsInt();
		else this.forks = 0;
		this.created_at = Instant.parse(json.get("created_at").getAsString());
		this.updated_at = Instant.parse(json.get("updated_at").getAsString());
	}
	// ==================================================
	public final @Override String getID() { return this.id; }
	public final @Override String getAuthorUserID() { return this.owner_id; }
	// --------------------------------------------------
	public final @Override Text getName() { return literal(this.full_name); }
	public final @Override Text getDescription() { return literal(this.description); }
	// --------------------------------------------------
	public final @Override Text[] getTags() { return this.topics; }
	// --------------------------------------------------
	public final @Override boolean hasIssues() { return this.has_issues; }
	public final @Override boolean hasForks() { return this.allow_forking; }
	public final @Override @Nullable Integer getOpenIssueCount() { return this.open_issues_count; }
	public final @Override Integer getForkCount() { return this.forks; }
	// --------------------------------------------------
	public final @Override Instant getCreatedTime() { return this.created_at; }
	public final @Override Instant getLastEditedTime() { return this.updated_at; }
	// ==================================================
	protected final @Override GitHubUserInfo fetchAuthorUserInfoSync() { return this.owner; }
	protected final @Override GitHubIssueInfo[] fetchIssuesSync(int perPage, int page)
			throws IllegalArgumentException, ClientProtocolException, URISyntaxException, IOException
	{
		//prepare arguments
		if(perPage < 1) throw new IllegalArgumentException("Cannot fetch '" + perPage + "' issues per page.");
		else if(page < 1) throw new IllegalArgumentException("Cannot fetch page '" + page + "'.");
		perPage = Math.min(Math.max(perPage, 1), 50);
		if(perPage * page > this.open_issues_count) return new GitHubIssueInfo[] {};
		
		//perform HTTP GET
		final var apiEndpoint = String.format(
				"https://api.github.com/repos/%s/issues?per_page=%d&page=%d",
				this.full_name,
				perPage,
				page);
		final var content = httpGetSync(apiEndpoint);
		
		//parse JSON
		try
		{
			final var resultJson = new Gson().fromJson(content, JsonArray.class);
			final var result = new ArrayList<GitHubIssueInfo>();
			resultJson.forEach(issueJson -> result.add(new GitHubIssueInfo(this, issueJson.getAsJsonObject())));
			return result.toArray(new GitHubIssueInfo[] {});
		}
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	// ==================================================
	/**
	 * Synchronously obtains information about a given GitHub repository, using GitHub's APIs.
	 * @param username The repository owner's unique username.
	 * @param repository The repository's unique name.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws URISyntaxException If an argument is not a valid URL component.
	 * @throws HttpResponseException If the API's response status code is not 200.
	 */
	public static final GitHubRepositoryInfo getRepositoryInfoSync(String username, String repository)
			throws NullPointerException, URISyntaxException, ClientProtocolException, IOException
	{
		//prepare and handle cache
		final var apiEndpoint = String.format(
				"https://api.github.com/repos/%s/%s",
				Objects.requireNonNull(username),
				Objects.requireNonNull(repository));
		final var cached = CACHE.getIfPresent(apiEndpoint);
		if(cached != null) return cached;
		
		//perform HTTP GET
		final var content = httpGetSync(apiEndpoint);
		
		//parse JSON
		try
		{
			final var result = new GitHubRepositoryInfo(new Gson().fromJson(content, JsonObject.class));
			CACHE.put(apiEndpoint, result);
			return result;
		}
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	// ==================================================
}
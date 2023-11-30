package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.httpGetSync;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfo;
import net.minecraft.text.Text;

public final class GitHubRepositoryInfo extends RepositoryInfo
{
	// ==================================================
	private GitHubUserInfo owner;
	// ---------------------------------------------------
	private String id;
	private String owner_id;
	private Text full_name, description;
	private Text[] topics;
	private boolean has_issues, allow_forking;
	private @Nullable Integer open_issues_count, forks;
	// ==================================================
	GitHubRepositoryInfo(JsonObject json)
	{
		this.owner = new GitHubUserInfo(json.get("owner").getAsJsonObject());
		
		this.id = Integer.toString(json.get("id").getAsInt());
		this.owner_id = this.owner.getID();
		this.full_name = literal(json.get("full_name").getAsString());
		this.description = literal(json.get("description").getAsString());
		List<Text> topics = new ArrayList<>();
		if(json.has("topics") && !json.get("topics").isJsonNull())
			json.get("topics").getAsJsonArray().forEach(topic -> topics.add(literal(topic.getAsString())));
		this.topics = topics.toArray(new Text[] {});
		this.has_issues = json.get("has_issues").getAsBoolean(); 
		this.allow_forking = json.get("allow_forking").getAsBoolean();
		if(this.has_issues)
			this.open_issues_count = json.get("open_issues_count").getAsInt();
		if(this.allow_forking)
			this.forks = json.get("forks").getAsInt();
	}
	// ==================================================
	public final @Override String getID() { return this.id; }
	public final @Override String getAuthorUserID() { return this.owner_id; }
	// --------------------------------------------------
	public final @Override Text getName() { return this.full_name; }
	public final @Override Text getDescription() { return this.description; }
	// --------------------------------------------------
	public final @Override Text[] getTags() { return this.topics; }
	// --------------------------------------------------
	public final @Override boolean hasIssues() { return this.has_issues; }
	public final @Override boolean hasForks() { return this.allow_forking; }
	public final @Override @Nullable Integer getOpenIssuesCount() { return this.open_issues_count; }
	public final @Override Integer getForkCount() { return this.forks; }
	// --------------------------------------------------
	protected final @Override GitHubUserInfo fetchAuthorUserInfoSync() { return this.owner; }
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
		//http get
		final var apiEndpoint = String.format(
				"https://api.github.com/repos/%s/%s",
				Objects.requireNonNull(username),
				Objects.requireNonNull(repository));
		final var content = httpGetSync(apiEndpoint);
		
		//parse JSON
		try { return new GitHubRepositoryInfo(new Gson().fromJson(content, JsonObject.class)); }
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	// ==================================================
}
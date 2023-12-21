package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.http.client.ClientProtocolException;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryIssueInfo;
import net.minecraft.text.Text;

/**
 * Represents a {@link RepositoryIssueInfo} for an issue posted on a GitHub repository.
 */
public final class GitHubIssueInfo extends RepositoryIssueInfo
{
	// ==================================================
	private final GitHubRepositoryInfo repository;
	// --------------------------------------------------
	private final String id;
	private final GitHubUserInfo user;
	private final long number;
	private final Text title;
	private final GitHubComment body;
	private final GitHubUserInfo[] assignees;
	private final String state; //"open" or "closed"
	private final Instant created_at, updated_at;
	private final int comments;
	// --------------------------------------------------
	private final boolean isClosed;
	// ==================================================
	GitHubIssueInfo(GitHubRepositoryInfo parentRepository, JsonObject json) throws NullPointerException
	{
		this.repository = Objects.requireNonNull(parentRepository);
		//
		this.id = Long.toString(json.get("id").getAsLong());
		this.cachedAuthorUserInfo = (this.user = new GitHubUserInfo(json.get("user").getAsJsonObject()));
		this.number = json.get("number").getAsLong();
		this.title = literal(json.get("title").getAsString());
		this.body = new GitHubComment(json);
		final var assignees = new ArrayList<GitHubUserInfo>();
		json.get("assignees").getAsJsonArray().forEach(
				assignee -> assignees.add(new GitHubUserInfo(assignee.getAsJsonObject())));
		this.assignees = assignees.toArray(new GitHubUserInfo[] {});
		this.state = json.get("state").getAsString();
		this.created_at = Instant.parse(json.get("created_at").getAsString());
		this.updated_at = Instant.parse(json.get("updated_at").getAsString());
		this.comments = json.get("comments").getAsInt();
		//
		this.isClosed = Objects.equals(this.state, "closed");
	}
	// ==================================================
	public final @Override GitHubRepositoryInfo getRepository() { return this.repository; }
	public final @Override String getID() { return this.id; }
	public final @Override String getAuthorUserID() { return this.user.getID(); }
	
	/**
	 * Returns the unique numerical GitHub issue number representing this issue.
	 */
	public final long getIssueNumber() { return this.number; }
	// --------------------------------------------------
	public final @Override Text getName() { return this.title; }
	public final @Override GitHubComment getBody() { return this.body; }
	// --------------------------------------------------
	public final @Override GitHubUserInfo[] getAssignees() { return this.assignees; }
	public final @Override boolean isClosed() { return this.isClosed; }
	// --------------------------------------------------
	public final @Override Instant getCreatedTime() { return this.created_at; }
	public final @Override Instant getLastEditedTime() { return this.updated_at; }
	// ==================================================
	protected final @Override GitHubUserInfo fetchAuthorUserInfoSync() { return this.user; }
	// --------------------------------------------------
	public final @Override boolean hasComments() { return true; }
	public final @Override Integer getCommentCount() { return this.comments; }
	protected final @Override GitHubComment[] fetchCommentsSync(int perPage, int page)
			throws IllegalArgumentException, ClientProtocolException, URISyntaxException, IOException
	{
		//prepare arguments
		if(perPage < 1) throw new IllegalArgumentException("Cannot fetch '" + perPage + "' comments per page.");
		else if(page < 1) throw new IllegalArgumentException("Cannot fetch page '" + page + "'.");
		perPage = Math.min(Math.max(perPage, 1), 50);
		if(perPage * page > this.comments) return new GitHubComment[] {};
		
		//perform HTTP GET
		final var apiEndpoint = String.format(
				"https://api.github.com/repos/%s/issues/%d/comments?per_page=%d&page=%d",
				this.repository.full_name,
				this.number,
				perPage,
				page);
		final String content = HttpUtils.httpGetSyncS(new URI(apiEndpoint));
		
		//parse JSON
		try
		{
			final var resultJson = new Gson().fromJson(content, JsonArray.class);
			final var result = new ArrayList<GitHubComment>();
			resultJson.forEach(issueJson -> result.add(new GitHubComment(issueJson.getAsJsonObject())));
			return result.toArray(new GitHubComment[] {});
		}
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	// ==================================================
	/**
	 * Represents an issue comment posted on GitHub.
	 */
	public static final class GitHubComment extends Comment
	{
		// ----------------------------------------------
		private final @Nullable String id;
		private final GitHubUserInfo user;
		private final String body;
		private final Instant created_at, updated_at;
		// ----------------------------------------------
		GitHubComment(JsonObject json) throws NullPointerException
		{
			this.id = Long.toString(json.get("id").getAsLong());
			this.cachedAuthorUserInfo = (this.user = new GitHubUserInfo(json.get("user").getAsJsonObject()));
			this.body = json.get("body").getAsString();
			this.created_at = Instant.parse(json.get("created_at").getAsString());
			this.updated_at = Instant.parse(json.get("updated_at").getAsString());
		}
		/*GitHubComment(GitHubUserInfo user, Instant timestamp, String body) throws NullPointerException
		{
			this.id = null;
			this.cachedAuthorUserInfo = (this.user = Objects.requireNonNull(user));
			this.body = Objects.requireNonNull(body);
			this.updated_at = (this.created_at = Objects.requireNonNull(timestamp));
		}*/
		// ----------------------------------------------
		public final @Nullable @Override String getID() { return this.id; }
		public final @Override String getAuthorUserID() { return this.user.getID(); }
		public final @Override String getRawBody() { return this.body; }
		// ----------------------------------------------
		public final @Override Instant getCreatedTime() { return this.created_at; }
		public final @Override Instant getLastEditedTime() { return this.updated_at; }
		// ----------------------------------------------
		protected final @Override GitHubUserInfo fetchAuthorUserInfoSync() { return this.user; }
		// ----------------------------------------------
	}
	// ==================================================
}
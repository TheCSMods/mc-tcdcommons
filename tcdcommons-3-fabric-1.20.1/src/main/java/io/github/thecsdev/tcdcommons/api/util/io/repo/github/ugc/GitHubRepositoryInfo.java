package io.github.thecsdev.tcdcommons.api.util.io.repo.github.ugc;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import io.github.thecsdev.tcdcommons.api.util.io.repo.github.GitHubHostInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;

/**
 * {@link RepositoryInfo} for GitHub repositories.
 */
public final class GitHubRepositoryInfo extends RepositoryInfo
{
	// ==================================================
	private final String owner_id;
	private final Instant created_at, updated_at;
	// --------------------------------------------------
	private final String id;
	private final String name;
	private final @Nullable String description;
	private final BigInteger open_issues_count, forks, stargazers_count;
	// ==================================================
	public GitHubRepositoryInfo(JsonObject repoData) throws NullPointerException, JsonIOException
	{
		Objects.requireNonNull(repoData);
		try
		{
			this.owner_id = repoData.get("owner").getAsJsonObject().get("id").getAsBigInteger().toString();
			this.created_at = Instant.parse(repoData.get("created_at").getAsString());
			this.updated_at = Instant.parse(repoData.get("updated_at").getAsString());
			
			this.id = repoData.get("id").getAsBigInteger().toString();
			this.name = Objects.requireNonNull(repoData.get("name").getAsString());
			
			if(repoData.has("description") && !repoData.get("description").isJsonNull())
				this.description = Objects.requireNonNull(repoData.get("description").getAsString());
			else this.description = null;
			
			this.open_issues_count = repoData.get("open_issues_count").getAsBigInteger();
			this.forks = repoData.get("forks").getAsBigInteger();
			this.stargazers_count = repoData.get("stargazers_count").getAsBigInteger();
		}
		catch(Exception exc) { throw new JsonIOException("Failed to read JSON repository data.", exc); }
	}
	// ==================================================
	public String getAuthorUserID() { return this.owner_id; }
	public Instant getCreatedTime() { return this.created_at; }
	public Instant getLastEditedTime() { return this.updated_at; }
	// --------------------------------------------------
	public final @Override GitHubHostInfo getHost() { return GitHubHostInfo.getInstance(); }
	public final @Override String getID() { return this.id; }
	// --------------------------------------------------
	public final @Override String getName() { return this.name; }
	public final @Nullable @Override String getDescription() { return this.description; }
	// --------------------------------------------------
	public final @Override BigInteger getOpenIssuesCount() { return this.open_issues_count; }
	public final @Override BigInteger getForkCount() { return this.forks; }
	public final @Override BigInteger getLikeCount() { return this.stargazers_count; }
	// ==================================================
}
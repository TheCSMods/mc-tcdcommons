package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;

/**
 * {@link RepositoryUserInfo} for users of GitHub.
 */
@Deprecated(since = "v3.12", forRemoval = true)
public final class GitHubUserInfo extends RepositoryUserInfo
{
	// ==================================================
	private final String id;    //user id
	private final String login; //account name
	private final String name;  //display name
	private final @Nullable String bio;
	private final @Nullable URI avatar_url;
	private final BigInteger followers, following, public_repos;
	// ==================================================
	public GitHubUserInfo(JsonObject userData) throws NullPointerException, JsonIOException
	{
		Objects.requireNonNull(userData);
		try
		{
			this.id = userData.get("id").getAsBigInteger().toString();
			this.login = Objects.requireNonNull(userData.get("login").getAsString());
			
			if(userData.has("name") && !userData.get("name").isJsonNull())
				this.name = Objects.requireNonNull(userData.get("name").getAsString());
			else this.name = this.login;
			
			if(userData.has("bio") && !userData.get("bio").isJsonNull())
				this.bio = Objects.requireNonNull(userData.get("bio").getAsString());
			else this.bio = null;
			
			if(userData.has("avatar_url") && !userData.get("avatar_url").isJsonNull())
				this.avatar_url = new URI(Objects.requireNonNull(userData.get("avatar_url").getAsString()));
			else this.avatar_url = null;
			
			this.followers = userData.get("followers").getAsBigInteger();
			this.following = userData.get("following").getAsBigInteger();
			this.public_repos = userData.get("public_repos").getAsBigInteger();
		}
		catch(Exception exc) { throw new JsonIOException("Failed to read JSON user data.", exc); }
	}
	// ==================================================
	public final @Override GitHubHostInfo getHost() { return GitHubHostInfo.getInstance(); }
	public final @Override String getID() { return this.id; }
	// --------------------------------------------------
	public final @Override String getAccountName() { return this.login; }
	public final @Override String getDisplayName() { return this.name; }
	public final @Nullable @Override String getBiography() { return this.bio; }
	// --------------------------------------------------
	public final @Nullable @Override URI getAvatarImageURI() { return this.avatar_url; }
	public final @Override BigInteger getFollowerCount() { return this.followers; }
	public final @Override BigInteger getFollowingCount() { return this.following; }
	public final @Override BigInteger getRepositoryCount() { return this.public_repos; }
	// ==================================================
}
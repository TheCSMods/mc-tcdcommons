package io.github.thecsdev.tcdcommons.api.util.io.repo;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Provides information about a repository hosting platform.
 */
@Deprecated(since = "v3.12", forRemoval = true)
public abstract class RepositoryHostInfo
{
	// ==================================================
	public @Override int hashCode() { return Objects.hash(getID()); }
	public @Override boolean equals(Object obj)
	{
		if(obj == null || !Objects.equals(getClass(), obj.getClass()))
			return false;
		else if(obj == this) return true;
		else return Objects.equals(getID(), ((RepositoryHostInfo)obj).getID());
	}
	// ==================================================
	/**
	 * Returns a {@link String} that represents the "unique identifier"
	 * for this repository hosting platform. By default, this is {@link URL#getHost()}.
	 * @apiNote If {@link #getURL()} does not have a "host" defined, then you must
	 * {@link Override} this and return a non-{@code null} value.
	 */
	public @Virtual String getID() { return Objects.requireNonNull(getURL().getHost()).toLowerCase(); }
	
	/**
	 * Returns the {@link URL} that represents where the hosting platform is located.
	 */
	public abstract URL getURL();
	
	/**
	 * Returns the {@link URL} that points to the repository host's APIs,
	 * or {@code null}, if the repository host does not have an API.
	 * @apiNote <b>Example 1:</b> <code>api.example.com</code>
	 * @apiNote <b>Example 2:</b> <code>example.com/api/v1</code>
	 */
	public abstract @Nullable URL getApiURL();
	// --------------------------------------------------
	/**
	 * Returns the user-friendly display name of the platform.
	 */
	public abstract String getDisplayName();
	// ==================================================
	/**
	 * Synchronously fetches {@link RepositoryUserInfo} about a given user, using the user's unique ID.
	 * @param userId The {@link String} representation of the user's unique ID.
	 * @see RepositoryUserInfo#getID()
	 * @apiNote It is highly advised to utilize the {@link CachedResourceManager} here.
	 */
	public abstract RepositoryUserInfo fetchUserInfoByIdSync(String userId)
			throws UnsupportedOperationException, NullPointerException, IOException;
	
	/**
	 * Synchronously fetches {@link RepositoryInfo} about a given repository, using the repository's unique ID.
	 * @param repoId The {@link String} representation of the repository's unique ID.
	 * @see RepositoryInfo#getID()
	 * @apiNote It is highly advised to utilize the {@link CachedResourceManager} here.
	 */
	public abstract RepositoryInfo fetchRepoInfoByIdSync(String repoId)
			throws UnsupportedOperationException, NullPointerException, IOException;
	// ==================================================
}
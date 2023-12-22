package io.github.thecsdev.tcdcommons.api.util.io.repo;

import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * Provides information about a given user whose account is registered on a repository hosting platform.
 * @see RepositoryHostInfo
 */
public abstract class RepositoryUserInfo
{
	// ==================================================
	/**
	 * Returns the {@link RepositoryHostInfo} about the host that hosts this user's account.
	 */
	public abstract RepositoryHostInfo getHost();
	
	/**
	 * Returns a {@link String} representation of the user's unique account identifier.
	 * @apiNote Not to be confused with the account's unique username.
	 */
	public abstract String getID();
	// --------------------------------------------------
	/**
	 * Returns the unique username of the account. This username has to
	 * be unique to this account on the entire repository platform.
	 */
	public abstract String getAccountName();
	
	/**
	 * Returns the "display name" for this account. Does not have to be "unique".
	 */
	public abstract String getDisplayName();
	
	/**
	 * Returns the user's "biography"/"about me" text, if there is one.
	 * May return {@code null} if the user doesn't have one.
	 */
	public abstract @Nullable String getBiography();
	// --------------------------------------------------
	/**
	 * Returns the {@link URI} that points to the resource that
	 * holds the user's avatar image. Could be a WWW URL or a file.
	 * May also be {@code null} if the user does not have an avatar image.
	 */
	public @Virtual @Nullable URI getAvatarImageURI() { return null; }
	
	/**
	 * Returns the number of "followers" this user has, or {@code null} if unsupported.
	 */
	public @Virtual @Nullable BigInteger getFollowerCount() { return null; }
	
	/**
	 * Returns the number of users this user "follows", or {@code null} if unsupported.
	 */
	public @Virtual @Nullable BigInteger getFollowingCount() { return null; }
	
	/**
	 * Returns the number of public repositories this user has, or {@code null} if unsupported.
	 */
	public @Virtual @Nullable BigInteger getRepositoryCount() { return null; }
	// ==================================================
	public @Override int hashCode() { return Objects.hash(getHost(), getID()); }
	public @Override boolean equals(Object obj)
	{
		if(obj == null || !Objects.equals(getClass(), obj.getClass()))
			return false;
		else if(obj == this) return true;
		
		final RepositoryUserInfo cObj = (RepositoryUserInfo)obj;
		return Objects.equals(getHost(), cObj.getHost()) &&
				Objects.equals(getID(), cObj.getID());
	}
	// ==================================================
}
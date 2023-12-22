package io.github.thecsdev.tcdcommons.api.util.io.repo.ugc;

import java.time.Instant;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryHostInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;

/**
 * Represents "user generated content" that is hosted on a repository hosting platform.
 * @see RepositoryUserInfo
 * @see RepositoryHostInfo
 */
public abstract class RepositoryUGC
{
	// ==================================================
	/**
	 * Returns the {@link RepositoryHostInfo} about the host that is
	 * hosting this user-generated content.
	 */
	public abstract RepositoryHostInfo getHost();
	
	/**
	 * A {@link String} representation of the unique ID that had been
	 * assigned to this user-generated content.
	 */
	public abstract String getID();
	
	/**
	 * A {@link String} representation of the unique ID of the author
	 * of this user-generated content.
	 * @see RepositoryUserInfo#getID()
	 */
	public abstract String getAuthorUserID();
	// --------------------------------------------------
	/**
	 * Returns an {@link Instant} representing the time at which
	 * this user-generated content had been created.
	 */
	public abstract Instant getCreatedTime();
	
	/**
	 * Returns an {@link Instant} representing the time at which
	 * this user-generated content was last edited/changed/modified.
	 */
	public abstract Instant getLastEditedTime();
	// ==================================================
	public @Override int hashCode() { return Objects.hash(getHost(), getID(), getAuthorUserID()); }
	public @Override boolean equals(Object obj)
	{
		if(obj == null || !Objects.equals(getClass(), obj.getClass()))
			return false;
		else if(obj == this) return true;
		
		final var cObj = (RepositoryUGC)obj;
		return Objects.equals(getHost(), cObj.getHost()) &&
				Objects.equals(getID(), cObj.getID()) &&
				Objects.equals(getAuthorUserID(), cObj.getAuthorUserID());
	}
	// ==================================================
}
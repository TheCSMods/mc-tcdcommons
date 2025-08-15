package io.github.thecsdev.tcdcommons.api.util.io.repo.ugc;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryHostInfo;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

@Deprecated(since = "v3.12", forRemoval = true)
public abstract class RepositoryInfo extends RepositoryUGC
{
	// ==================================================
	/**
	 * Returns the {@link RepositoryHostInfo} about the host that hosts this repository.
	 */
	public abstract RepositoryHostInfo getHost();
	
	/**
	 * Returns a {@link String} representation of this repository's unique identifier.
	 */
	public abstract String getID();
	// --------------------------------------------------
	/**
	 * Returns the name of this repository.
	 */
	public abstract String getName();
	
	/**
	 * Returns the description of this repository.
	 */
	public abstract @Nullable String getDescription();
	// --------------------------------------------------
	/**
	 * Returns the number of issues or bug reports that are
	 * currently "open" for this repository.
	 * @apiNote Returns {@code null} if unsupported.
	 */
	public abstract @Nullable BigInteger getOpenIssuesCount();
	
	/**
	 * Returns the number of times this repository had been forked.
	 * @apiNote Returns {@code null} if unsupported.
	 */
	public abstract @Nullable BigInteger getForkCount();
	
	/**
	 * Returns the number of times this repository had been liked/starred/favorited.
	 * @apiNote Returns {@code null} if unsupported.
	 */
	public abstract @Nullable BigInteger getLikeCount();
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.util.io.repo;

import java.util.concurrent.ExecutorService;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;

public abstract class RepositoryInfo
{
	// ==================================================
	protected static final ExecutorService SCHEDULER = RepositoryInfoProvider.SCHEDULER;
	// ==================================================
	/**
	 * A {@link String} representation of the unique ID assigned to the repository.<br/>
	 * May be {@code null} if the repository does not have a unique ID.
	 */
	public abstract @Nullable String getID();
	
	/**
	 * A {@link String} representation of the unique identifier of the user that
	 * owns this repository, if there is one.
	 * @apiNote Not to be confused with the user's unique username or account name!
	 * On platforms like GitHub, this is usually an {@link Integer}.
	 */
	public abstract @Nullable String getAuthorUserID();
	// --------------------------------------------------
	public abstract @Nullable Text getName();
	public abstract @Nullable Text getDescription();
	// --------------------------------------------------
	/**
	 * Represents an array of "tags" or "labels" or "topics" assigned to this repository.
	 * Intended to be a user-friendly/readable array of {@link Text} representing those "tags".<br/>
	 * May be {@code null} if the repository does not have those assigned to it.
	 */
	public abstract @Nullable Text[] getTags();
	// --------------------------------------------------
	/**
	 * Returns {@code true} if this repository supports and allows "issues" aka posting bug reports.
	 */
	public abstract boolean hasIssues();
	
	/**
	 * Returns {@code true} if this repository supports and allows being "forked".
	 */
	public abstract boolean hasForks();
	
	public abstract @Nullable Integer getOpenIssuesCount();
	public abstract @Nullable Integer getForkCount();
	// ==================================================
}
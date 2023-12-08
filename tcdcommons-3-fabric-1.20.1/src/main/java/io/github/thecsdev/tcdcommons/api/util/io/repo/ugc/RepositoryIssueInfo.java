package io.github.thecsdev.tcdcommons.api.util.io.repo.ugc;

import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.getInfoAsync;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;

public abstract class RepositoryIssueInfo extends RepositoryUGC
{
	// ==================================================
	/**
	 * Returns information about the repository this issue belongs to.
	 */
	public abstract RepositoryInfo getRepository();
	// --------------------------------------------------
	/**
	 * Returns the name of the issue, if there is one.
	 */
	public abstract @Nullable Text getName();
	
	/**
	 * Returns a {@link Comment} instance representing the "initial comment"/"description" of the issue.
	 */
	public abstract @Nullable Comment getBody();
	
	/**
	 * Returns an array of people that have been assigned to this issue,
	 * or an empty array if no people have been assigned.
	 */
	public abstract RepositoryUserInfo[] getAssignees();
	
	/**
	 * Returns {@code true} if this issue has been "closed"/"resolved".
	 */
	public abstract boolean isClosed();
	// --------------------------------------------------
	/**
	 * Returns {@code true} if this repository issue supports and allows "comments".
	 */
	public abstract boolean hasComments();
	
	public abstract @Nullable Integer getCommentCount();
	// ==================================================
	/**
	 * Asynchronously obtains an array of {@link Comment}s posted on this "repository issue".
	 * @param perPage How many {@link Comment}s will be fetched "per page".
	 * @param page The current "page" of {@link Comment}s that will be fetched.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 * @see #fetchCommentsSync(int, int)
	 */
	public final void getCommentsAsync(
			int perPage, int page,
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final Consumer<Comment[]> onReady,
			final Consumer<Exception> onError)
	{
		getInfoAsync(minecraftClientOrServer, onReady, onError, () -> fetchCommentsSync(perPage, page));
	}
	
	/**
	 * Synchronously obtains an array of {@link Comment}s posted on this "repository issue".
	 * @param perPage How many {@link Comment}s will be fetched "per page".
	 * @param page The current "page" of {@link Comment}s that will be fetched.
	 * @throws UnsupportedOperationException If this repository issue does not support fetching comments.
	 * @throws Exception If some other {@link Exception} takes place while fetching comments.
	 */
	protected abstract Comment[] fetchCommentsSync(int perPage, int page)
			throws UnsupportedOperationException, Exception;
	// ==================================================
	/**
	 * Refers to a "comment" posted to a repository's issue.
	 */
	public static abstract class Comment extends RepositoryUGC
	{
		/**
		 * Returns a "raw" {@link String} representation of the {@link Comment}'s contents.
		 */
		public abstract String getRawBody();
	}
	// ==================================================
}
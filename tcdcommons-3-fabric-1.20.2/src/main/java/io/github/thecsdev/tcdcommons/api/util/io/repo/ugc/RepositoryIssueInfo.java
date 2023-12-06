package io.github.thecsdev.tcdcommons.api.util.io.repo.ugc;

import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.SCHEDULER;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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
			final @Nullable Consumer<Comment[]> onReady,
			final @Nullable Consumer<Exception> onError)
	{
		//prepare
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		final AtomicReference<Comment[]> result = new AtomicReference<>(null);
		final AtomicReference<Exception> raisedException = new AtomicReference<Exception>(null);
		
		//execute thread task and perform the fetch
		SCHEDULER.submit(() ->
		{
			//handle fetching
			try { result.set(fetchCommentsSync(perPage, page)); }
			catch(Exception exc) { raisedException.set(exc); }
			
			//handle the results - must be done on the main thread
			minecraftClientOrServer.executeSync(() ->
			{
				//handle unsupported operation
				if(result.get() == null && raisedException.get() == null)
					raisedException.set(new UnsupportedOperationException());
				//handle any raised exceptions
				if(raisedException.get() != null)
					onError.accept(raisedException.get());
				//and finally, handle "on ready"
				else onReady.accept(result.get());
			});
		});
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
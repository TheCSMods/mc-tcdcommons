package io.github.thecsdev.tcdcommons.api.util.io.repo;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;

public abstract class RepositoryIssueInfo extends RepositoryUGC
{
	// ==================================================
	protected static final ExecutorService SCHEDULER = RepositoryInfoProvider.SCHEDULER;
	// ==================================================
	/**
	 * A {@link String} representation of the unique ID assigned to the issue.<br/>
	 * May be {@code null} if the issue does not have a unique ID.
	 */
	public abstract @Nullable String getID();
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
	 */
	protected @Virtual Comment[] fetchCommentsSync(int perPage, int page) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
	// ==================================================
	/**
	 * Refers to a "comment" posted to a repository's issue.
	 */
	public abstract class Comment extends RepositoryUGC
	{
		/**
		 * A {@link String} representation of the unique ID assigned to the comment.<br/>
		 * May be {@code null} if the comment does not have a unique ID.
		 */
		public abstract @Nullable String getID();
		
		/**
		 * Returns a "raw" {@link String} representation of the {@link Comment}'s contents.
		 */
		public abstract String getRawBody();
	}
	// ==================================================
}
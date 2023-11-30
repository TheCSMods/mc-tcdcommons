package io.github.thecsdev.tcdcommons.api.util.io.repo;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;

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
	/**
	 * Asynchronously fetches {@link RepositoryUserInfo} about the repository's author.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 */
	public final void getAutherUserInfoAsync(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final @Nullable Consumer<RepositoryUserInfo> onReady,
			final @Nullable Consumer<Exception> onError)
	{
		//prepare
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		final AtomicReference<RepositoryUserInfo> result = new AtomicReference<>(null);
		final AtomicReference<Exception> raisedException = new AtomicReference<Exception>(null);
		
		//execute thread task and perform the fetch
		SCHEDULER.submit(() ->
		{
			//handle the results - must be done on the main thread
			minecraftClientOrServer.executeSync(() ->
			{
				if(result.get() == null) raisedException.set(new UnsupportedOperationException());
				if(raisedException.get() != null) onError.accept(raisedException.get());
				else onReady.accept(result.get());
			});
		});
	}
	// --------------------------------------------------
	/**
	 * Synchronously fetches {@link RepositoryUserInfo} about the repository's author.
	 * @throws Exception A "catch-all" clause for any {@link Exception}s that may get raised in the process.
	 * @throws NullPointerException If {@link #getAuthorUserID()} returns {@code null}.
	 * @throws UnsupportedOperationException If this method is not implemented or the repository host does not support it.
	 * @see #getAuthorUserID()
	 */
	protected @Virtual RepositoryUserInfo fetchAuthorUserInfoSync()
			throws Exception, NullPointerException, UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
	// ==================================================
}
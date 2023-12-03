package io.github.thecsdev.tcdcommons.api.util.io.repo;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;

public abstract class RepositoryInfo extends RepositoryUGC
{
	// ==================================================
	protected static final ExecutorService SCHEDULER = RepositoryInfoProvider.SCHEDULER;
	// ==================================================
	/**
	 * A {@link String} representation of the unique ID assigned to the repository.<br/>
	 * May be {@code null} if the repository does not have a unique ID.
	 */
	public abstract @Nullable String getID();
	// --------------------------------------------------
	public abstract @Nullable Text getName();
	public abstract @Nullable Text getDescription();
	// --------------------------------------------------
	/**
	 * Represents an array of "tags" or "labels" or "topics" assigned to this repository.
	 * Intended to be a user-friendly/readable array of {@link Text}s representing those "tags".
	 * @apiNote If the repository host does not support "tags", or the repository itself does
	 * not have any "tags" assigned to it, then return an empty array.
	 */
	public abstract Text[] getTags();
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
	 * Asynchronously obtains an array of {@link RepositoryIssueInfo}s posted on this "repository".
	 * @param perPage How many {@link RepositoryIssueInfo}s will be fetched "per page".
	 * @param page The current "page" of {@link RepositoryIssueInfo}s that will be fetched.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 */
	public final void getIssuesAsync(
			int perPage, int page,
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final @Nullable Consumer<RepositoryIssueInfo[]> onReady,
			final @Nullable Consumer<Exception> onError)
	{
		//prepare
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		final AtomicReference<RepositoryIssueInfo[]> result = new AtomicReference<>(null);
		final AtomicReference<Exception> raisedException = new AtomicReference<Exception>(null);
		
		//execute thread task and perform the fetch
		SCHEDULER.submit(() ->
		{
			//handle fetching
			try { result.set(fetchIssuesSync(perPage, page)); }
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
	 * Synchronously obtains an array of {@link RepositoryIssueInfo}s posted on this "repository".
	 * @param perPage How many {@link RepositoryIssueInfo}s will be fetched "per page".
	 * @param page The current "page" of {@link RepositoryIssueInfo}s that will be fetched.
	 */
	protected @Virtual RepositoryIssueInfo[] fetchIssuesSync(int perPage, int page) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
	// ==================================================
}
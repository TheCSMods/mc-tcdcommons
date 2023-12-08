package io.github.thecsdev.tcdcommons.api.util.io.repo.ugc;

import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.getInfoAsync;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * Holds information about a given repository.
 */
public abstract class RepositoryInfo extends RepositoryUGC
{
	// ==================================================
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
	
	public abstract @Nullable Integer getOpenIssueCount();
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
			final Consumer<RepositoryIssueInfo[]> onReady,
			final Consumer<Exception> onError)
	{
		getInfoAsync(minecraftClientOrServer, onReady, onError, () -> fetchIssuesSync(perPage, page));
	}
	
	/**
	 * Synchronously obtains an array of {@link RepositoryIssueInfo}s posted on this "repository".
	 * @param perPage How many {@link RepositoryIssueInfo}s will be fetched "per page".
	 * @param page The current "page" of {@link RepositoryIssueInfo}s that will be fetched.
	 * @throws UnsupportedOperationException If this repository does not support fetching issues.
	 * @throws Exception If some other {@link Exception} takes place while fetching issues.
	 */
	protected abstract RepositoryIssueInfo[] fetchIssuesSync(int perPage, int page)
			throws UnsupportedOperationException, Exception;
	// ==================================================
}
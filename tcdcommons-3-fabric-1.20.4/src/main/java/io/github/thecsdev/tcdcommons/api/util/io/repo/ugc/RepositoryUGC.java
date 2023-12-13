package io.github.thecsdev.tcdcommons.api.util.io.repo.ugc;

import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.getInfoAsync;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryIssueInfo.Comment;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * Represents "user generated content" on a repository platform.
 * It could be a repository, or an issue, or a comment, and so on...
 */
public abstract class RepositoryUGC
{
	// ==================================================
	/**
	 * Stores the {@link RepositoryUGC}'s "cached" {@link RepositoryUserInfo}
	 * variable value, so {@link #fetchAuthorUserInfoSync()} doesn't have to
	 * be called every time the author's user info needs to be retrieved.
	 */
	protected @Nullable RepositoryUserInfo cachedAuthorUserInfo;
	// ==================================================
	public @Virtual @Override int hashCode()
	{
		return Objects.hash(
				getClass().getName(),
				getAuthorUserID(),
				getCreatedTime(),
				getLastEditedTime());
	}
	public @Virtual @Override boolean equals(Object obj)
	{
		if(obj == null || !Objects.equals(getClass(), obj.getClass())) return false;
		else if(obj == this) return true;
		final var ugc = (RepositoryUGC)obj;
		return Objects.equals(getAuthorUserID(), ugc.getAuthorUserID()) &&
				Objects.equals(getCreatedTime(), ugc.getCreatedTime()) &&
				Objects.equals(getLastEditedTime(), ugc.getLastEditedTime());
	}
	// ==================================================
	/**
	 * A {@link String} representation of the unique ID assigned to this component.<br/>
	 * May be {@code null} if this component does not have a unique ID.
	 */
	public abstract @Nullable String getID();
	
	/**
	 * A {@link String} representation of the unique identifier of the user that owns this component, if there is one.
	 * @apiNote Not to be confused with the user's unique username or account name!
	 * On platforms like GitHub, this is usually an {@link Integer}.
	 */
	public abstract @Nullable String getAuthorUserID();
	// --------------------------------------------------
	/**
	 * Returns an {@link Instant} representing the time at which
	 * this {@link Comment} was first posted.
	 */
	public abstract @Nullable Instant getCreatedTime();
	
	/**
	 * Returns an {@link Instant} representing the time at which
	 * this {@link Comment} was last "edited"/"changed"/"updated".
	 * @apiNote If this comment was never "edited", return {@link #getCreatedTime()}.
	 */
	public abstract @Nullable Instant getLastEditedTime();
	// ==================================================
	/**
	 * Asynchronously fetches {@link RepositoryUserInfo} about this component's author.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 */
	public final void getAutherUserInfoAsync(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final Consumer<RepositoryUserInfo> onReady,
			final Consumer<Exception> onError)
	{
		getInfoAsync(minecraftClientOrServer, onReady, onError, () ->
		{
			@Nullable RepositoryUserInfo antiDeadlockBczImTooParanoidLol = null;
			synchronized(RepositoryUGC.this.cachedAuthorUserInfo) { antiDeadlockBczImTooParanoidLol = RepositoryUGC.this.cachedAuthorUserInfo; }
			
			if(antiDeadlockBczImTooParanoidLol != null) return antiDeadlockBczImTooParanoidLol;
			else
			{
				antiDeadlockBczImTooParanoidLol = fetchAuthorUserInfoSync();
				synchronized(RepositoryUGC.this.cachedAuthorUserInfo) { RepositoryUGC.this.cachedAuthorUserInfo = antiDeadlockBczImTooParanoidLol; }
				return antiDeadlockBczImTooParanoidLol;
			}
		});
	}
	// --------------------------------------------------
	/**
	 * Synchronously fetches {@link RepositoryUserInfo} about this component's author.
	 * @throws Exception A "catch-all" clause for any {@link Exception}s that may get raised in the process.
	 * @throws NullPointerException If {@link #getAuthorUserID()} returns {@code null}.
	 * @throws UnsupportedOperationException If this method is not implemented or the repository host does not support it.
	 * @see #getAuthorUserID()
	 */
	protected abstract RepositoryUserInfo fetchAuthorUserInfoSync()
			throws Exception, NullPointerException, UnsupportedOperationException;
	// ==================================================
}
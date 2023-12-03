package io.github.thecsdev.tcdcommons.api.util.io.repo;

import static io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider.SCHEDULER;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
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
	/**
	 * A {@link String} representation of the unique identifier of the user that owns this component, if there is one.
	 * @apiNote Not to be confused with the user's unique username or account name!
	 * On platforms like GitHub, this is usually an {@link Integer}.
	 */
	public abstract @Nullable String getAuthorUserID();
	// --------------------------------------------------
	/**
	 * Asynchronously fetches {@link RepositoryUserInfo} about this component's author.
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
			//handle fetching
			if(RepositoryUGC.this.cachedAuthorUserInfo != null)
				result.set(RepositoryUGC.this.cachedAuthorUserInfo);
			else try { result.set(RepositoryUGC.this.cachedAuthorUserInfo = fetchAuthorUserInfoSync()); }
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
	// --------------------------------------------------
	/**
	 * Synchronously fetches {@link RepositoryUserInfo} about this component's author.
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
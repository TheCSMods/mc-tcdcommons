package io.github.thecsdev.tcdcommons.api.util.io.repo;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.REPO_INFO_PROVIDER;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.io.repo.github.GitHubRepositoryInfoProvider;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * An {@link Object} used for fetching data from remote
 * repositories over the HTTP network protocol.
 */
public abstract class RepositoryInfoProvider extends Object
{
	// ==================================================
	protected static final String THREAD_NAME =
			getModID() + ":" + RepositoryInfoProvider.class.getSimpleName().toLowerCase();
	
	/**
	 * An {@link ExecutorService} whose sole purpose is to perform {@link RepositoryInfo}
	 * fetching on separate {@link Thread}s, so as to avoid lag-spikes.
	 * @apiNote {@link Internal} use only. Do not use this yourself!
	 */
	public static final @Internal ExecutorService SCHEDULER = Executors.newCachedThreadPool(
			runnable ->
			{
				final var thread = new Thread(runnable, THREAD_NAME);
				thread.setDaemon(true);
				return thread;
			});
	// --------------------------------------------------
	static
	{
		//register repository info providers
		REPO_INFO_PROVIDER.register(new Identifier(getModID(), "github"), new GitHubRepositoryInfoProvider());
	}
	// ==================================================
	/**
	 * Performs an asynchronous "information obtaining operation" on a
	 * {@link RepositoryInfoProvider}'s {@link Thread}.
	 * @param <T> The generic type of the information that will be fetched.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * This will be used to invoke the "on ready" and "on error" {@link Consumer}s on the main {@link Thread}.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * Executes on the main {@link Thread}.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 * Executes on the main {@link Thread}.
	 * @param infoObtainer The {@link Callable} task that will be responsible for the "information fetching".
	 * Executes asynchronously on a {@link RepositoryInfoProvider}'s {@link Thread}.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final <T> void getInfoAsync(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final Consumer<T> onReady,
			final Consumer<Exception> onError,
			final Callable<T> infoObtainer) throws NullPointerException
	{
		//prepare
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		Objects.requireNonNull(infoObtainer);
		final AtomicReference<T> result = new AtomicReference<T>(null);
		final AtomicReference<Exception> raisedException = new AtomicReference<Exception>(null);
		
		//execute thread task and perform the fetch
		SCHEDULER.submit(() ->
		{
			//use try-catch to handle any raised exceptions
			try { result.set(infoObtainer.call()); }
			catch(Exception exc) { raisedException.set(exc); }
			
			//handle the results - must be done on the main thread
			minecraftClientOrServer.executeSync(() ->
			{
				//handle unsupported operation
				if(result.get() == null && raisedException.get() == null)
					raisedException.set(new UnsupportedOperationException("Information fetching returned null."));
				//handle any raised exceptions
				if(raisedException.get() != null)
					onError.accept(raisedException.get());
				//and finally, handle "on ready"
				else onReady.accept(result.get());
			});
		});
	}
	// ==================================================
	/**
	 * Asynchronously obtains information about a given remote repository.
	 * @param repoUrl The remote Git repository web URL.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 * @apiNote The repository info will be fetched asynchronously, on a separate {@link Thread}.
	 * Once fetching is complete or fails, the {@link Consumer}s will be invoked on the main {@link Thread}.
	 */
	public static final void getRepositoryInfoAsync(
			final String repoUrl,
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final Consumer<RepositoryInfo> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		getInfoAsync(minecraftClientOrServer, onReady, onError, () ->
		{
			//iterate all repository info providers, and attempt to find one that will work
			for(final var rip : REPO_INFO_PROVIDER)
			{
				//attempt to obtain repository info using the next provider
				try
				{
					final @Nullable var ri = rip.getValue().fetchRepositoryInfoSync(repoUrl);
					if(ri != null) return ri;
				}
				//skip UnsupportedRepositoryHostException-s
				catch(UnsupportedRepositoryHostException exc) { continue; }
			}
			
			//if nothing was found, return null
			return null;
		});
	}
	// --------------------------------------------------
	/**
	 * Fetches {@link RepositoryInfo} about a given remote repository, synchronously.
	 * @param repoUrl The remote Git repository web URL.
	 * @return {@link RepositoryInfo} if all goes well, or {@code null} if this
	 * {@link RepositoryInfoProvider} does not support the given repository host.
	 * @throws UnsupportedRepositoryHostException If the repository URL is not supported.
	 * @apiNote This method is invoked asynchronously, on a separate {@link Thread}.
	 * @apiNote It is your responsibility to handle {@link Exception}s and avoid raising them.
	 * If your {@link RepositoryInfoProvider} does not support the URL, either return {@code null},
	 * or {@code throw} {@link UnsupportedRepositoryHostException} instead.
	 * @apiNote Do not take too long to execute (few seconds), or else a {@link TimeoutException} may be raised.
	 */
	public abstract RepositoryInfo fetchRepositoryInfoSync(String repoUrl) throws UnsupportedRepositoryHostException;
	// ==================================================
}
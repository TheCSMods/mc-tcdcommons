package io.github.thecsdev.tcdcommons.api.util.io.repo;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.REPO_INFO_PROVIDER;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.io.repo.github.GitHubRepositoryInfoProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ReentrantThreadExecutor;

/**
 * An {@link Object} used for fetching data from remote
 * repositories over the HTTP network protocol.
 */
public abstract class RepositoryInfoProvider extends Object
{
	// ==================================================
	protected static final String THREAD_NAME = getModID() + ":repo_info_provider";
	protected static final ExecutorService SCHEDULER = Executors.newCachedThreadPool(
			runnable -> new Thread(runnable, THREAD_NAME));
	// --------------------------------------------------
	static
	{
		//register repository info providers
		REPO_INFO_PROVIDER.register(new Identifier(getModID(), "github"), new GitHubRepositoryInfoProvider());
	}
	// ==================================================
	/**
	 * Asynchronously obtains information about a given remote repository.
	 * @param minecraftClientOrServer An instance on the MinecraftClient or the MinecraftServer.
	 * @param repoUrl The remote Git repository web URL.
	 * @param onReady A {@link Consumer} that is invoked once the info is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the info fails.
	 * @apiNote The repository info will be fetched asynchronously, on a separate {@link Thread}.
	 * Once fetching is complete or fails, the {@link Consumer}s will be invoked on the main {@link Thread}.
	 */
	public static final void getRepositoryInfoAsync(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final String repoUrl,
			final @Nullable Consumer<RepositoryInfo> onReady,
			final @Nullable Consumer<Exception> onError) throws NullPointerException
	{
		//prepare
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(repoUrl);
		final AtomicReference<RepositoryInfo> result = new AtomicReference<>(null);
		final AtomicReference<Exception> raisedException = new AtomicReference<Exception>(null);
		
		//execute thread task and perform the fetch
		SCHEDULER.submit(() ->
		{
			//use try-catch to handle any raised exceptions
			try
			{
				//iterate all repository info providers, and attempt to find one that will work
				for(final var rip : REPO_INFO_PROVIDER)
				{
					//attempt to obtain repository info using the next provider
					try { result.set(rip.getValue().fetchRepositoryInfo(repoUrl)); }
					//skip UnsupportedRepositoryHostException-s
					catch(UnsupportedRepositoryHostException exc) { continue; }
					//if a value was obtained, break the loop
					if(rip.getValue() != null) break;
				}
			}
			catch(Exception exc) { raisedException.set(exc); }
			
			//handle the results - must be done on the main thread
			minecraftClientOrServer.executeSync(() ->
			{
				if(result.get() == null) raisedException.set(new UnsupportedRepositoryHostException(repoUrl));
				if(raisedException.get() != null) onError.accept(raisedException.get());
				else onReady.accept(result.get());
			});
		});
	}
	// ==================================================
	/**
	 * Fetches {@link RepositoryInfo} about a given remote repository.
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
	public abstract RepositoryInfo fetchRepositoryInfo(String repoUrl) throws UnsupportedRepositoryHostException;
	// ==================================================
}
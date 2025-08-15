package io.github.thecsdev.tcdcommons.api.util.io.repo;

import io.github.thecsdev.tcdcommons.api.registry.TRegistries;
import io.github.thecsdev.tcdcommons.api.util.io.repo.github.GitHubRepositoryInfoProvider;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.REPO_INFO_PROVIDER;

/**
 * A utility {@link Class} that parses repository {@link URI}s and
 * attempts to fetch information about said repositories.
 */
@Deprecated(since = "v3.12", forRemoval = true)
public abstract class RepositoryInfoProvider
{
	// ==================================================
	private static final String THREAD_NAME;
	private static final ExecutorService THREAD_SCHEDULER;
	// --------------------------------------------------
	static
	{
		//initialize static fields
		THREAD_NAME = getModID() + ":" + RepositoryInfoProvider.class.getSimpleName().toLowerCase();
		THREAD_SCHEDULER = Executors.newCachedThreadPool(task ->
		{
			final var thread = new Thread(task, THREAD_NAME);
			thread.setDaemon(true);
			return thread;
		});
		
		//register repository info providers
		REPO_INFO_PROVIDER.register(ResourceLocation.fromNamespaceAndPath(getModID(), "github"), GitHubRepositoryInfoProvider.getInstance());
	}
	// ==================================================
	static final @Internal <T> void getInfoAsync(
			BlockableEventLoop<?> minecraftClientOrServer,
			Consumer<T> onReady,
			Consumer<Exception> onError,
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
		THREAD_SCHEDULER.submit(() ->
		{
			//use try-catch to handle any raised exceptions
			try { result.set(infoObtainer.call()); }
			catch(Exception exc) { raisedException.set(exc); }
			catch(Error err) { minecraftClientOrServer.executeIfPossible(() -> { throw err; }); return; }
			
			//handle the results - must be done on the main thread
			minecraftClientOrServer.executeIfPossible(() ->
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
	// --------------------------------------------------
	/**
	 * Attempts to obtain {@link RepositoryInfo} about a given repository using its {@link URI}.
	 * @param repoUri The {@link URI} representing the repository.
	 * @param minecraftClientOrServer An instance of the {@code MinecraftClient} or the {@code MinecraftServer}.
	 * @param onReady A {@link Consumer} that is invoked if the fetching is successful.
	 * @param onError A {@link Consumer} that is invoked if the fetching fails.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @see TRegistries#REPO_INFO_PROVIDER
	 * @apiNote The {@link Consumer}s are invoked on the {@link BlockableEventLoop}'s main {@link Thread}.
	 * @apiNote The returned result may end up being {@code null}, if no registered
	 * {@link RepositoryInfoProvider}s return a value.
	 */
	public static final void getRepoInfoAsync(
			URI repoUri,
			BlockableEventLoop<?> minecraftClientOrServer,
			Consumer<@Nullable RepositoryInfo> onReady,
			Consumer<Exception> onError) throws NullPointerException
	{
		Objects.requireNonNull(repoUri);
		getInfoAsync(minecraftClientOrServer, onReady, onError, () ->
		{
			//synchronize the registry to prevent concurrent modifications
			synchronized(REPO_INFO_PROVIDER)
			{
				//iterate all registered providers
				for(final var entry : REPO_INFO_PROVIDER)
				{
					//attempt to fetch repository info from the provider
					final var rip = entry.getValue();
					RepositoryInfo ri = rip.fetchRepoInfoSync(repoUri);
					//only return if an info is fetched
					if(ri != null) return ri;
				}
				
				//if no providers return a value, return null
				return null;
			}
		});
	}
	
	/**
	 * Attempts to obtain {@link RepositoryUserInfo} about a given user using their {@link URI}.
	 * @param userUri The {@link URI} representing the user.
	 * @param minecraftClientOrServer An instance of the {@code MinecraftClient} or the {@code MinecraftServer}.
	 * @param onReady A {@link Consumer} that is invoked if the fetching is successful.
	 * @param onError A {@link Consumer} that is invoked if the fetching fails.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @see TRegistries#REPO_INFO_PROVIDER
	 * @apiNote The {@link Consumer}s are invoked on the {@link BlockableEventLoop}'s main {@link Thread}.
	 * @apiNote The returned result may end up being {@code null}, if no registered
	 * {@link RepositoryInfoProvider}s return a value.
	 */
	public static final void getUserInfoAsync(
			URI userUri,
			BlockableEventLoop<?> minecraftClientOrServer,
			Consumer<@Nullable RepositoryUserInfo> onReady,
			Consumer<Exception> onError) throws NullPointerException
	{
		Objects.requireNonNull(userUri);
		getInfoAsync(minecraftClientOrServer, onReady, onError, () ->
		{
			//synchronize the registry to prevent concurrent modifications
			synchronized(REPO_INFO_PROVIDER)
			{
				//iterate all registered providers
				for(final var entry : REPO_INFO_PROVIDER)
				{
					//attempt to fetch repository info from the provider
					final var rip = entry.getValue();
					RepositoryUserInfo ri = rip.fetchUserInfoSync(userUri);
					//only return if an info is fetched
					if(ri != null) return ri;
				}
				
				//if no providers return a value, return null
				return null;
			}
		});
	}
	// ==================================================
	/**
	 * Attempts to fetch information about a given repository using its {@link URI}.
	 * @param repoUri The repository {@link URI}.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IOException If an {@link IOException} is raised while fetching the info.
	 * @apiNote First determine the host using {@link URI#getHost()}. Then, if this
	 * {@link RepositoryInfoProvider} doesn't support the host, return {@code null}.
	 * Otherwise, make attempts to fetch the info. Should {@link Exception}s take place,
	 * it is preferable that they be {@link IOException}s.
	 */
	public abstract @Nullable RepositoryInfo fetchRepoInfoSync(URI repoUri)
			throws NullPointerException, IOException;
	
	/**
	 * Attempts to fetch information about a given user on a repository
	 * hosting platform, using the user's {@link URI}.
	 * @param userUri The {@link URI} of the user.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IOException If an {@link IOException} is raised while fetching the info.
	 * @apiNote First determine the host using {@link URI#getHost()}. Then, if this
	 * {@link RepositoryInfoProvider} doesn't support the host, return {@code null}.
	 * Otherwise, make attempts to fetch the info. Should {@link Exception}s take place,
	 * it is preferable that they be {@link IOException}s.
	 */
	public abstract @Nullable RepositoryUserInfo fetchUserInfoSync(URI userUri)
			throws NullPointerException, IOException;
	// ==================================================
	/**
	 * Returns {@link URI#getPath()} in form of a {@link String} array,
	 * with any "blank" parts filtered out, including the first part.
	 * If the {@link URI} does not have a path, an empty array will be returned.
	 * @param uri The {@link URI}.
	 */
	public static final String[] getUriPathEntries(URI uri) throws NullPointerException
	{
		//prepare
		Objects.requireNonNull(uri);
		final @Nullable var path = uri.getPath();
		if(path == null) return new String[] {};
		
		//do a split, filter the first empty one out, and return
		return Arrays.stream(path.split("/"))
				.filter(s -> !StringUtils.isAllBlank(s))
				.toArray(String[]::new);
	}
	// ==================================================
}
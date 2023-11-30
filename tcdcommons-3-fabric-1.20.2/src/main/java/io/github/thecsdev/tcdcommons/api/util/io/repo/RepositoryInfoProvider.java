package io.github.thecsdev.tcdcommons.api.util.io.repo;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.REPO_INFO_PROVIDER;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
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
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
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
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
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
					try { result.set(rip.getValue().fetchRepositoryInfoSync(repoUrl)); }
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
	// --------------------------------------------------
	/**
	 * Performs a synchronous HTTP GET request to an API endpoint.
	 * @param apiEndpoint The URL of the API endpoint, to which the request will be sent.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws URISyntaxException If an argument is not a valid URL component.
	 * @throws HttpResponseException If the API's response status code is not 200.
	 */
	@Experimental
	public static final @Internal String httpGetSync(String apiEndpoint)
			throws NullPointerException, URISyntaxException, ClientProtocolException, IOException
	{
		//prepare http get
		final var httpGet = new HttpGet(new URI(Objects.requireNonNull(apiEndpoint)));
		httpGet.addHeader("User-Agent", TCDCommons.getInstance().userAgent);
		
		final var reqConfig = RequestConfig.custom()
				.setSocketTimeout(3000)
				.setConnectTimeout(3000)
				.setConnectionRequestTimeout(3000)
				.build();
		final var httpClient = HttpClients.custom()
				.setDefaultRequestConfig(reqConfig)
				.build();
		
		//execute
		try
		{
			final var response = httpClient.execute(httpGet);
			final var responseEntity = response.getEntity();
			
			final var responseSL = response.getStatusLine();
			if(responseSL.getStatusCode() != 200)
				throw new HttpResponseException(responseSL.getStatusCode(), responseSL.getReasonPhrase());
			
			return EntityUtils.toString(responseEntity);
		}
		finally { httpClient.close(); }
	}
	// ==================================================
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
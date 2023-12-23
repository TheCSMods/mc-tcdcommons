package io.github.thecsdev.tcdcommons.api.util.io;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.io.IOException;
import java.lang.StackWalker.Option;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;

/**
 * Provides utilities for performing HTTP requests.
 * @apiNote Requires internet connection, and that this {@link Class} be enabled in the config.
 */
public final class HttpUtils
{
	// ==================================================
	private static final Logger LOGGER = LoggerFactory.getLogger(
			getModID() + ":" + HttpUtils.class.getSimpleName().toLowerCase());
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
	// --------------------------------------------------
	private HttpUtils() {}
	// ==================================================
	/**
	 * Synchronously performs an HTTP GET request to a given {@link URI} endpoint.
	 * @param endpoint The {@link URI} endpoint to send the request to.
	 * @param httpHeaders Any HTTP {@link Header}s you might wanna include in the request.
	 * @return The response from the server, in {@link String} format.
	 * @throws UnsupportedOperationException If {@link HttpUtils} is disabled in the {@link TCDCommons}'s config.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IOException If an {@link IOException} is raised while performing the request, or if the
	 * response code does not end up being '200'.
	 * @see #isEnabled()
	 * @apiNote No caching is performed here. It is advised to use the {@link CachedResourceManager} alongside this.
	 */
	public static final String httpGetSyncS(final URI endpoint, final Header... httpHeaders)
			throws UnsupportedOperationException, NullPointerException, IOException
	{
		return httpGetSync(String.class, STACK_WALKER.getCallerClass(), endpoint, httpHeaders);
	}
	
	/**
	 * Synchronously performs an HTTP GET request to a given {@link URI} endpoint.
	 * @param endpoint The {@link URI} endpoint to send the request to.
	 * @param httpHeaders Any HTTP {@link Header}s you might wanna include in the request.
	 * @return The response from the server, in a {@link Byte} array format.
	 * @throws UnsupportedOperationException If {@link HttpUtils} is disabled in the {@link TCDCommons}'s config.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IOException If an {@link IOException} is raised while performing the request, or if the
	 * response code does not end up being '200'.
	 * @see #isEnabled()
	 * @apiNote No caching is performed here. It is advised to use the {@link CachedResourceManager} alongside this.
	 */
	public static final byte[] httpGetSyncB(final URI endpoint, final Header... httpHeaders)
			throws UnsupportedOperationException, NullPointerException, IOException
	{
		return httpGetSync(byte[].class, STACK_WALKER.getCallerClass(), endpoint, httpHeaders);
	}
	
	/**
	 * An {@link Internal} method for performing HTTP GET requests.
	 * @param requestedType The requested response format. Either a {@link String} or a {@link Byte} array.
	 * @param requestee The {@link Class} that made this request.
	 * @param endpoint The {@link URI} endpoint to send the request to.
	 * @param httpHeaders The HTTP {@link Header}s to use in the request.
	 */
	@SuppressWarnings("unchecked")
	private static final @Internal <T> T httpGetSync(
			Class<T> requestedType,
			Class<?> requestee,
			final URI endpoint,
			final Header... httpHeaders) throws UnsupportedOperationException, NullPointerException, IOException
	{
		//check if allowed to make http requests
		assertEnabled();
		final String requesteeName = requestee.getName();
		
		//prepare http get
		final var httpGet = new HttpGet(Objects.requireNonNull(endpoint));
		for(final var header : Objects.requireNonNull(httpHeaders))
			httpGet.addHeader(Objects.requireNonNull(header));
		httpGet.addHeader("User-Agent", TCDCommons.getInstance().userAgent);
		httpGet.addHeader("x-" + getModID() + "-requestee", requesteeName);
		
		final var reqConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(3000)
				.setConnectTimeout(3000)
				.setSocketTimeout(5000)
				.build();
		final var httpClient = HttpClients.custom()
				.setDefaultRequestConfig(reqConfig)
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build();
		
		//execute
		final AtomicBoolean success = new AtomicBoolean(false);
		try
		{
			final var response = httpClient.execute(httpGet);
			final var responseEntity = response.getEntity();
			
			final var responseSL = response.getStatusLine();
			if(responseSL.getStatusCode() != 200)
				throw new HttpResponseException(responseSL.getStatusCode(), responseSL.getReasonPhrase());
			success.set(true);
			
			if(Objects.equals(requestedType, String.class))
				return (T) EntityUtils.toString(responseEntity);
			else if(Objects.equals(requestedType, byte[].class))
				return (T) EntityUtils.toByteArray(responseEntity);
			else throw new UnsupportedOperationException(
					"Unsupported resource type '" + requestedType +
					"'; Also you're wasting bandwidth and RAM because of this!");
		}
		finally
		{
			LOGGER.info(String.format("HTTP GET '%s'; Requested by '%s'; Success '%s'.",
					endpoint.toString(),
					requesteeName,
					Boolean.toString(success.get())));
			HttpClientUtils.closeQuietly(httpClient);
		}
	}
	// ==================================================
	/**
	 * Returns {@code true} if the {@link TCDCommons}'s config enables {@link HttpUtils}.
	 */
	public static final boolean isEnabled()
	{
		final var tcdc = TCDCommons.getInstance();
		if(tcdc == null) return false;
		return tcdc.getConfig().enableHttpUtils;
	}
	// --------------------------------------------------
	/**
	 * Ensures {@link HttpUtils} have been enabled via the {@link TCDCommons}'s config file.
	 */
	private static final @Internal void assertEnabled() throws UnsupportedOperationException
	{
		final var tcdc = TCDCommons.getInstance();
		if(tcdc == null) throw new UnsupportedOperationException("API not initialized yet.");
		else if(!tcdc.getConfig().enableHttpUtils)
			throw new UnsupportedOperationException(String.format(
					"%s has been disabled via the '%s' config file.",
					HttpUtils.class.getSimpleName(),
					getModID()));
	}
	// ==================================================
}
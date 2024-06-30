package io.github.thecsdev.tcdcommons.api.util.io;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.StackWalker.Option;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.annotations.CallerSensitive;
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
	@Deprecated(since = "v3.12", forRemoval = true)
	@CallerSensitive
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
	@Deprecated(since = "v3.12", forRemoval = true)
	@CallerSensitive
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
	@Deprecated(since = "v3.12", forRemoval = true)
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
				.setConnectionRequestTimeout(5000)  //waiting in the connection pool
				.setConnectTimeout(3000)            //waiting for connection to the server
				.setSocketTimeout(10000)            //waiting for a response from the server
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
	/**
	 * Same as {@link #fetch(String, FetchOptions, Class)}, but with default {@link FetchOptions}.
	 * @param url The {@link String} representation of the endpoint that will accept the request.
	 */
	public static final CloseableHttpResponse fetchSync(String url)
			throws NullPointerException, UnsupportedOperationException, URISyntaxException,
			ClientProtocolException, IOException
	{
		return fetch(url, new FetchOptions() {}, STACK_WALKER.getCallerClass());
	}
	
	/**
	 * Synchronously performs an HTTP request to a given endpoint.
	 * @param url The {@link String} representation of the endpoint that will accept the request.
	 * @param options The {@link FetchOptions} containing information about the request.
	 */
	@CallerSensitive
	public static final CloseableHttpResponse fetchSync(String url, FetchOptions options)
			throws NullPointerException, UnsupportedOperationException, URISyntaxException,
			ClientProtocolException, IOException
	{
		return fetch(url, options, STACK_WALKER.getCallerClass());
	}
	
	private static final CloseableHttpResponse fetch(String url, FetchOptions options, Class<?> requestee)
			throws NullPointerException, UnsupportedOperationException, URISyntaxException,
			ClientProtocolException, IOException
	{
		//prepare
		assertEnabled();
		Objects.requireNonNull(url);
		Objects.requireNonNull(options);
		final String httpMethod = options.method();
		Objects.requireNonNull(httpMethod);
		
		//perform the operation
		final var method = httpMethod.toUpperCase(Locale.ENGLISH).trim();
		final var result = switch(method)
		{
			case "GET"     -> fetch_get    (url, options, requestee);
			case "HEAD"    -> fetch_head   (url, options, requestee);
			case "POST"    -> fetch_post   (url, options, requestee);
			case "PUT"     -> fetch_put    (url, options, requestee);
			case "DELETE"  -> fetch_delete (url, options, requestee);
			case "OPTIONS" -> fetch_options(url, options, requestee);
			case "TRACE"   -> fetch_trace  (url, options, requestee);
			case "PATCH"   -> fetch_patch  (url, options, requestee);
			default        -> throw new UnsupportedOperationException("HTTP " + httpMethod);
		};
		final long contentLength = result.getEntity() != null ? result.getEntity().getContentLength() : 0;
		LOGGER.info("HTTP " + method + " " + url + " | Response: HTTP " +
				result.getStatusLine().getStatusCode() + " " + result.getStatusLine().getReasonPhrase() + " | " +
				"Content-Length: " + contentLength +
				" | Requested by: " + requestee.getName());
		if(contentLength > 100000000)
		{
			IOUtils.closeQuietly(result);
			throw new IOException("Response 'Content-Length' is too large!");
		}
		return result;
	}
	
	private static final CloseableHttpResponse fetch_get(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//validity check
		if(options.body() != null)
			throw new UnsupportedOperationException("HTTP GET does not support having a request body.");
		
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpGet(new URI(url));
		
		//handle http headers, and execute the request
		fetch_setHeaders(request, options, requestee);
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_head(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//validity check
		if(options.body() != null)
			throw new UnsupportedOperationException("HTTP HEAD does not support having a request body.");
		
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpHead(new URI(url));
		
		//handle http headers, and execute the request
		fetch_setHeaders(request, options, requestee);
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_post(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpPost(new URI(url));
		
		//handle http headers, and handle the body
		fetch_setHeaders(request, options, requestee);
		final @Nullable var body = fetch_bodyToEntity(options.body());
		if(body != null) request.setEntity(body);
		
		//execute the request
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_put(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpPut(new URI(url));
		
		//handle http headers, and handle the body
		fetch_setHeaders(request, options, requestee);
		final @Nullable var body = fetch_bodyToEntity(options.body());
		if(body != null) request.setEntity(body);
		
		//execute the request
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_delete(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//validity check
		if(options.body() != null)
			throw new UnsupportedOperationException("HTTP DELETE does not support having a request body.");
		
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpDelete(new URI(url));
		
		//handle http headers, and execute the request
		fetch_setHeaders(request, options, requestee);
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_options(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//validity check
		if(options.body() != null)
			throw new UnsupportedOperationException("HTTP OPTIONS does not support having a request body.");
		
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpOptions(new URI(url));
		
		//handle http headers, and execute the request
		fetch_setHeaders(request, options, requestee);
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_trace(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//validity check
		if(options.body() != null)
			throw new UnsupportedOperationException("HTTP TRACE does not support having a request body.");
		
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpTrace(new URI(url));
		
		//handle http headers, and execute the request
		fetch_setHeaders(request, options, requestee);
		return client.execute(request);
	}
	
	private static final CloseableHttpResponse fetch_patch(String url, FetchOptions options, Class<?> requestee)
			throws URISyntaxException, ClientProtocolException, IOException
	{
		//prepare
		final var client  = HttpClients.createDefault();
		final var request = new HttpPatch(new URI(url));
		
		//handle http headers, and handle the body
		fetch_setHeaders(request, options, requestee);
		final @Nullable var body = fetch_bodyToEntity(options.body());
		if(body != null) request.setEntity(body);
		
		//execute the request
		return client.execute(request);
	}
	// -------
	private static final void fetch_setHeaders(AbstractHttpMessage request, FetchOptions options, Class<?> requestee)
	{
		request.setHeader("User-Agent",             TCDCommons.getInstance().userAgent);
		request.setHeader("x-tcdcommons-requestee", requestee.getName());
		
		final @Nullable var headers = options.headers();
		if(headers != null)
			for(final var header : headers)
				request.setHeader(header);
	}
	
	private static final @Nullable HttpEntity fetch_bodyToEntity(@Nullable Object body) throws UnsupportedEncodingException
	{
		if(body == null)
			return null;
		else if(body instanceof HttpEntity bodyEntity)
			return bodyEntity;
		else if(body instanceof JsonElement jsEl)
			return new StringEntity(jsEl.toString(), ContentType.APPLICATION_JSON);
		else if(body instanceof Boolean bodyBool)
			return new StringEntity(Boolean.toString(bodyBool).toLowerCase(Locale.ENGLISH), (ContentType)null);
		else if(body instanceof String || ClassUtils.isPrimitiveOrWrapper(body.getClass()))
			return new StringEntity(Objects.toString(body), (ContentType)null);
		else throw new IllegalArgumentException("Unsupported HTTP body type: " + body.getClass().getName());
	}
	// --------------------------------------------------
	/**
	 * Contains settings that defines the behavior of {@link HttpUtils#fetchSync(String, FetchOptions)}.
	 */
	public static interface FetchOptions
	{
		/**
		 * The HTTP method to perform. Defaults to "GET".
		 * @apiNote Must <b>not</b> be {@code null}!
		 */
		default String method() { return "GET"; }
		
		/**
		 * An array of {@link Header}s to include in the HTTP request.
		 * Defaults to {@code null}.
		 */
		default @Nullable Header[] headers() { return null; }
		
		/**
		 * The body to include in the HTTP request. Defaults to {@code null}.
		 * 
		 * @apiNote It is recommended to provide an {@link HttpEntity} as the body.
		 * 
		 * @apiNote Supports {@code null}, {@link HttpEntity}s, primitive types, and {@link JsonElement}s.
		 * All other types will throw an {@link UnsupportedOperationException}.
		 * 
		 * @apiNote Not all HTTP methods support having a request body.
		 * Attempting to provide a body while using such methods will
		 * result in an {@link UnsupportedOperationException} being thrown.
		 */
		default @Nullable Object body() { return null; }
	}
	// ==================================================
}
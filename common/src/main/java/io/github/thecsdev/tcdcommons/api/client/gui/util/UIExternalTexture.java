package io.github.thecsdev.tcdcommons.api.client.gui.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorNativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext.TEXTURE_ICONS;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

/**
 * A {@link UITexture} that is loaded from an external
 * source outside of the vanilla game's resource manager.
 * @see {@link UIExternalTexture#UIExternalTexture(NativeImage)}
 */
public final class UIExternalTexture extends UITexture implements Closeable
{
	// ==================================================
	static final String THREAD_NAME = getModID() + ":" + UIExternalTexture.class.getSimpleName().toLowerCase();
	static final @Internal ExecutorService SCHEDULER = Executors.newCachedThreadPool(
			runnable ->
			{
				final var thread = new Thread(runnable, THREAD_NAME);
				thread.setDaemon(true);
				return thread;
			});
	// --------------------------------------------------
	/**
	 * A {@link UITexture} typically used when loading an external texture
	 * results in an {@link Exception} being raised.
	 */
	public static final UITexture FALLBACK_TEXTURE;
	
	/**
	 * Holds cached {@link UIExternalTexture}s obtained from
	 * remote resources such as the world-wide-web.
	 */
	@Deprecated(since = "3.7", forRemoval = true)
	public static final Cache<Object, UIExternalTexture> TEXTURE_CACHE;
	// ==================================================
	private boolean isClosed = false;
	// --------------------------------------------------
	private final TextureManager textureManager;
	private final NativeImage nativeImage;
	private final DynamicTexture nativeImageBackedTexture;
	// ==================================================
	static
	{
		FALLBACK_TEXTURE = new UITexture(TEXTURE_ICONS, new Rectangle(0, 0, 64, 64));
		TEXTURE_CACHE = CacheBuilder.newBuilder()
				.expireAfterWrite(10, TimeUnit.MINUTES) //minimize bandwidth and ram usage
				.expireAfterAccess(10, TimeUnit.MINUTES) //minimize bandwidth and ram usage
				.maximumSize(32) //minimize memory usage
				.build();
		TaskScheduler.schedulePeriodicCacheCleanup(TEXTURE_CACHE);
	}
	
	/**
	 * Creates a {@link UIExternalTexture} instance using a {@link NativeImage} that
	 * has been loaded from an external source.
	 * @throws NullPointerException When the argument is {@code null}.
	 * @throws IllegalStateException If this constructor is executed "off-thread".
	 * @see NativeImage#read(InputStream)
	 * @apiNote This operation is synchronous, and must be performed on the main thread.
	 * The game will raise an {@link Exception} if done "off-thread".
	 * @apiNote For performance reasons, it is recommended that the {@link NativeImage}
	 * be loaded "off-thread", and then this constructor be called on the main thread.
	 * @apiNote {@link NativeImage}s currently only support the "png" format (as of `1.20.2`).
	 */
	public UIExternalTexture(NativeImage image) throws NullPointerException, IllegalStateException
	{
		super(generateTextureIdentifier());
		
		this.nativeImage = Objects.requireNonNull(image);
		this.nativeImageBackedTexture = new DynamicTexture(() -> getTextureID().toString(), this.nativeImage);
		
		this.textureManager = MC_CLIENT.getTextureManager();
		this.textureManager.register(getTextureID(), this.nativeImageBackedTexture);
	}
	// --------------------------------------------------
	public final @Override void close()
	{
		//handle the closing flag
		if(this.isClosed) return;
		this.isClosed = true;
		
		//destroy texture and release the image
		this.textureManager.release(getTextureID());
		this.nativeImage.close();
	}
	@SuppressWarnings("removal")
	protected final @Override void finalize() throws Throwable { try { close(); } finally { super.finalize(); } }
	// ==================================================
	/**
	 * Returns {@code true} if {@link #close()} was called on this {@link UIExternalTexture}.
	 */
	public final boolean isClosed() { return this.isClosed; }
	
	/**
	 * Returns the width of the {@link NativeImage}.
	 * @see NativeImage#getWidth()
	 * @apiNote Not to be confused with {@link #getTextureSize()}, which serves a different purpose!
	 */
	public final int getNativeWidth() { return this.nativeImage.getWidth(); }
	
	/**
	 * Returns the height of the {@link NativeImage}.
	 * @see NativeImage#getHeight()
	 * @apiNote Not to be confused with {@link #getTextureSize()}, which serves a different purpose!
	 */
	public final int getNativeHeight() { return this.nativeImage.getHeight(); }
	
	/**
	 * Returns the color of a given pixel on the {@link NativeImage}.
	 * @param x The X coordinate of the target pixel.
	 * @param y The Y coordinate of the target pixel.
	 * @throws IllegalArgumentException If the pixel coordinates are "out of bounds".
	 * @see NativeImage#getPixelABGR(int, int)
	 * @see #getNativeWidth()
	 * @see #getNativeHeight()
	 */
	public final int getNativeColor(int x, int y) throws IllegalArgumentException
	{
		return ((AccessorNativeImage)(Object)this.nativeImage).tcdcommons_getColor(x, y);
	}
	// ==================================================
	private static long nextTexId = 0;
	/**
	 * Generates a new unique {@link ResourceLocation} for a new texture.
	 */
	private static final ResourceLocation generateTextureIdentifier()
	{
		nextTexId++;
		final var uuid = UUID.randomUUID();
		final var uid = String.format("%s_%s_%s_%s",
				Long.toString(System.currentTimeMillis()),
				Long.toString(nextTexId),
				Long.toString(uuid.getMostSignificantBits()),
				Long.toString(uuid.getLeastSignificantBits()));
		return ResourceLocation.fromNamespaceAndPath(getModID(), UIExternalTexture.class.getSimpleName().toLowerCase(Locale.ENGLISH) + "/" + uid);
	}
	// --------------------------------------------------
	/**
	 * Asynchronously loads a {@link UIExternalTexture} using a {@link Byte} array.
	 * @param pngBytes The image's byte data. Must be in "png" format.
	 * @param onReady A {@link Consumer} that is invoked on successful load.
	 * @param onError A {@link Consumer} that is invoked if an {@link Exception} is raised.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @apiNote The {@link Consumer}s are invoked on the {@link Minecraft}'s main {@link Thread}.
	 */
	@Experimental
	public static final void loadTextureAsync(
			final byte[] pngBytes,
			final Consumer<UIExternalTexture> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		//prepare
		Objects.requireNonNull(pngBytes);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		
		//execute
		SCHEDULER.submit(() ->
		{
			//check if there are existing cached versions
			final @Nullable var existing = TEXTURE_CACHE.getIfPresent(pngBytes);
			if(existing != null)
			{
				MC_CLIENT.executeIfPossible(() -> onReady.accept(existing));
				return;
			}
			
			//try to load a new version if no cached one exists
			try
			{
				final var nImage = NativeImage.read(pngBytes);
				MC_CLIENT.executeIfPossible(() ->
				{
					final var eTex = new UIExternalTexture(nImage);
					TEXTURE_CACHE.put(pngBytes, eTex);
					onReady.accept(eTex);
				});
			}
			catch(Exception e) { MC_CLIENT.executeIfPossible(() -> onError.accept(e)); }
		});
	}
	
	/**
	 * Asynchronously loads a "png" image hosted on the WWW.
	 * @param textureUrl The {@link URL} that points to the "png" image.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the image is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the image fails.
	 * @throws NullPointerException If a non-{@link Nullable} argument is null.
	 * @apiNote For obvious reasons, requires internet connection.
	 * @apiNote {@link Deprecated} because it does not handle caching properly!
	 */
	@Deprecated(since = "3.7", forRemoval = true)
	public static final void loadTextureAsync(
			final URL textureUrl,
			final ReentrantBlockableEventLoop<?> minecraftClientOrServer,
			final Consumer<UIExternalTexture> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		//prepare
		Objects.requireNonNull(textureUrl);
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		
		//handle cache
		final String cacheKey = textureUrl.toString().toLowerCase();
		final var cached = TEXTURE_CACHE.getIfPresent(cacheKey);
		if(cached != null && !cached.isClosed())
		{
			minecraftClientOrServer.executeIfPossible(() -> onReady.accept(cached));
			return;
		}
		
		//execute
		final Callable<NativeImage> nis = () ->
		{
			//perform the http get request
			final byte[] pngEntity = HttpUtils.httpGetSyncB(
					new URI(textureUrl.toString()),
					new BasicHeader("Accept", "image/png"));
			return NativeImage.read(pngEntity);
		};
		loadTextureAsync(
				nis,
				minecraftClientOrServer,
				tex -> { TEXTURE_CACHE.put(cacheKey, tex); onReady.accept(tex); },
				onError);
	}
	
	/**
	 * Asynchronously loads a "png" image using the {@link Callable} "{@link NativeImage} supplier".
	 * @param nativeImageSupplier Use this to load the {@link NativeImage}. Executes asynchronously.
	 * @param minecraftClientOrServer An instance of the current MinecraftClient or the MinecraftServer.
	 * @param onReady A {@link Consumer} that is invoked once the image is successfully obtained.
	 * @param onError A {@link Consumer} that is invoked in the event fetching the image fails.
	 * @throws NullPointerException If a non-{@link Nullable} argument is null.
	 * @apiNote This method does not automatically cache {@link UIExternalTexture}s!
	 */
	@Deprecated(since = "3.7", forRemoval = true)
	public static final void loadTextureAsync(
			final Callable<NativeImage> nativeImageSupplier,
			final ReentrantBlockableEventLoop<?> minecraftClientOrServer,
			final Consumer<UIExternalTexture> onReady,
			final Consumer<Exception> onError) throws NullPointerException
	{
		//prepare
		Objects.requireNonNull(nativeImageSupplier);
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(onReady);
		Objects.requireNonNull(onError);
		final AtomicReference<UIExternalTexture> result = new AtomicReference<>(null);
		final AtomicReference<Exception> raisedException = new AtomicReference<Exception>(null);
		
		//execute thread task and perform the fetch
		SCHEDULER.submit(() ->
		{
			//fetch and load the image "off-thread"
			@Nullable NativeImage nativeImage = null;
			try { nativeImage = nativeImageSupplier.call(); } catch(Exception exc) { raisedException.set(exc); }
			final @Nullable var finalImage = nativeImage;
			
			//handle the results - must be done on the main thread
			minecraftClientOrServer.executeIfPossible(() ->
			{
				//create texture instance if everything went well
				if(finalImage != null) result.set(new UIExternalTexture(finalImage));
				
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
	// ==================================================
}
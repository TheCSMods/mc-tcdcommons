package io.github.thecsdev.tcdcommons.api.client.gui.util;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

/**
 * A {@link UITexture} that is loaded from an external
 * source outside of the vanilla game's resource manager.
 * @see {@link UIExternalTexture#UIExternalTexture(NativeImage)}
 */
public final class UIExternalTexture extends UITexture implements Closeable
{
	// ==================================================
	private boolean isClosed = false;
	// --------------------------------------------------
	private final TextureManager textureManager;
	private final NativeImage nativeImage;
	private final NativeImageBackedTexture nativeImageBackedTexture;
	// ==================================================
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
		RenderSystem.assertOnGameThreadOrInit(); //enforced by the game anyways...
		
		this.nativeImage = Objects.requireNonNull(image);
		this.nativeImageBackedTexture = new NativeImageBackedTexture(this.nativeImage);
		
		this.textureManager = MC_CLIENT.getTextureManager();
		this.textureManager.registerTexture(getTextureID(), this.nativeImageBackedTexture);
	}
	// --------------------------------------------------
	public final @Override void close()
	{
		//handle the closing flag
		if(this.isClosed) return;
		this.isClosed = true;
		
		//destroy texture and release the image
		this.textureManager.destroyTexture(getTextureID());
		this.nativeImage.close();
	}
	@SuppressWarnings("deprecation")
	protected final @Override void finalize() throws Throwable { try { close(); } finally { super.finalize(); } }
	// ==================================================
	/**
	 * Returns {@code true} if {@link #close()} was called on this {@link UIExternalTexture}.
	 */
	public final boolean isClosed() { return this.isClosed; }
	
	/**
	 * Returns the width of the texture, in pixels.
	 * @see NativeImage#getWidth()
	 */
	public final int getWidth() { return this.nativeImage.getWidth(); }
	
	/**
	 * Returns the height of the texture, in pixels.
	 * @see NativeImage#getHeight()
	 */
	public final int getHeight() { return this.nativeImage.getHeight(); }
	
	/**
	 * Returns the color of a given pixel on the image.
	 * @param x The X coordinate of the target pixel.
	 * @param y The Y coordinate of the target pixel.
	 * @throws IllegalArgumentException If the pixel coordinates are "out of bounds".
	 * @see NativeImage#getColor(int, int)
	 * @see #getWidth()
	 * @see #getHeight()
	 */
	public final int getColor(int x, int y) throws IllegalArgumentException { return this.nativeImage.getColor(x, y); }
	// ==================================================
	private static long nextTexId = 0;
	/**
	 * Generates a new unique {@link Identifier} for a new texture.
	 */
	private static final Identifier generateTextureIdentifier()
	{
		nextTexId++;
		final var uuid = UUID.randomUUID();
		final var uid = String.format("%s_%s_%s_%s",
				Long.toString(System.currentTimeMillis()),
				Long.toString(nextTexId),
				Long.toString(uuid.getMostSignificantBits()),
				Long.toString(uuid.getLeastSignificantBits()));
		return new Identifier(getModID(), UIExternalTexture.class.getSimpleName().toLowerCase() + "/" + uid);
	}
	// ==================================================
}
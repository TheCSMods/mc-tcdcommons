package io.github.thecsdev.tcdcommons.api.client.gui.util;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

/**
 * A {@link UITexture} that is loaded from an external
 * source outside of the vanilla game's resource manager.
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
	public UIExternalTexture(File pngFile) throws NullPointerException, IOException
	{
		this(new FileInputStream(Objects.requireNonNull(pngFile)), true);
	}
	
	public UIExternalTexture(InputStream pngStream, boolean closeStream) throws NullPointerException, IOException
	{
		super(generateTextureIdentifier());
		try
		{
			this.nativeImage = NativeImage.read(Objects.requireNonNull(pngStream));
			this.nativeImageBackedTexture = new NativeImageBackedTexture(this.nativeImage);
			
			this.textureManager = MC_CLIENT.getTextureManager();
			this.textureManager.registerTexture(getTextureID(), this.nativeImageBackedTexture);
		}
		finally { if(closeStream) pngStream.close(); }
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
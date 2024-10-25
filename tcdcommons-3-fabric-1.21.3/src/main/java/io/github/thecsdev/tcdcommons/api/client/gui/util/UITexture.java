package io.github.thecsdev.tcdcommons.api.client.gui.util;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TTextureElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * An immutable {@link Object} representing a GUI texture that can be drawn on the screen.
 */
public @Virtual class UITexture extends Object
{
	// ==================================================
	public static final Identifier TEXTURE_UV_DEBUG = Identifier.of(getModID(), "textures/gui/uv_debug.png");
	// --------------------------------------------------
	private final @Nullable Identifier textureId;
	private final Dimension textureSize;
	private final Rectangle textureUVs;
	// ==================================================
	public UITexture() { this(TEXTURE_UV_DEBUG); }
	public UITexture(Identifier textureId) { this(textureId, 1, 1); }
	public UITexture(Identifier textureId, int textureWidth, int textureHeight) { this(textureId, new Dimension(textureWidth, textureHeight)); }
	public UITexture(Identifier textureId, Dimension textureSize) { this(textureId, textureSize, 0, 0, 1, 1); }
	public UITexture(Identifier textureId, Dimension textureSize, int u, int v, int uvW, int uvH) { this(textureId, textureSize, new Rectangle(u, v, uvW, uvH)); }
	public UITexture(Identifier textureId, int textureWidth, int textureHeight, int u, int v, int uvW, int uvH) { this(textureId, new Dimension(textureWidth, textureHeight), new Rectangle(u, v, uvW, uvH)); }
	public UITexture(Identifier textureId, Rectangle textureUVs) { this(textureId, new Dimension(256, 256), textureUVs); }
	public UITexture(Identifier textureId, Dimension textureSize, Rectangle textureUVs)
	{
		this.textureId = textureId; //already immutable
		this.textureSize = textureSize.getSize(); //clone original for immutability
		this.textureUVs = textureUVs.getBounds(); //clone original for immutability
	}
	// ==================================================
	public @Override boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		UITexture other = (UITexture) obj;

		// Compare textureId, textureUVs, and textureSize
		return (Objects.equals(this.textureId, other.textureId) &&
				this.textureUVs.equals(other.textureUVs) &&
				this.textureSize.equals(other.textureSize));
	}
	// --------------------------------------------------
	public @Override int hashCode()
	{
		int result = 17;
		
		// Compute hash codes from the fields and add them to the result
		result = 31 * result + (this.textureId != null ? this.textureId.hashCode() : 0);
		result = 31 * result + this.textureUVs.hashCode();
		result = 31 * result + this.textureSize.hashCode();
		
		return result;
	}
	// ==================================================
	/**
	 * Deprecated. Please use {@link #getTextureID()} instead.
	 * @see #getTextureID()
	 */
	@Deprecated(since = "3.6", forRemoval = true)
	public final Identifier getTexture() { return this.textureId; }
	
	/**
	 * Returns the texture's resource {@link Identifier}.
	 */
	public final Identifier getTextureID() { return this.textureId; }
	// --------------------------------------------------
	/**
	 * Returns the width and height of the texture.
	 */
	public final Dimension getTextureSize() { return this.textureSize.getSize(); /*clone original for immutability*/ }
	// --------------------------------------------------
	/**
	 * Returns the UVs of the texture that are rendered on this {@link TTextureElement}.
	 */
	public final Rectangle getTextureUVs() { return this.textureUVs.getBounds(); /*clone original for immutability*/ }
	// ==================================================
	/**
	 * Draws the {@link UITexture} over the {@link TDrawContext#currentTarget} element.
	 * @param pencil The {@link TDrawContext}.
	 */
	public final void drawTexture(TDrawContext pencil)
	{
		final var curr = pencil.currentTarget;
		drawTexture(pencil, curr.getX(), curr.getY(), curr.getWidth(), curr.getHeight());
	}
	// --------------------------------------------------
	/**
	 * Draws the {@link UITexture} in the given UI coordinates.
	 * @param pencil The {@link DrawContext}.
	 * @param x The UI x coordinate to draw at
	 * @param y The UI x coordinate to draw at
	 * @param width The UI texture horizontal size
	 * @param height The UI texture vertical size
	 */
	public final void drawTexture(DrawContext pencil, int x, int y, int width, int height)
	{
		if(this.textureId == null) return;
		pencil.drawTexture(
				RenderLayer::getGuiTextured,
				this.textureId,
				x, y,
				width, height,
				this.textureUVs.x, this.textureUVs.y,
				this.textureUVs.width, this.textureUVs.height,
				this.textureSize.width, this.textureSize.height);
	}
	// ==================================================
}
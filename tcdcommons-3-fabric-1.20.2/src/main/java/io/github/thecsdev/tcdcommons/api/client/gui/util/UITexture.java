package io.github.thecsdev.tcdcommons.api.client.gui.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TTextureElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * An immutable {@link Object} representing a GUI texture that can be drawn on the screen.
 */
public final class UITexture extends Object
{
	// ==================================================
	private final @Nullable Identifier textureId/* = WIDGETS_TEXTURE*/;
	private final Dimension textureSize/* = new Dimension(256, 256)*/;
	private final Rectangle textureUVs/* = new Rectangle(1, 1, 20, 20)*/;
	// ==================================================
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
	// --------------------------------------------------
	//no point in cloning an immutable object:
	//public @Override UITexture clone() { return new UITexture(this.textureId, this.textureSize, this.textureUVs); }
	// ==================================================
	/*@Deprecated
	public final @Nullable Identifier getTextureID() { return this.textureId; }
	
	@Deprecated //planning to make the class immutable
	public @Virtual void setTextureID(Identifier textureId) { this.textureId = textureId; }*/
	// --------------------------------------------------
	/**
	 * Returns the texture rendered by this {@link TTextureElement}.
	 */
	public final Identifier getTexture() { return this.textureId; }
	
	/*
	 * Sets the texture that will be rendered by this {@link TTextureElement}.
	 * @param textureId The {@link Identifier} of the texture png file in a resource pack.
	 * @param textureWidth The width of the png texture file in pixels.
	 * @param textureHeight The height of the png texture file in pixels.
	 * @see #getTexture()
	 *
	@Deprecated //planning to make the class immutable
	public @Virtual void setTexture(Identifier textureId, int textureWidth, int textureHeight)
	{
		if(textureId == null) textureId = WIDGETS_TEXTURE;
		this.textureId = textureId;
		setTextureSize(textureWidth, textureHeight);
		setTextureUVs(0, 0, textureWidth, textureHeight);
	}*/
	// --------------------------------------------------
	/**
	 * Returns the width and height of the texture.
	 */
	public final Dimension getTextureSize() { return this.textureSize.getSize(); /*clone original for immutability*/ }
	
	/*
	 * Defines the width and height of the current texture in pixels.
	 * @param textureWidth The width of the png texture file in pixels.
	 * @param textureHeight The height of the png texture file in pixels.
	 * @see #getTextureSize()
	 * @see #setTexture(Identifier, int, int)
	 *
	@Deprecated //planning to make the class immutable
	public @Virtual void setTextureSize(int textureWidth, int textureHeight)
	{
		this.textureSize.width = textureWidth;
		this.textureSize.height = textureHeight;
	}*/
	// --------------------------------------------------
	/**
	 * Returns the UVs of the texture that are rendered on this {@link TTextureElement}.
	 */
	public final Rectangle getTextureUVs() { return this.textureUVs.getBounds(); /*clone original for immutability*/ }
	
	/*
	 * Defines the UV coordinates (of the current {@link #getTexture()})
	 * that will be rendered on this {@link TTextureElement}.<br/>
	 * If a texture for example consists of multiple sprites, this can
	 * be used to define what sprite to render.
	 * @param u The starting X position on the texture.
	 * @param v The starting Y position on the texture.
	 * @param uWidth The width of the "sprite" in the texture.
	 * @param vHeight The height of the "sprite" in the texture.
	 *
	@Deprecated //planning to make the class immutable
	public @Virtual void setTextureUVs(int u, int v, int uWidth, int vHeight)
	{
		this.textureUVs.x = u;
		this.textureUVs.y = v;
		this.textureUVs.width = uWidth;
		this.textureUVs.height = vHeight;
	}*/
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
				this.textureId,
				x, y,
				width, height,
				this.textureUVs.x, this.textureUVs.y,
				this.textureUVs.width, this.textureUVs.height,
				this.textureSize.width, this.textureSize.height);
	}
	// ==================================================
}
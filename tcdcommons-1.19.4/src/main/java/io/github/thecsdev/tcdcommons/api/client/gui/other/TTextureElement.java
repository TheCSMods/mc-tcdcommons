package io.github.thecsdev.tcdcommons.api.client.gui.other;

import java.awt.Dimension;
import java.awt.Rectangle;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * A {@link TElement} that renders textures on the {@link TScreen}.
 */
public class TTextureElement extends TElement
{
	// ==================================================
	protected Identifier texture_id;
	protected final Dimension texture_size;
	protected final Rectangle uv_coords;
	protected final float[] color;
	// ==================================================
	public TTextureElement(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.texture_id = WIDGETS_TEXTURE;
		this.texture_size = new Dimension(256, 256);
		this.uv_coords = new Rectangle(1, 1, 20, 20);
		this.color = new float[] { 1,1,1,1 };
	}
	public @Override boolean isClickThrough() { return true; }
	public @Override boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return !gainingFocus; }
	// ==================================================
	/**
	 * Returns the texture rendered by this {@link TTextureElement}.
	 */
	public Identifier getTexture() { return this.texture_id; }
	
	/**
	 * Sets the texture that will be rendered by this {@link TTextureElement}.
	 * @param textureId The {@link Identifier} of the texture png file in a resource pack.
	 * @param textureWidth The width of the png texture file in pixels.
	 * @param textureHeight The height of the png texture file in pixels.
	 * @see #getTexture()
	 */
	public void setTexture(Identifier textureId, int textureWidth, int textureHeight)
	{
		if(textureId == null) textureId = WIDGETS_TEXTURE;
		this.texture_id = textureId;
		setTextureSize(textureWidth, textureHeight);
		setTextureUVs(0, 0, textureWidth, textureHeight);
	}
	// --------------------------------------------------
	/**
	 * Returns the width and height of the texture.
	 * @see #setTexture(Identifier, int, int)
	 * @see #setTextureSize(int, int)
	 */
	public Dimension getTextureSize() { return this.texture_size; }
	
	/**
	 * Defines the width and height of the current texture in pixels.
	 * @param textureWidth The width of the png texture file in pixels.
	 * @param textureHeight The height of the png texture file in pixels.
	 * @see #getTextureSize()
	 * @see #setTexture(Identifier, int, int)
	 */
	public void setTextureSize(int textureWidth, int textureHeight)
	{
		this.texture_size.width = textureWidth;
		this.texture_size.height = textureHeight;
	}
	// --------------------------------------------------
	/**
	 * Returns the UVs of the texture that are rendered on this {@link TTextureElement}.
	 * @see #setTextureUVs(int, int, int, int)
	 */
	public Rectangle getTextureUVs() { return this.uv_coords; }
	
	/**
	 * Defines the UV coordinates (of the current {@link #getTexture()})
	 * that will be rendered on this {@link TTextureElement}.<br/>
	 * If a texture for example consists of multiple sprites, this can
	 * be used to define what sprite to render.
	 * @param u The starting X position on the texture.
	 * @param v The starting Y position on the texture.
	 * @param uWidth The width of the "sprite" in the texture.
	 * @param vHeight The height of the "sprite" in the texture.
	 */
	public void setTextureUVs(int u, int v, int uWidth, int vHeight)
	{
		this.uv_coords.x = u;
		this.uv_coords.y = v;
		this.uv_coords.width = uWidth;
		this.uv_coords.height = vHeight;
	}
	// ==================================================
	public @Override void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		//apply shader stuff
		float alpha = getAlpha() * this.color[3];
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, this.texture_id);
		RenderSystem.setShaderColor(this.color[0], this.color[1], this.color[2], alpha);
		
		//enable a bunch of rendering stuff - *whatever this is*
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		//draw the texture
		drawTexture(matrices,
				this.x, this.y,
				this.width, this.height,
				this.uv_coords.x, this.uv_coords.y,
				this.uv_coords.width, this.uv_coords.height,
				this.texture_size.width, this.texture_size.height);
	}
	// ==================================================
}

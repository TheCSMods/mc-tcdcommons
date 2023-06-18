package io.github.thecsdev.tcdcommons.api.client.gui;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.mixin.hooks.MixinDrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * {@link TCDCommons}'s variation of {@link DrawContext}.
 */
public class TDrawContext extends DrawContext
{
	// ==================================================
	/**
	 * Represents the {@link TElement} that is currently being rendered by a {@link TScreen}.<br/>
	 * <b>Can be null. Read only, do not modify.</b>
	 */
	public @Nullable TElement currentTElement;
	// ==================================================
	public TDrawContext(DrawContext drawContext)
	{
		//copy variables from given draw context into this draw context
		super(null, null);
		final var mixin_this = ((MixinDrawContext)this);
		final var mixin_that = ((MixinDrawContext)drawContext);
		mixin_this.setClient(mixin_that.getClient());
		mixin_this.setMatrices(mixin_that.getMatrices());
		mixin_this.setVertexConsumers(mixin_that.getVertexConsumers());
		
		//initialize variables
		this.currentTElement = null;
	}
	// ==================================================
	/**
	 * Draws a {@link Text} on top of the {@link #currentTElement}.<br/>
	 * Uses the default text color and a padding of 5 pixels.
	 * @param text The {@link Text} to draw.
	 * @param alignment The {@link HorizontalAlignment} of the {@link Text} on the {@link #currentTElement}.
	 */
	public final void drawTText(Text text, HorizontalAlignment alignment)
	{
		drawTText(text, alignment, this.currentTElement.getEnabled() ? 16777215 : 10526880);
	}
	
	/**
	 * Draws a {@link Text} on top of the {@link #currentTElement}.<br/>
	 * Uses a padding of 5 pixels.
	 * @param text The {@link Text} to draw.
	 * @param alignment The {@link HorizontalAlignment} of the {@link Text} on the {@link #currentTElement}.
	 * @param color The integer RBG value of the {@link Text} color.
	 */
	public final void drawTText(Text text, HorizontalAlignment alignment, int color)
	{
		drawTText(text, alignment, color, 5);
	}
	
	/**
	 * Draws a {@link Text} on top of the {@link #currentTElement}.<br/>
	 * Uses a padding of 5 pixels.
	 * @param text The {@link Text} to draw.
	 * @param alignment The {@link HorizontalAlignment} of the {@link Text} on the {@link #currentTElement}.
	 * @param color The integer RBG value of the {@link Text} color.
	 * @param padding The horizontal {@link Text} offset to apply, measured in pixels.
	 */
	public void drawTText(Text text, HorizontalAlignment alignment, int color, int padding)
	{
		//null check the text
		if(text == null) return;
		
		//draw the text on the current element
		TextRenderer txtR = this.currentTElement.getTextRenderer();
		int x = this.currentTElement.getTpeX();
		int y = this.currentTElement.getTpeY() + (this.currentTElement.getTpeHeight() - 8) / 2;
		int width = this.currentTElement.getTpeWidth();
		
		switch(alignment)
		{
			case CENTER:
				drawCenteredTextWithShadow(txtR, text, x + width / 2, y, color);
				break;
			case LEFT:
				drawTextWithShadow(txtR, text, x + padding, y, color);
				break;
			case RIGHT:
				drawTextWithShadow(txtR, text, x + width - padding - txtR.getWidth(text.getString()), y, color);
				break;
			default: return;
		}
	}
	// ==================================================
	/**
	 * Draws an inner outline around the {@link #currentTElement}.
	 * @param color The border color.
	 */
	public void drawTBorder(int color)
	{
		drawBorder(
				this.currentTElement.getTpeX(), this.currentTElement.getTpeY(),
				this.currentTElement.getTpeWidth(), this.currentTElement.getTpeHeight(),
				color);
	}
	// --------------------------------------------------
	/**
	 * Draws a nine-sliced texture on top of the {@link #currentTElement}.<br/>
	 * Uses 256x256 as the texture image width and height. 
	 * @param textureId The texture {@link Identifier}.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param regionWidth The width of the texture's UV region, measured in pixels.
	 * @param regionHeight The height of the texture's UV region, measured in pixels.
	 * @param slicedBorderSize The size of the sliced pieces around the center piece.
	 */
	public final void drawTNineSlicedTexture(
			Identifier textureId,
			int u, int v, int regionWidth, int regionHeight,
			int slicedBorderSize)
	{
		drawTNineSlicedTexture(textureId, u, v, regionWidth, regionHeight, 256, 256, slicedBorderSize);
	}
	
	/**
	 * Draws a nine-sliced texture on top of the {@link #currentTElement}.
	 * @param textureId The texture {@link Identifier}.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvW The width of the texture's UV region, measured in pixels.
	 * @param uvH The height of the texture's UV region, measured in pixels.
	 * @param tW The width of the texture image, measured in pixels.
	 * @param tH The height of the texture image, measured in pixels.
	 * @param s The size of the sliced pieces around the center piece.
	 */
	public void drawTNineSlicedTexture(
			Identifier textureId,
			int u, int v, int uvW, int uvH,
			int tW, int tH,
			int s)
	{
		drawTNineSlicedTexture(textureId,
				this.currentTElement.getTpeX(), this.currentTElement.getTpeY(),
				this.currentTElement.getTpeWidth(), this.currentTElement.getTpeHeight(),
				u, v, uvW, uvH, tW, tH, s);
	}
	
	/**
	 * Draws a nine-sliced texture.
	 * @param textureId The texture {@link Identifier}.
	 * @param x The starting X position on the screen.
	 * @param y The starting Y position on the screen.
	 * @param w The width size of the sliced texture on the screen.
	 * @param h The height size of the sliced texture on the screen.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvW The width of the texture's UV region, measured in pixels.
	 * @param uvH The height of the texture's UV region, measured in pixels.
	 * @param tW The width of the texture image, measured in pixels.
	 * @param tH The height of the texture image, measured in pixels.
	 * @param s The size of the sliced pieces around the center piece.
	 */
	public void drawTNineSlicedTexture(
			Identifier textureId,
			int x, int y, int w, int h,
			int u, int v, int uvW, int uvH,
			int tW, int tH,
			int s)
	{
		//calculations
		final int s2 = s * 2;
		
		//draw 9-slice if possible...
		if(s2 < w || s2 < h)
		{
			//the four corners
			drawTexture(textureId, x, y, s, s, u, v, s, s, tW, tH);
			drawTexture(textureId, x + w - s, y, s, s, u + uvW - s, v, s, s, tW, tH);
			drawTexture(textureId, x, y + h - s, s, s, u, v + uvH - s, s, s, tW, tH);
			drawTexture(textureId, x + w - s, y + h - s, s, s, u + uvW - s, v + uvH - s, s, s, tW, tH);
			
			//the four sides
			drawTexture(textureId, x + s, y, w - s2, s, u + s, v, uvW - s2, s, tW, tH);
			drawTexture(textureId, x, y + s, s, h - s2, u, v + s, s, uvH - s2, tW, tH);	
			drawTexture(textureId, x + w - s, y + s, s, h - s2, u + uvW - s, v + s, s, uvH - s2, tW, tH);
			drawTexture(textureId, x + s, y + h - s, w - s2, s, u + s, v + uvH - s, uvW - s2, s, tW, tH);
			
			//the middle
			drawTRepeatingTexture(textureId, x + s, y + s, w - s2, h - s2, u + s, v + s, uvW - s2, uvH - s2, tW, tH);
		}
		//...else draw the full texture
		else
		{
			//if the slicing is larger than the element itself, then draw
			//the full texture in one single draw without slicing
			drawTexture(textureId, x, y, w, h, u, v, uvW, uvH, tW, tH);
		}
	}
	// --------------------------------------------------
	/**
	 * @param x The starting X position on the screen.
	 * @param y The starting Y position on the screen.
	 * @param width The width size of the repeating texture on the screen.
	 * @param height The height size of the repeating texture on the screen.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvRegionWidth The width of the texture's UV region, measured in pixels.
	 * @param uvRegionHeight The height of the texture's UV region, measured in pixels.
	 * @param textureWidth The width of the texture image, measured in pixels.
	 * @param textureHeight The height of the texture image, measured in pixels.
	 */
	public void drawTRepeatingTexture(
			Identifier textureId,
			int x, int y, int width, int height,
			int u, int v, int uvRegionWidth, int uvRegionHeight,
			int textureWidth, int textureHeight)
	{
		int endX = x + width, endY = y + height;
		for(int y1 = y; y1 < endY; y1 += uvRegionHeight)
		for(int x1 = x; x1 < endX; x1 += uvRegionWidth)
		{
			int nextW = uvRegionWidth, nextH = uvRegionHeight;
			if(x1 + nextW > endX) nextW -= (x1 + nextW) - endX;
			if(y1 + nextH > endY) nextH -= (y1 + nextH) - endY;
			if(nextW < 1 || nextH < 1) continue;
			drawTexture(textureId, x1, y1, nextW, nextH, u, v, nextW, nextH, textureWidth, textureHeight);
		}
	}
	// ==================================================
}
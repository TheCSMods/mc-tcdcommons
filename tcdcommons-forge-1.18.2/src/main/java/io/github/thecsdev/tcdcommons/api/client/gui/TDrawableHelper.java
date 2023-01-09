package io.github.thecsdev.tcdcommons.api.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class TDrawableHelper extends GuiComponent implements TParentElement
{
	// ==================================================
	/**
	 * The {@link Identifier} for the GUI widgets texture used by <b>Minecraft</b>.
	 */
	public static final ResourceLocation WIDGETS_TEXTURE = AbstractButton.GUI_ICONS_LOCATION;
	
	/**
	 * The {@link Identifier} for the GUI widgets texture used by {@link TCDCommons}.
	 */
	public static final ResourceLocation T_WIDGETS_TEXTURE = new ResourceLocation(TCDCommons.getModID(), "textures/gui/widgets.png");
	// ==================================================
	/**
	 * Draws an inner border/outline (1px) for this element.
	 * @param matrices The {@link MatrixStack}.
	 * @param color The color of the inner border, aka outline.
	 */
	public void drawOutline(PoseStack matrices, int color)
	{
		int x = getTpeX(), y = getTpeY();
		int w = getTpeWidth(), h = getTpeHeight();
		int ol = GuiUtils.applyAlpha(color, getAlpha());
		
		drawHorizontalLine(matrices, x, x + w - 1, y, ol);
		drawHorizontalLine(matrices, x, x + w - 1, y + h - 1, ol);
		drawVerticalLine(matrices, x, y, y + h - 1, ol);
		drawVerticalLine(matrices, x + w - 1, y, y + h - 1, ol);
	}
	public void drawHorizontalLine(PoseStack matrices, int x1, int x2, int y, int color) { hLine(matrices, x1, x2, y, color); }
	public void drawVerticalLine(PoseStack matrices, int x, int y1, int y2, int color) { vLine(matrices, x, y1, y2, color); }
	// ==================================================
	/**
	 * Draws a 9-sliced texture (Corners are SLICExSLICE pixels).
	 * @param matrices The {@link MatrixStack} used for rendering.
	 * @param u The texture UV X coordinate.
	 * @param v The texture UV Y coordinate.
	 * @param regionWidth The texture UV width.
	 * @param regionHeight The texture UV height.
	 * @param slice The slice size in all directions.
	 */
	public void draw9SliceTexture(PoseStack matrices, int u, int v, int regionWidth, int regionHeight, int slice)
	{
		draw9SliceTexture(
				matrices,
				getTpeX(), getTpeY(),
				getTpeWidth(), getTpeHeight(),
				u, v,
				regionHeight, regionHeight,
				256, 256,
				slice);
	}
	
	/**
	 * Draws a 9-sliced texture (Corners are SLICExSLICE pixels).
	 * @param matrices The {@link MatrixStack} used for rendering.
	 * @param x The X coordinate on the screen.
	 * @param y The Y coordinate on the screen.
	 * @param w (width)
	 * @param h (height)
	 * @param u The texture UV X coordinate.
	 * @param v The texture UV Y coordinate.
	 * @param uW (uv/region width) The texture UV width.
	 * @param vH (uv/region height) The texture UV height.
	 * @param tW (texture width) The width of the shader texture being used.
	 * @param tH (texture height) The height of the shader texture being used.
	 * @param s (slice) The slice size in all directions.
	 */
	public static void draw9SliceTexture(
			PoseStack matrices,
			int x, int y,
			int w, int h,
			int u, int v,
			int uW, int vH,
			int tW, int tH,
			int s)
	{
		int s2 = s * 2;
		if(s2 < w || s2 < h)
		{
			//the four corners
			drawTexture(matrices, x, y, s, s, u, v, s, s, tW, tH);
			drawTexture(matrices, x + w - s, y, s, s, u + uW - s, v, s, s, tW, tH);
			drawTexture(matrices, x, y + h - s, s, s, u, v + vH - s, s, s, tW, tH);
			drawTexture(matrices, x + w - s, y + h - s, s, s, u + uW - s, v + vH - s, s, s, tW, tH);
			
			//the four sides
			drawTexture(matrices, x + s, y, w - s2, s, u + s, v, uW - s2, s, tW, tH);
			drawTexture(matrices, x, y + s, s, h - s2, u, v + s, s, vH - s2, tW, tH);	
			drawTexture(matrices, x + w - s, y + s, s, h - s2, u + uW - s, v + s, s, vH - s2, tW, tH);
			drawTexture(matrices, x + s, y + h - s, w - s2, s, u + s, v + vH - s, uW - s2, s, tW, tH);
			
			//the middle
			drawTiledTexture(matrices, x + s, y + s, w - s2, h - s2, u + s, v + s, uW - s2, vH - s2, tW, tH);
		}
		else
		{
			//if the slicing is larger than the element itself, then draw
			//the full texture in one single draw without slicing
			drawTexture(matrices, x, y, w, h, u, v, uW, vH, tW, tH);
		}
	}
	// --------------------------------------------------
	/**
	 * Draws a tiled repeating texture.
	 * @param matrices The {@link MatrixStack} used for rendering.
	 * @param x The X coordinate on the screen.
	 * @param y The Y coordinate on the screen.
	 * @param w (width)
	 * @param h (height)
	 * @param u The texture UV X coordinate.
	 * @param v The texture UV Y coordinate.
	 * @param uW (uv/region width) The texture UV width.
	 * @param vH (uv/region height) The texture UV height.
	 * @param tW (texture width) The width of the shader texture being used.
	 * @param tH (texture height) The height of the shader texture being used.
	 */
	public static void drawTiledTexture(
			PoseStack matrices,
			int x, int y,
			int w, int h,
			int u, int v,
			int uW, int vH,
			int tW, int tH)
	{
		int endX = x + w, endY = y + h;
		for(int y1 = y; y1 < endY; y1 += vH)
		for(int x1 = x; x1 < endX; x1 += uW)
		{
			int nextW = uW, nextH = vH;
			if(x1 + nextW > endX) nextW -= (x1 + nextW) - endX;
			if(y1 + nextH > endY) nextH -= (y1 + nextH) - endY;
			if(nextW < 1 || nextH < 1) continue;
			drawTexture(matrices, x1, y1, nextW, nextH, u, v, nextW, nextH, tW, tH);
		}
	}
	// ==================================================
	public static void drawTexture(PoseStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight)
	{
		blit(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
	}
	public static void drawTexture(PoseStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight)
	{
		blit(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
	}
	// ==================================================
	public static void drawTextWithShadow(PoseStack matrices, Font textRenderer, Component text, int x, int y, int color)
	{
		drawString(matrices, textRenderer, text, x, y, color);
	}
	public static void drawCenteredText(PoseStack matrices, Font textRenderer, String text, int centerX, int y, int color)
	{
		drawCenteredString(matrices, textRenderer, text, centerX, y, color);
	}
	public static void drawCenteredText(PoseStack matrices, Font textRenderer, Component text, int centerX, int y, int color)
	{
		drawCenteredString(matrices, textRenderer, text, centerX, y, color);
	}
	// ==================================================
}
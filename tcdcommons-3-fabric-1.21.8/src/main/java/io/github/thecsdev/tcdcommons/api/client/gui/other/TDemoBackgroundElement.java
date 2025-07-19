package io.github.thecsdev.tcdcommons.api.client.gui.other;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.util.Identifier;

/**
 * A {@link TPanelElement} that looks like the one seen in the game's menus.
 */
public @Virtual class TDemoBackgroundElement extends TElement
{
	// ==================================================
	public static final Identifier TEX_BACKGROUND = Identifier.of("textures/gui/demo_background.png");
	// --------------------------------------------------
	protected final float[] color = new float[] { 1, 1, 1, 1 };
	// ==================================================
	public TDemoBackgroundElement(int x, int y, int width, int height) { super(x, y, width, height); }
	// --------------------------------------------------
	/**
	 * Returns the array containing the RGBA values of the color.
	 */
	public final float[] getColor() { return this.color; }
	
	/**
	 * Sets the color using an array that contains RGBA color values.
	 * @param rgbaColor The new color.
	 */
	public @Virtual void setColor(float[] rgbaColor) throws NullPointerException, IllegalArgumentException
	{
		//argument check
		if(Objects.requireNonNull(rgbaColor).length != 4)
			throw new IllegalArgumentException("Illegal array length. Must be 4.");
		
		//assign new color
		this.color[0] = rgbaColor[0];
		this.color[1] = rgbaColor[1];
		this.color[2] = rgbaColor[2];
		this.color[3] = rgbaColor[3];
	}
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		//push the color
		pencil.pushTShaderColor(this.color[0], this.color[1], this.color[2], this.color[3]);
		
		//draw the background
		pencil.drawTNineSlicedTexture(TEX_BACKGROUND, 0, 0, 248, 166, 5);
		
		//pop the color
		pencil.popTShaderColor();
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.other;

import java.awt.Color;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class TFillColorElement extends TBlankElement
{
	// ==================================================
	private static final int COLOR_MAGENTA = Color.MAGENTA.getRGB();
	// --------------------------------------------------
	protected int color;
	// ==================================================
	public TFillColorElement(int x, int y, int width, int height) { this(x, y, width, height, COLOR_MAGENTA); }
	public TFillColorElement(int x, int y, int width, int height, int color)
	{
		super(x, y, width, height);
		this.color = color;
	}
	// ==================================================
	public final int getColor() { return this.color; }
	public @Virtual void setColor(int color) { this.color = color; }
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil) { pencil.drawTFill(this.color); }
	// ==================================================
}
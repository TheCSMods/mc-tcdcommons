package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class TFillColorElement extends TBlankElement
{
	// ==================================================
	protected int color;
	// ==================================================
	public TFillColorElement(int x, int y, int width, int height) { super(x, y, width, height); }
	// ==================================================
	public final int getColor() { return this.color; }
	public @Virtual void setColor(int color) { this.color = color; }
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil) { pencil.drawTFill(this.color); }
	// ==================================================
}
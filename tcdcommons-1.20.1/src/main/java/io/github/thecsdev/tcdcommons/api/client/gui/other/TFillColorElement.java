package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.TDrawContext;

public class TFillColorElement extends TBlankElement
{
	// ==================================================
	protected int color;
	// ==================================================
	public TFillColorElement(int x, int y, int width, int height) { super(x, y, width, height); }
	public TFillColorElement(int x, int y, int width, int height, int color)
	{
		this(x, y, width, height);
		setColor(color);
	}
	// --------------------------------------------------
	public int getColor() { return color; }
	public void setColor(int color) { this.color = color; }
	// ==================================================
	@Override
	public void render(TDrawContext pencil, int mouseX, int mouseY, float deltaTime)
	{
		super.render(pencil, mouseX, mouseY, deltaTime);
		pencil.fill(x, y, x + width, y + height, color);
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.other;

import net.minecraft.client.util.math.MatrixStack;

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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		super.render(matrices, mouseX, mouseY, deltaTime);
		fill(matrices, x, y, width, height, color);
	}
	// ==================================================
}
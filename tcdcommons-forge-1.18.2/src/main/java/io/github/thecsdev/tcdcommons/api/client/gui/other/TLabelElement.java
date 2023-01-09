package io.github.thecsdev.tcdcommons.api.client.gui.other;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.SubjectToChange;
import net.minecraft.network.chat.Component;

public class TLabelElement extends TElement
{
	// ==================================================
	protected Component text;
	protected HorizontalAlignment horizontalAlignment;
	protected int colorEnabled, colorDisabled;
	// ==================================================
	public TLabelElement(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TLabelElement(int x, int y, int width, int height, Component label)
	{
		super(x, y, width, height);
		setText(label);
		setHorizontalAlignment(HorizontalAlignment.LEFT);
		setColor(16777215, 10526880); //was -5570561
	}
	
	@Override
	public boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return !gainingFocus; }
	// --------------------------------------------------
	/**
	 * Returns the label text that this {@link TLabelElement} will draw.
	 */
	public Component getText() { return this.text; }
	
	/**
	 * Sets the {@link #getText()}.
	 * @param label The new label text that will be drawn.
	 */
	public void setText(Component label) { this.text = label; }
	// --------------------------------------------------
	/**
	 * Returns the {@link HorizontalAlignment} of the
	 * {@link #getText()} that will be drawn.
	 */
	public HorizontalAlignment getHorizontalAlignment() { return this.horizontalAlignment; }
	
	/**
	 * Sets the {@link #getHorizontalAlignment()}.
	 * @param alignment The new {@link HorizontalAlignment}.
	 */
	public void setHorizontalAlignment(HorizontalAlignment alignment) { this.horizontalAlignment = alignment; }
	// --------------------------------------------------
	/**
	 * Returns the color that will be used to draw the {@link #getText()}.<br/>
	 * {@link #isEnabled()} is taken into account as well.
	 */
	@SubjectToChange
	public final int getColor() { return isEnabled() ? this.colorEnabled : this.colorDisabled; }
	public int getColorEnabled() { return this.colorEnabled; }
	public int getColorDisabled() { return this.colorDisabled; }
	
	/**
	 * Sets the {@link #getColor()}.
	 * @param color The new color. See {@link Color#getRGB()}.
	 */
	@SubjectToChange
	public void setColor(int whenEnabled, int whenDisabled)
	{
		this.colorEnabled = whenEnabled;
		this.colorDisabled = whenDisabled;
	}
	// ==================================================
	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		drawTElementText(matrices, getText(), getHorizontalAlignment(), getColor(), 0, deltaTime);
	}
	
	@Override
	public void postRender(PoseStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		//this adds a visual focus indicator just in
		//case this element supports focus
		if(isFocused()) drawOutline(matrices, -5570561);
	}
	// ==================================================
}
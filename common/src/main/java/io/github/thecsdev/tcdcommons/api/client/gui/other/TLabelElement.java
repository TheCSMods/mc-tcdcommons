package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProviderSetter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public @Virtual class TLabelElement extends TBlankElement implements ITextProviderSetter
{
	// ==================================================
	protected @Nullable Component text;
	protected HorizontalAlignment textHorizontalAlignment = HorizontalAlignment.LEFT;
	protected int textColor = TDrawContext.DEFAULT_TEXT_COLOR;
	protected int textSideOffset = 0 /*TDrawContext.DEFAULT_TEXT_SIDE_OFFSET*/;
	protected float textScale = 1;
	// ==================================================
	public TLabelElement(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TLabelElement(int x, int y, int width, int height, Component text)
	{
		super(x, y, width, height);
		this.text = text;
	}
	// ==================================================
	public final @Override Component getText() { return this.text; }
	public @Virtual @Override void setText(Component text) { this.text = text; }
	// --------------------------------------------------
	public final HorizontalAlignment getTextHorizontalAlignment() { return this.textHorizontalAlignment; }
	public @Virtual void setTextHorizontalAlignment(HorizontalAlignment alignment) { this.textHorizontalAlignment = alignment; }
	// --------------------------------------------------
	public final int getTextColor() { return this.textColor; }
	public @Virtual void setTextColor(int color) { this.textColor = color; }
	//
	public final int getTextSideOffset() { return this.textSideOffset; }
	public @Virtual void setTextSideOffset(int textSideOffset) { this.textSideOffset = textSideOffset; }
	//
	public final float getTextScale() { return this.textScale; }
	public @Virtual void setTextScale(float textScale) { this.textScale = Math.max(textScale, 0.5f); }
	// --------------------------------------------------
	public @Override void render(TDrawContext pencil)
	{
		pencil.drawTElementTextTHSCS(
				this.text,
				this.textHorizontalAlignment,
				this.textSideOffset,
				this.textColor,
				this.textScale);
	}
	// ==================================================
}
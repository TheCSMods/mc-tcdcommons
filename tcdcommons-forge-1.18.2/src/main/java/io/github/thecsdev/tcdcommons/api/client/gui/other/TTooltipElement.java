package io.github.thecsdev.tcdcommons.api.client.gui.other;

import static io.github.thecsdev.tcdcommons.api.client.gui.widget.TTextFieldWidget.TEXT_PADDING;

import java.awt.Color;
import java.awt.Rectangle;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.util.SubjectToChange;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@SubjectToChange("May get removed.")
public class TTooltipElement extends TElement
{
	// ==================================================
	protected @Nullable MultiLineLabel multilineText;
	protected int textColor;
	protected int maxWidth;
	protected int lineSpacing;
	// ==================================================
	public TTooltipElement(int maxWidth) { this(0, 0, maxWidth, null); }
	public TTooltipElement(int x, int y, int maxWidth) { this(x, y, maxWidth, null); }
	public TTooltipElement(int x, int y, int maxWidth, Component text)
	{
		super(x, y, Math.max(maxWidth, 60), 10);
		setBlitOffset((int)(getItemRenderer().blitOffset + 120));
		this.textColor = Color.WHITE.getRGB();
		setMaxWidth(maxWidth);
		setLineSpacing(4);
		setTooltip(text);
	}
	// --------------------------------------------------
	public final @Override boolean getEnabled() { return false; }
	public final @Override boolean isClickThrough() { return true; }
	// --------------------------------------------------
	public @Override @Nullable Rectangle getRenderingBoundingBox()
	{
		//always render regardless of parents
		RENDER_RECT.setLocation(getTpeX(), getTpeY());
		RENDER_RECT.setSize(getTpeWidth(), getTpeHeight());
		return RENDER_RECT;
	}
	// ==================================================
	public @Override void setTooltip(@Nullable Component tooltip)
	{
		super.setTooltip(tooltip);
		refreshText();
	}
	// --------------------------------------------------
	/**
	 * Updates the {@link #multilineText} component that
	 * is used for rendering the tooltip text.
	 */
	public final void refreshText()
	{
		//assign multiline text
		Component text = getTooltip();
		int textW = getTextWidth(text);
		//width scalability
		if(textW < this.maxWidth) this.width = (TEXT_PADDING * 2) + textW;
		else this.width = this.maxWidth;
		//assign multiline text
		if(text != null)
			this.multilineText = MultiLineLabel.create(getTextRenderer(), text, getTpeWidth());
		else this.multilineText = null;
		//height scalability
		this.height = (TEXT_PADDING * 2) + getTextHeight();
	}
	
	/**
	 * Updates the position of this tooltip element based
	 * on the target element whose tooltip text will be rendered.
	 */
	public void refreshPosition(@Nullable TElement target, int mouseX, int mouseY)
	{
		if(target != null && target.isFocused())
			refreshPosition_toElement(target);
		else refreshPosition_toCursor(mouseX, mouseY);
	}
	
	protected void refreshPosition_toCursor(int mouseX, int mouseY)
	{
		//get parent
		TParentElement p = this.screen;
		if(p == null) return;
		//set position to cursor position
		this.x = mouseX + 5;
		this.y = mouseY + 5;
		//re-align X
		if(getTpeEndX() > p.getTpeEndX()) this.x -= getTpeWidth() + 10;
		//re-align Y
		if(getTpeEndY() > p.getTpeEndY()) this.y -= getTextHeight() + 10;
	}
	
	protected void refreshPosition_toElement(TElement target)
	{
		//get parent
		TParentElement p = this.screen;
		if(p == null) return;
		//set position to cursor position
		this.x = target.getTpeX();
		this.y = target.getTpeEndY();
		//re-align X
		if(getTpeEndX() > p.getTpeEndX()) this.x -= getTpeWidth() - target.getTpeWidth();
		//re-align Y
		if(getTpeEndY() > p.getTpeEndY()) this.y -= getTpeHeight() + target.getTpeHeight();
	}
	// ==================================================
	public final int getTextHeight()
	{
		int fh = getTextRenderer().lineHeight, ls = getLineSpacing();
		if(this.multilineText == null) return fh;
		return (this.multilineText.getLineCount() * (fh + ls)) - ls;
	}
	// --------------------------------------------------
	/**
	 * Returns the spacing between tooltip lines.
	 */
	public int getLineSpacing() { return this.lineSpacing; }
	
	/**
	 * Sets the {@link #getLineSpacing()}.
	 * @param spacing The new line spacing.
	 */
	public void setLineSpacing(int spacing) { this.lineSpacing = Mth.clamp(spacing, 0, 20); }
	
	public int getMaxWidth() { return this.maxWidth; }
	public void setMaxWidth(int maxWidth) { this.maxWidth = Math.max(maxWidth, 60); }
	// ==================================================
	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		//draw background
		RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
	    RenderSystem.setShaderColor(1, 1, 1, getAlpha());
		draw9SliceTexture(matrices, 40, 20, 20, 20, 3);
		//draw message
		drawMessage(matrices, mouseX, mouseY, deltaTime);
	}
	
	public void drawMessage(PoseStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		//----- pretty much copy-pasted from text area code
		//null check
		if(this.multilineText == null || this.screen == null)
			return;
		//scissors
		Rectangle rr = getRenderingBoundingBox();
		if(rr == null) return; //weird, shouldn't happen, but it does.
		GuiUtils.enableScissor(this.screen.getClient(), rr.x, rr.y, rr.width, rr.height);
		//draw
		int lh = getTextRenderer().lineHeight + getLineSpacing();
		this.multilineText.renderLeftAligned(matrices, getTpeX() + TEXT_PADDING, getTpeY() + TEXT_PADDING, lh, textColor);
		//scissors, again
		this.screen.resetScissors();
	}
	// ==================================================
	public final int getTextWidth(Component text)
	{
		//null check
		if(text == null) return 0;
		//get text renderer and split lines
		Font tr = getTextRenderer();
		String[] lines = text.getString().split("\\r?\\n");
		//if only one line or less, use TextRenderer#getWidth
		if(lines.length < 2) return tr.width(text);
		//iterate all lines
		int maxLineSize = 0;
		for(String line : lines)
		{
			//find the longest line
			int lineSize = tr.width(line);
			if(maxLineSize < lineSize) maxLineSize = lineSize;
		}
		//return the size of the longest line
		return maxLineSize;
	}
	// ==================================================
}
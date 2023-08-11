package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE;
import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE_FOCUSED;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext.DEFAULT_TEXT_COLOR;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public @Virtual class TMenuItemElement extends TButtonWidget
{
	// ==================================================
	private static final TextRenderer TR = TCDCommonsClient.MC_CLIENT.textRenderer;
	// ==================================================
	public TMenuItemElement() { this(literal("-")); }
	public TMenuItemElement(Text text) { this(null, text); }
	public TMenuItemElement(TMenuPanel targetParentMenu, Text text)
	{
		super(0, 0, 0, 0, null);
		setText(text);
		targetParentMenu.addChild(this, true);
	}
	// --------------------------------------------------
	protected final void TButtonWidget_super_setText(@Nullable Text text) { super.setText(text); }
	public @Virtual @Override void setText(@Nullable Text text) { super.setText(text); pack(); }
	// --------------------------------------------------
	/**
	 * This method sets the size of this {@link TMenuItemElement} to its minimum 
	 * possible size by calling {@link #setSize(int, int)}. It's akin to the packing 
	 * of a component in Swing, where the component is resized to the smallest 
	 * possible size that accommodates its contents.<p>
	 * This allows the parent {@link TMenuPanel} to properly control this element's size
	 * when re-aligning its children.
	 * @see #realignParentMenuChildren()
	 * @see TMenuPanel#realignChildren()
	 */
	protected @Virtual void pack()
	{
		setSize(TR.getWidth(text == null ? literal("-") : text) + 12, TR.fontHeight + 4);
		realignParentMenuChildren();
	}
	
	/**
	 * If {@link #getParent()} is a {@link TMenuPanel}, calls
	 * {@link TMenuPanel#realignChildren()} on it.
	 */
	protected final void realignParentMenuChildren()
	{
		if(getParent() instanceof TMenuPanel)
			((TMenuPanel)getParent()).realignChildren();
	}
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		//draw the text, but scissor it to this element
		pencil.enableScissor(this.x, this.y, this.getEndX(), this.getEndY());
		pencil.drawTElementTextTHSC(this.text, HorizontalAlignment.LEFT, 5, DEFAULT_TEXT_COLOR);
		pencil.disableScissor();
	}
	
	public @Virtual @Override void postRender(TDrawContext pencil)
	{
		//if hovered or focused, draw an outline
		if(isFocused()) pencil.drawTBorder(COLOR_OUTLINE_FOCUSED);
		else if(isHovered()) pencil.drawTBorder(COLOR_OUTLINE);
	}
	// ==================================================
}
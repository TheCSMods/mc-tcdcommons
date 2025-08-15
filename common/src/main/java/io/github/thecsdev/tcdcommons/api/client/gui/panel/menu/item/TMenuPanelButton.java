package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE;
import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE_FOCUSED;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext.DEFAULT_TEXT_COLOR;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

public @Virtual class TMenuPanelButton extends TButtonWidget implements IMenuPanelItem
{
	// ==================================================
	private static final Font TR = TCDCommonsClient.MC_CLIENT.font;
	// ==================================================
	public TMenuPanelButton() { this(literal("-")); }
	public TMenuPanelButton(Component text) { this(null, text); }
	public TMenuPanelButton(TMenuPanel targetParentMenu, Component text)
	{
		super(0, 0, 0, 0, null);
		setText(text);
		if(targetParentMenu != null)
			targetParentMenu.addChild(this, true);
	}
	// --------------------------------------------------
	protected final void TButtonWidget_super_setText(@Nullable Component text) { super.setText(text); }
	public @Virtual @Override void setText(@Nullable Component text) { super.setText(text); pack(); }
	// --------------------------------------------------
	public @Virtual @Override void pack()
	{
		setSize(TR.width(text == null ? literal("-") : text) + 12, TR.lineHeight + 4);
		realignParentMenuChildren();
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
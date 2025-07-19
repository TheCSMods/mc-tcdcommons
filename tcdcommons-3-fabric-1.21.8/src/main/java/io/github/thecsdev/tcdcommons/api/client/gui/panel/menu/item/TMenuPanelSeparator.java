package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class TMenuPanelSeparator extends TElement implements IMenuPanelItem
{
	// ==================================================
	public TMenuPanelSeparator() { this(null); }
	public TMenuPanelSeparator(@Nullable TMenuPanel targetParentMenu)
	{
		super(0, 0, 5, 5);
		if(targetParentMenu != null)
			targetParentMenu.addChild(this, true);
	}
	// ==================================================
	public @Virtual @Override void pack() { setSize(3, 3); realignParentMenuChildren(); }
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil)
	{
		//horizontal lines
		if(this.width > this.height) pencil.drawHorizontalLine(getX() + 2, getEndX() - 2, getY() + 2, COLOR_OUTLINE);
		//vertical lines
		else pencil.drawVerticalLine(getX() + 2, getY() + 2, getEndY() - 2, COLOR_OUTLINE);
	}
	// ==================================================
}
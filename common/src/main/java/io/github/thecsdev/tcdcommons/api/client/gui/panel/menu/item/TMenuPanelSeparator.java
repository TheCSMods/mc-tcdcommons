package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import org.jetbrains.annotations.Nullable;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE;

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
		if(this.width > this.height) pencil.hLine(getX() + 2, getEndX() - 2, getY() + 2, COLOR_OUTLINE);
		//vertical lines
		else pencil.vLine(getX() + 2, getY() + 2, getEndY() - 2, COLOR_OUTLINE);
	}
	// ==================================================
}
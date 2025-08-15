package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import org.jetbrains.annotations.ApiStatus.Experimental;

public @Virtual class TMenuBarPanel extends TMenuPanel
{
	// ==================================================
	public static final int HEIGHT = 15;
	// ==================================================
	public @Experimental TMenuBarPanel(TParentElement target)
	{
		this(0, 0,
				(target instanceof TPanelElement) ?
					(target.getWidth() - ((TPanelElement)target).getScrollPadding() * 2) :
					target.getWidth());
		target.addChild(this, true);
	}
	public TMenuBarPanel(int x, int y, int width)
	{
		super(x, y, width, HEIGHT);
		this.scrollFlags = SCROLL_HORIZONTAL;
	}
	// ==================================================
	public final @Override void onRealignChildren()
	{
		//iterate all children, and re-adjust them
		final int sp = this.scrollPadding, sp2 = this.scrollPadding * 2;
		TElement previous = null;
		for(final var child : getChildren())
		{
			//first update the child's position;
			//focus on positioning it after the previous child
			int nextX = getX() + sp, nextY = getY() + sp;
			if(previous != null)
			{
				nextX = previous.getEndX();
				nextY = previous.getY();
			}
			child.setPosition(nextX, nextY, false);
			
			//next up, update the child's size;
			//height should be same as parent height, while width is up to the child element
			child.setSize(child.getWidth(), getHeight() - sp2);
			
			//set previous
			previous = child;
		}
	}
	// ==================================================
}
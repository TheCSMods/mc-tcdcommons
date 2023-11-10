package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import org.joml.Math;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.AutomaticSize;

/**
 * A {@link UILayout} that automatically adjusts the parent
 * element's size to fit all of its children.
 */
public final class UIAutomaticSize extends UILayout
{
	// ==================================================
	protected AutomaticSize automaticSize;
	// ==================================================
	public UIAutomaticSize(AutomaticSize automaticSize)
	{
		this.automaticSize = automaticSize;
	}
	// ==================================================
	public @Virtual void apply()
	{
		//obtain parent
		final var parent = getParent();
		if(!(parent instanceof TElement) || this.automaticSize == null)
			return;
		
		//prepare
		final int padding = getParentScrollPadding(), padding2 = padding * 2;
		int newWidth = parent.getWidth();
		int newHeight = parent.getHeight();
		
		//obtain topmost elements
		@SuppressWarnings("removal")
		final var t = parent.getChildren().getTopmostElements();
		final var tRight = t.Item4;
		final var tBottom = t.Item2;
		if(this.automaticSize.hasX())
		{
			if(tRight != null) newWidth = Math.max((tRight.getEndX() - parent.getX()) + padding, padding2);
			else newWidth = padding2;
		}
		if(this.automaticSize.hasY())
		{
			if(tBottom != null) newHeight = Math.max((tBottom.getEndY() - parent.getY()) + padding, padding2);
			else newHeight = padding2;
		}
		
		//finally, apply the new size
		((TElement)parent).setSize(newWidth, newHeight);
	}
	// --------------------------------------------------
	protected @Virtual @Override void onSiblingAdded(TElement sibling, boolean repositioned) { apply(); }
	protected @Virtual @Override void onSiblingRemoved(TElement sibling, boolean repositioned) { apply(); }
	// ==================================================
}
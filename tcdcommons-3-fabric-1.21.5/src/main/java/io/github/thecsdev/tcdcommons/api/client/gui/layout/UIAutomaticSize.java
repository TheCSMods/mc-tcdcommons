package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import org.joml.Math;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.AutomaticSize;

/**
 * {@link Deprecated} due to naming issues. Please use {@link UIAutomaticSizeLayout} instead.
 */
@Deprecated(since = "3.9.8")
public @Virtual class UIAutomaticSize extends UILayout
{
	// ==================================================
	protected AutomaticSize automaticSize;
	// ==================================================
	public UIAutomaticSize(AutomaticSize automaticSize) { this.automaticSize = automaticSize; }
	// ==================================================
	public @Virtual @Override void apply(TParentElement parent)
	{
		//obtain parent
		if(!(parent instanceof TElement) || this.automaticSize == null)
			return;
		
		//prepare
		final int padding = getElementScrollPadding(parent), padding2 = padding * 2;
		int newWidth = parent.getWidth();
		int newHeight = parent.getHeight();
		
		//obtain topmost elements
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
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.AutomaticSize;

/**
 * A {@link UILayout} that adjusts the size of a {@link TParentElement} to fit its children.
 * @apiNote Same as {@link UIAutomaticSize}.
 */
@SuppressWarnings("deprecation")
public @Virtual class UIAutomaticSizeLayout extends UIAutomaticSize
{
	public UIAutomaticSizeLayout(AutomaticSize automaticSize) { super(automaticSize); }
}
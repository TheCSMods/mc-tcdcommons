package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuPanel;

/**
 * Represents a GUI element on a {@link TMenuPanel}.
 */
public interface IMenuPanelItem extends TParentElement
{
	/**
	 * This method sets the size of this {@link IMenuPanelItem} to its minimum 
	 * possible size by calling {@link TElement#setSize(int, int)}. It's akin to the packing 
	 * of a component in Swing, where the component is resized to the smallest 
	 * possible size that accommodates its contents.<p>
	 * This allows the parent {@link TMenuPanel} to properly control this element's size
	 * when re-aligning its children.
	 * @see #realignParentMenuChildren()
	 * @see TMenuPanel#realignChildren()
	 */
	public void pack();
	
	/**
	 * If {@link #getParent()} is a {@link TMenuPanel}, calls
	 * {@link TMenuPanel#realignChildren()} on it.
	 */
	default void realignParentMenuChildren()
	{
		if(getParent() instanceof TMenuPanel)
			((TMenuPanel)getParent()).realignChildren();
	}
}
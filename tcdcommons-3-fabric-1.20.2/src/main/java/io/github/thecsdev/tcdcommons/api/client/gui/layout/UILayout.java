package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * An {@link Object} used to enforce certain
 * "children layout rules" within a {@link TParentElement}.
 */
public abstract class UILayout extends Object
{
	// ==================================================
	/**
	 * Default "parent scroll padding" returned by {@link #getElementScrollPadding(TParentElement)}
	 * when the {@link TParentElement} is not a {@link TPanelElement}.
	 */
	protected int defaultPsp = 0;
	// ==================================================
	/**
	 * Applies this {@link UILayout} to a given {@link TParentElement}.
	 */
	public abstract void apply(TParentElement parent);
	// ==================================================
	/**
	 * See {@link #defaultPsp} for more info.
	 */
	public final int getDefaultPSP() { return this.defaultPsp; }
	public final void setDefaultPSP(int defaultPsp) { this.defaultPsp = Math.abs(defaultPsp); }
	// --------------------------------------------------
	/**
	 * If the {@link TParentElement} is a {@link TPanelElement},
	 * returns {@link TPanelElement#getScrollPadding()}, and {@link #defaultPsp} otherwise.
	 * @param parent The {@link TParentElement} being checked.
	 */
	protected @Virtual int getElementScrollPadding(TParentElement parent)
	{
		if(parent instanceof TPanelElement)
			return ((TPanelElement)parent).getScrollPadding();
		else return this.defaultPsp;
	}
	// ==================================================
}
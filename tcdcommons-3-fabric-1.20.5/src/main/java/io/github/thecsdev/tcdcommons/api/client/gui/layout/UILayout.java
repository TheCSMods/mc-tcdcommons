package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import java.awt.Rectangle;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.config.TConfigPanelBuilder;
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
		if(parent instanceof TPanelElement parentPanel)
			return parentPanel.getScrollPadding();
		else return this.defaultPsp;
	}
	// ==================================================
	/**
	 * Applies this {@link UILayout} to a given {@link TParentElement}.
	 */
	public abstract void apply(TParentElement parent);
	// ==================================================
	/**
	 * Similar to {@link TConfigPanelBuilder#nextPanelBottomY(TPanelElement)},
	 * returns the next "free" global Y coordinate at which a child can be placed
	 * without any obstructions from other child elements.
	 * @param parent The {@link TParentElement} to check.
	 * @throws NullPointerException If the {@link TParentElement} is {@code null}.
	 */
	public static final int nextChildBottomY(TParentElement parent) throws NullPointerException
	{ return nextChildBottomY(parent, 0); }
	
	/**
	 * Same as {@link #nextChildBottomY(TParentElement)}, except it also
	 * allows you to define a "padding" for the parent element.
	 * @param parent The {@link TParentElement} to check.
	 * @param defaultScrollPadding Behaves like {@link TPanelElement#getScrollPadding()}.
	 * Will get overridden by {@link TPanelElement#getScrollPadding()} if present.
	 * @throws NullPointerException If the {@link TParentElement} is {@code null}.
	 * @see TPanelElement#getScrollPadding()
	 */
	public static final int nextChildBottomY(TParentElement parent, int defaultScrollPadding)
			throws NullPointerException
	{
		//handle panel element scroll paddings
		Objects.requireNonNull(parent);
		if(parent instanceof TPanelElement parentPanel)
			defaultScrollPadding = parentPanel.getScrollPadding();
		
		//obtain the bottom child element
		@SuppressWarnings("removal")
		@Nullable TElement bottom = parent.getChildren().getTopmostElements().Item2;
		
		//ignore the bottom element if it's too far up out of bounds
		if(bottom != null && bottom.getEndY() <= parent.getY() + defaultScrollPadding)
			bottom = null;
		
		//calculate and return
		return (bottom != null) ? bottom.getEndY(): parent.getY() + defaultScrollPadding;
	}
	// --------------------------------------------------
	/**
	 * Similar to {@link TConfigPanelBuilder#nextPanelVerticalRect(TPanelElement)},
	 * returns the next "free / unoccupied" space on the global Y coordinate for
	 * the next child {@link TParentElement} to be placed at.
	 * @param parent The {@link TParentElement} to check.
	 * @throws NullPointerException If the {@link TParentElement} is {@code null}.
	 */
	public static final Rectangle nextChildVerticalRect(TParentElement parent) throws NullPointerException
	{ return nextChildVerticalRect(parent, 0); }
	
	/**
	 * Same as {@link #nextChildVerticalRect(TParentElement)}, except it also
	 * allows you to define a "padding" for the parent element.
	 * @param parent The {@link TParentElement} to check.
	 * @param defaultScrollPadding Behaves like {@link TPanelElement#getScrollPadding()}.
	 * Will get overridden by {@link TPanelElement#getScrollPadding()} if present.
	 * @throws NullPointerException If the {@link TParentElement} is {@code null}.
	 * @see TPanelElement#getScrollPadding()
	 */
	public static final Rectangle nextChildVerticalRect(TParentElement parent, int defaultScrollPadding)
			throws NullPointerException
	{
		//handle panel element scroll paddings
		Objects.requireNonNull(parent);
		if(parent instanceof TPanelElement parentPanel)
			defaultScrollPadding = parentPanel.getScrollPadding();
		
		//find the next bottom Y (global coordinate),
		//calculate next width,
		//calculate next X
		final var nextY = nextChildBottomY(parent, defaultScrollPadding);
		final int nextW = parent.getWidth() - (defaultScrollPadding * 2);
		final int nextX = parent.getX() + defaultScrollPadding;
		
		//construct and return a rectangle
		return new Rectangle(nextX, nextY, nextW, 20); //next-height defaults to 20 units
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.events;

import io.github.thecsdev.tcdcommons.api.client.gui.TClickableElement;

@Deprecated
public class TClickableElementEvents extends TElementEvents
{
	// ==================================================
	/**
	 * An event invoked when the {@link #getTElement()} is clicked.
	 */
	public final TEvent<Runnable> CLICKED = new TEvent<>();
	// ==================================================
	public TClickableElementEvents(TClickableElement owner) { super(owner); }
	// ==================================================
}
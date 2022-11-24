package thecsdev.tcdcommons.api.client.gui.events;

import thecsdev.tcdcommons.api.client.gui.TClickableElement;

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
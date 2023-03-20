package io.github.thecsdev.tcdcommons.api.client.gui.events;

import java.util.function.Consumer;

import io.github.thecsdev.tcdcommons.api.client.gui.widget.AbstractTSliderWidget;

public class TSliderWidgetEvents extends TClickableElementEvents
{
	// ==================================================
	/**
	 * An event invoked when the value of an {@link AbstractTSliderWidget} changes.<br/>
	 * The first parameter is the new value.
	 */
	public final TEvent<Consumer<Double>> VALUE_CHANGED = new TEvent<>();
	// ==================================================
	public TSliderWidgetEvents(AbstractTSliderWidget owner) { super(owner); }
	// ==================================================
}
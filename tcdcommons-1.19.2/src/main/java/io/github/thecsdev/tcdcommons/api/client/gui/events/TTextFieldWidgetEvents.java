package io.github.thecsdev.tcdcommons.api.client.gui.events;

import java.util.function.Consumer;

import io.github.thecsdev.tcdcommons.api.client.gui.widget.TTextFieldWidget;

@Deprecated
public class TTextFieldWidgetEvents extends TElementEvents
{
	// ==================================================
	/**
	 * An event invoked when the text of a {@link TTextFieldWidget} changes.
	 */
	public final TEvent<Consumer<String>> TEXT_CHANGED = new TEvent<>();
	// ==================================================
	public TTextFieldWidgetEvents(TTextFieldWidget owner) { super(owner); }
	// ==================================================
}

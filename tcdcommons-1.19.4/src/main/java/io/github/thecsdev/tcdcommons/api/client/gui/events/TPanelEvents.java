package io.github.thecsdev.tcdcommons.api.client.gui.events;

import java.util.function.Consumer;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;

public class TPanelEvents extends TElementEvents
{
	// ==================================================
	/**
	 * An event invoked when the {@link TPanelElement} is
	 * scrolled horizontally. The first parameter is deltaX.
	 */
	public final TEvent<Consumer<Integer>> SCROLL_H = new TEvent<>();
	
	/**
	 * An event invoked when the {@link TPanelElement} is
	 * scrolled vertically. The first parameter is deltaY.
	 */
	public final TEvent<Consumer<Integer>> SCROLL_V = new TEvent<>();
	// ==================================================
	public TPanelEvents(TPanelElement owner) { super(owner); }
	// ==================================================
}
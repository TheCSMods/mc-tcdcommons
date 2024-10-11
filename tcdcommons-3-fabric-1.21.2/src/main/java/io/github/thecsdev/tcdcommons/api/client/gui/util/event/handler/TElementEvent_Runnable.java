package io.github.thecsdev.tcdcommons.api.client.gui.util.event.handler;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.event.TEvent;

/**
 * A {@link Runnable}-like {@link TEvent} handler interface.
 */
public interface TElementEvent_Runnable<T extends TElement>
{
	/**
	 * Invokes this {@link TEvent}.
	 * @param element The {@link TElement} that the {@link TEvent} belongs to. Pass 'this'.
	 */
	public void invoke(T element);
}
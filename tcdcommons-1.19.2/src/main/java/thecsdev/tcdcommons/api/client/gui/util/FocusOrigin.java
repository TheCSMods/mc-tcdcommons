package thecsdev.tcdcommons.api.client.gui.util;

import thecsdev.tcdcommons.api.client.gui.TElement;
import thecsdev.tcdcommons.api.client.gui.screen.TScreen;

/**
 * Used to tell a {@link TElement} why a {@link TScreen} is
 * attempting to focus on or focus off of the given {@link TElement}.
 */
public enum FocusOrigin
{
	/** The focus does not originate from any other source in {@link FocusOrigin}. */
	UNKNOWN,
	/** The focus originates from a mouse click. */
	MOUSE_CLICK,
	/** The focus originates from pressing Tab. */
	TAB,
}
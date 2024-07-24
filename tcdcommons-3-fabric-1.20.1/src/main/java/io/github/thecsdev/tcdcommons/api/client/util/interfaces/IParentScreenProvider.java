package io.github.thecsdev.tcdcommons.api.client.util.interfaces;

import net.minecraft.client.gui.screen.Screen;

/**
 * Typically applied to {@link Screen}s that wish to "publicly" provide access to their "parent" {@link Screen}.
 */
public interface IParentScreenProvider
{
	/**
	 * Returns the "parent" {@link Screen} that should be opened when the current one closes.
	 */
	public Screen getParentScreen();
}
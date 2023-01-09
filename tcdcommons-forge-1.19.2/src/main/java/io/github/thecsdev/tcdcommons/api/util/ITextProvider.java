package io.github.thecsdev.tcdcommons.api.util;

import net.minecraft.network.chat.Component;

/**
 * Uses {@link #getIText()} to provide a {@link Text}
 * for a given {@link Object}.<br/>
 * <br/>
 * <b>Important note:</b><br/>
 * To avoid issues with obfuscation, the method
 * is named {@link #getIText()} instead of "getText".
 */
public interface ITextProvider
{
	/**
	 * Returns the textual {@link Component} associated with this object.
	 */
	public Component getIText();
}
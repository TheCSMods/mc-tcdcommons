package io.github.thecsdev.tcdcommons.api.util;

import net.minecraft.text.Text;

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
	 * Returns the {@link Text} associated with this object.
	 */
	public Text getIText();
}
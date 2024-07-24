package io.github.thecsdev.tcdcommons.api.util.interfaces;

import net.minecraft.text.Text;

/**
 * An interface representing an {@link Object} that provides
 * a {@link Text} via {@link #getText()}.
 */
public interface ITextProvider
{
	public Text getText();
}
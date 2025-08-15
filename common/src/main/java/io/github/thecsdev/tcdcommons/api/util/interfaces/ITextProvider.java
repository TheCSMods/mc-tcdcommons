package io.github.thecsdev.tcdcommons.api.util.interfaces;

import net.minecraft.network.chat.Component;

/**
 * An interface representing an {@link Object} that provides
 * a {@link Component} via {@link #getText()}.
 */
public interface ITextProvider
{
	public Component getText();
}
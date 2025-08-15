package io.github.thecsdev.tcdcommons.api.util.interfaces;

import net.minecraft.network.chat.Component;

public interface ITextProviderSetter extends ITextProvider
{
	public void setText(Component text);
}
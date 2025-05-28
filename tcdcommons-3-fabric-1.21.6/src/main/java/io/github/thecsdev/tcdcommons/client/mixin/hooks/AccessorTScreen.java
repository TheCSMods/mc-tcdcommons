package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;

@Mixin(value = TScreen.class, remap = false)
public interface AccessorTScreen
{
	public @Invoker("onOpened") void tcdcommons_onOpened();
	public @Invoker("onClosed") void tcdcommons_onClosed();
}
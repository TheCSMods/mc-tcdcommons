package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.PressableWidget;

@Mixin(PressableWidget.class)
public interface AccessorPressableWidget
{
	static @Accessor("TEXTURES") ButtonTextures getButtonTextures() { throw new AssertionError(); }
}
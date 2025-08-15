package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractButton.class)
public interface AccessorPressableWidget
{
	static @Accessor("SPRITES") WidgetSprites getButtonTextures() { throw new AssertionError(); }
}
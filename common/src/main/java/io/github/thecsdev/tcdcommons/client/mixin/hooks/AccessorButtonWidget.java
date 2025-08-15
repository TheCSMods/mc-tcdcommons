package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Button.class)
public interface AccessorButtonWidget
{
	public @Accessor("onPress") Button.OnPress getOnPress();
	public @Mutable @Accessor("onPress") void setOnPress(Button.OnPress onPress);
}
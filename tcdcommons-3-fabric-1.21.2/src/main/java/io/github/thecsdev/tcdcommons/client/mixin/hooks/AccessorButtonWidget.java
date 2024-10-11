package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.ButtonWidget;

@Mixin(ButtonWidget.class)
public interface AccessorButtonWidget
{
	public @Accessor("onPress") ButtonWidget.PressAction getOnPress();
	public @Mutable @Accessor("onPress") void setOnPress(ButtonWidget.PressAction onPress);
}
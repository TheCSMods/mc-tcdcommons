package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.mixin.TCMInternal.CURRENT_SCISSORS;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext
{
	@Inject(method = "setScissor", at = @At("RETURN"))
	private void onSetScissor(@Nullable ScreenRect rect, CallbackInfo callback)
	{
		if(rect != null) CURRENT_SCISSORS.setBounds(rect.position().x(), rect.position().y(), rect.width(), rect.height());
		else CURRENT_SCISSORS.setBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
}
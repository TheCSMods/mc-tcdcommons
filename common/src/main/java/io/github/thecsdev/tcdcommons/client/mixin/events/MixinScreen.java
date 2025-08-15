package io.github.thecsdev.tcdcommons.client.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.client.gui.screen.ScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen
{
	@Inject(method = "init", at = @At("RETURN"))
	private void onInitPost(Minecraft client, int width, int height, CallbackInfo callback)
	{
		ScreenEvent.INIT_POST.invoker().invoke((Screen)(Object)this);
	}
	
	@Inject(method = "rebuildWidgets", at = @At("RETURN"))
	private void onClearAndInit(CallbackInfo callback)
	{
		ScreenEvent.INIT_POST.invoker().invoke((Screen)(Object)this);
	}
}
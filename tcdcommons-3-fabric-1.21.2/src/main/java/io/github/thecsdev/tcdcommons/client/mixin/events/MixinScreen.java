package io.github.thecsdev.tcdcommons.client.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.client.gui.screen.ScreenEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class MixinScreen
{
	@Inject(method = "init", at = @At("RETURN"))
	private void onInitPost(MinecraftClient client, int width, int height, CallbackInfo callback)
	{
		ScreenEvent.INIT_POST.invoker().invoke((Screen)(Object)this);
	}
	
	@Inject(method = "clearAndInit", at = @At("RETURN"))
	private void onClearAndInit(CallbackInfo callback)
	{
		ScreenEvent.INIT_POST.invoker().invoke((Screen)(Object)this);
	}
}
package io.github.thecsdev.tcdcommons.client.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.client.MinecraftClientEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
{
	// ==================================================
	public @Shadow Window window;
	public @Shadow Screen currentScreen;
	// ==================================================
	@Inject(method = "onResolutionChanged", at = @At("RETURN"))
	public void onResolutionChanged(CallbackInfo callback)
	{
		//invoke the resolution change event
		final int sW = window.getScaledWidth(), sH = window.getScaledHeight();
		MinecraftClientEvent.RESOLUTION_CHANGED.invoker().invoke(sW, sH);
	}
	// --------------------------------------------------
	@Inject(method = "setScreen", at = @At("RETURN"))
	public void onSetScreen(Screen screen, CallbackInfo callback)
	{
		//invoke the screen change event
		if(this.currentScreen == screen)
			MinecraftClientEvent.SET_SCREEN_POST.invoker().invoke(screen);
	}
	// ==================================================
}
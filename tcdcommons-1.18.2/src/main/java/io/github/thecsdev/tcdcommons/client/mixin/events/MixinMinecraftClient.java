package io.github.thecsdev.tcdcommons.client.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.events.TClientEvent;
import io.github.thecsdev.tcdcommons.api.client.events.TClientGuiEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
{
	// ==================================================
	public @Shadow Screen currentScreen;
	// ==================================================
	@Inject(method = "onResolutionChanged", at = @At("RETURN"))
	public void onResolutionChanged(CallbackInfo callback)
	{
		//invoke the resolution change event
		TClientEvent.RESOLUTION_CHANGED.invoker().resolutionChanged();
	}
	// --------------------------------------------------
	@Inject(method = "setScreen", at = @At("RETURN"))
	public void onSetScreen(Screen screen, CallbackInfo callback)
	{
		//invoke the screen change event
		if(this.currentScreen == screen)
			TClientGuiEvent.POST_SET_SCREEN.invoker().postSetScreen(screen);
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
{
	// ==================================================
	@Shadow public Screen currentScreen;
	// ==================================================
	@Inject(method = "onResolutionChanged", at = @At("RETURN"))
	public void onResolutionChanged(CallbackInfo callback)
	{
		//basically update the sizes of hud screens
		TCDCommonsClientRegistry.reInitHudScreens();
	}
	// ==================================================
}
package thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import thecsdev.tcdcommons.api.client.gui.screen.TScreen;

@Mixin(value = InGameHud.class, priority = 1001)
public abstract class MixinInGameHud
{
	@Shadow private MinecraftClient client;
	
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onRender(MatrixStack matrices, float tickDelta, CallbackInfo callback)
	{
		//check if the current screen is a TScreen
		if(client == null || !(client.currentScreen instanceof TScreen))
			return;
		//ask the currently opened TScreen if this hud should be rendered
		if(!((TScreen)client.currentScreen).shouldRenderInGameHud())
			callback.cancel();
	}
}
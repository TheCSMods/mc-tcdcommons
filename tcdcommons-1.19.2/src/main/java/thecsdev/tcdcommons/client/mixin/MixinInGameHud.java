package thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;

@Mixin(value = InGameHud.class, priority = 1001)
public abstract class MixinInGameHud
{
	// ==================================================
	private @Shadow MinecraftClient client;
	private @Shadow int scaledWidth;
	private @Shadow int scaledHeight;
	// ==================================================
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onPreRender(MatrixStack matrices, float tickDelta, CallbackInfo callback)
	{
		//check if the current screen is a TScreen
		if(client == null || !(client.currentScreen instanceof TScreen))
			return;
		//ask the currently opened TScreen if this hud should be rendered
		if(!((TScreen)client.currentScreen).shouldRenderInGameHud())
			callback.cancel();
	}
	// --------------------------------------------------
	@Inject(method = "render", at = @At("TAIL"))
	public void onPostRender(MatrixStack matrices, float tickDelta, CallbackInfo callback)
	{
		//mouse XY centered
		int mX = scaledWidth / 2, mY = scaledHeight / 2;
		//iterate all hud screens
		for(Screen hScreen : TCDCommonsClientRegistry.InGameHud_Screens.values())
			hScreen.render(matrices, mX, mY, tickDelta);
	}
	// ==================================================
}
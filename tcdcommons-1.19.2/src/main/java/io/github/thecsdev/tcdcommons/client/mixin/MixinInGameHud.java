package io.github.thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

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
	private static float tcdcommons_tickDeltaTime = 0;
	
	@Inject(method = "render", at = @At("TAIL"))
	public void onPostRender(MatrixStack matrices, float tickDelta, CallbackInfo callback)
	{
		//keep track of tick delta time
		tcdcommons_tickDeltaTime += tickDelta;
		
		//get mouse XY
		//int mX = scaledWidth / 2, mY = scaledHeight / 2;
		int mX = (int)(this.client.mouse.getX() * scaledWidth / this.client.getWindow().getWidth());
	    int mY = (int)(this.client.mouse.getY() * scaledHeight / this.client.getWindow().getHeight());
	    
		//iterate and render all hud screens
	    boolean tick = tcdcommons_tickDeltaTime > 1;
		for(var hScreen : TCDCommonsClientRegistry.InGameHud_Screens.entrySet())
		{
			//do not handle current screen
			if(client.currentScreen == hScreen.getValue()) continue;
			//render and tick if needed
			hScreen.getValue().render(matrices, mX, mY, tickDelta);
			if(tick) hScreen.getValue().tick();
		}
		
		//clear delta time if ticked
		if(tick) tcdcommons_tickDeltaTime = 0;
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(value = Gui.class, priority = 1001, remap = true)
public abstract class MixinInGameHud
{
	// ==================================================
	private @Shadow Minecraft minecraft;
	private @Shadow int screenWidth;
	private @Shadow int screenHeight;
	// ==================================================
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onPreRender(PoseStack matrices, float tickDelta, CallbackInfo callback)
	{
		//check if the current screen is a TScreen
		if(minecraft == null || !(minecraft.screen instanceof TScreen))
			return;
		//ask the currently opened TScreen if this hud should be rendered
		if(!((TScreen)minecraft.screen).shouldRenderInGameHud())
			callback.cancel();
	}
	// --------------------------------------------------
	private static float tcdcommons_tickDeltaTime = 0;
	
	@Inject(method = "render", at = @At("TAIL"))
	public void onPostRender(PoseStack matrices, float tickDelta, CallbackInfo callback)
	{
		//keep track of tick delta time
		tcdcommons_tickDeltaTime += tickDelta;
		
		//get mouse XY
		//int mX = scaledWidth / 2, mY = scaledHeight / 2;
		int mX = (int)(this.minecraft.mouseHandler.xpos() * screenWidth / this.minecraft.getWindow().getWidth());
	    int mY = (int)(this.minecraft.mouseHandler.ypos() * screenHeight / this.minecraft.getWindow().getHeight());
	    
		//iterate and render all hud screens
	    boolean tick = tcdcommons_tickDeltaTime > 1;
		for(var hScreen : TCDCommonsClientRegistry.InGameHud_Screens.entrySet())
		{
			//do not handle current screen
			if(minecraft.screen == hScreen.getValue()) continue;
			//render and tick if needed
			hScreen.getValue().render(matrices, mX, mY, tickDelta);
			if(tick) hScreen.getValue().tick();
		}
		
		//clear delta time if ticked
		if(tick) tcdcommons_tickDeltaTime = 0;
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.client.mixin.events;

import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.events.client.gui.hud.InGameHudEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.thecsdev.tcdcommons.client.mixin.TCMInternal.CURRENT_T_SCREEN;

@Mixin(value = Gui.class, priority = 1001)
public abstract class MixinInGameHud
{
	// ==================================================
	private @Shadow Minecraft minecraft;
	// ==================================================
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onPreRender(GuiGraphics pencil, DeltaTracker tickCounter, CallbackInfo callback)
	{
		//check if a TScreen is open, and if it allows the in-game-hud to render
		if(CURRENT_T_SCREEN != null && !CURRENT_T_SCREEN.shouldRenderInGameHud())
			callback.cancel();
		
		//invoke the pre-render event
		else if(InGameHudEvent.RENDER_PRE.invoker().invoke(pencil, tickCounter.getGameTimeDeltaPartialTick(false)).isEventCancelled())
			//if the event got cancelled, then also cancel the rendering of the game hud
			callback.cancel();
	}
	// --------------------------------------------------
	@Inject(method = "render", at = @At("RETURN"))
	public void onPostRender(GuiGraphics pencil, DeltaTracker tickCounter, CallbackInfo callback)
	{
		//render in-game-hud screens
		if(TClientRegistries.HUD_SCREEN.size() > 0)
		{
			final var currentScreen = minecraft.screen;
			final var clientWindow = minecraft.getWindow();
			final var mouse = minecraft.mouseHandler;
			
			var mX = mouse.isMouseGrabbed() ? (clientWindow.getScreenWidth() / 2) : mouse.xpos();
			var mY = mouse.isMouseGrabbed() ? (clientWindow.getScreenHeight() / 2) : mouse.ypos();
			int i = (int)(mX * clientWindow.getGuiScaledWidth() / clientWindow.getScreenWidth());
		    int j = (int)(mY * clientWindow.getGuiScaledHeight() / clientWindow.getScreenHeight());
			
			for(final var entry : TClientRegistries.HUD_SCREEN)
			{
				//do not render the current screen
				if(entry.getValue() == currentScreen)
					continue;
				
				//render the screen onto the in-game-hud
				entry.getValue().render(pencil, i, j, tickCounter.getGameTimeDeltaPartialTick(false));
				
				//note: ticking has been deprecated here, to avoid weird visual bugs
			}
		}
		
		//invoke the post render event
		InGameHudEvent.RENDER_POST.invoker().invoke(pencil, tickCounter.getGameTimeDeltaPartialTick(false));
	}
	// ==================================================
}
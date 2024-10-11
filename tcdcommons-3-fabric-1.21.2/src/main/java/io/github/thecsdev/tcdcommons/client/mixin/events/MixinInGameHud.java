package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.mixin.TCMInternal.CURRENT_T_SCREEN;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.events.client.gui.hud.InGameHudEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(value = InGameHud.class, priority = 1001)
public abstract class MixinInGameHud
{
	// ==================================================
	private @Shadow MinecraftClient client;
	// ==================================================
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onPreRender(DrawContext pencil, RenderTickCounter tickCounter, CallbackInfo callback)
	{
		//check if a TScreen is open, and if it allows the in-game-hud to render
		if(CURRENT_T_SCREEN != null && !CURRENT_T_SCREEN.shouldRenderInGameHud())
			callback.cancel();
		
		//invoke the pre-render event
		else if(InGameHudEvent.RENDER_PRE.invoker().invoke(pencil, tickCounter.getTickDelta(false)).isEventCancelled())
			//if the event got cancelled, then also cancel the rendering of the game hud
			callback.cancel();
	}
	// --------------------------------------------------
	@Inject(method = "render", at = @At("RETURN"))
	public void onPostRender(DrawContext pencil, RenderTickCounter tickCounter, CallbackInfo callback)
	{
		//render in-game-hud screens
		if(TClientRegistries.HUD_SCREEN.size() > 0)
		{
			final var currentScreen = client.currentScreen;
			final var clientWindow = client.getWindow();
			final var mouse = client.mouse;
			
			var mX = mouse.isCursorLocked() ? (clientWindow.getWidth() / 2) : mouse.getX();
			var mY = mouse.isCursorLocked() ? (clientWindow.getHeight() / 2) : mouse.getY();
			int i = (int)(mX * clientWindow.getScaledWidth() / clientWindow.getWidth());
		    int j = (int)(mY * clientWindow.getScaledHeight() / clientWindow.getHeight());
			
			for(final var entry : TClientRegistries.HUD_SCREEN)
			{
				//do not render the current screen
				if(entry.getValue() == currentScreen)
					continue;
				
				//render the screen onto the in-game-hud
				entry.getValue().render(pencil, i, j, tickCounter.getTickDelta(false));
				
				//note: ticking has been deprecated here, to avoid weird visual bugs
			}
		}
		
		//invoke the post render event
		InGameHudEvent.RENDER_POST.invoker().invoke(pencil, tickCounter.getTickDelta(false));
	}
	// ==================================================
}
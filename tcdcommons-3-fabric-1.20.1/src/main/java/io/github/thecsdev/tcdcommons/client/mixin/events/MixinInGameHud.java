package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.mixin.CMInternal.CURRENT_T_SCREEN;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.client.gui.hud.InGameHudEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(value = InGameHud.class, priority = 1001)
public abstract class MixinInGameHud
{
	// ==================================================
	private @Shadow MinecraftClient client;
	private @Shadow int scaledWidth;
	private @Shadow int scaledHeight;
	// ==================================================
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onPreRender(DrawContext pencil, float tickDelta, CallbackInfo callback)
	{
		//check if a TScreen is open, and if it allows the in-game-hud to render
		if(CURRENT_T_SCREEN != null && !CURRENT_T_SCREEN.shouldRenderInGameHud())
			callback.cancel();
		
		//invoke the pre-render event
		else if(InGameHudEvent.RENDER_PRE.invoker().invoke(pencil, tickDelta).isEventCancelled())
			//if the event got cancelled, then also cancel the rendering of the game hud
			callback.cancel();
	}
	// --------------------------------------------------
	@Inject(method = "render", at = @At("TAIL"))
	public void onPostRender(DrawContext pencil, float tickDelta, CallbackInfo callback)
	{
		//invoke the post render event
		InGameHudEvent.RENDER_POST.invoker().invoke(pencil, tickDelta);
	}
	// ==================================================
}
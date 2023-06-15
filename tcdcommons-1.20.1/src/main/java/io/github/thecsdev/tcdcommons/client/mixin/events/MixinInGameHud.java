package io.github.thecsdev.tcdcommons.client.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.events.TClientGuiEvent;
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
		//invoke the pre render event
		if(TClientGuiEvent.RENDER_GAME_HUD_PRE.invoker().gameHudRenderPre(pencil, tickDelta).isFalse())
		{
			//if the event got cancelled, then also cancel the rendering of the game hud
			callback.cancel();
			return;
		}
	}
	// --------------------------------------------------
	@Inject(method = "render", at = @At("TAIL"))
	public void onPostRender(DrawContext pencil, float tickDelta, CallbackInfo callback)
	{
		//invoke the post render event
		TClientGuiEvent.RENDER_GAME_HUD_POST.invoker().gameHudRenderPost(pencil, tickDelta);
	}
	// ==================================================
}
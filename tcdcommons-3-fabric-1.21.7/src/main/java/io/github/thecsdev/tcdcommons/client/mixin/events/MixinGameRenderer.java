package io.github.thecsdev.tcdcommons.client.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.client.GameRendererEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class MixinGameRenderer
{
	// ==================================================
	private @Shadow MinecraftClient client;
	// --------------------------------------------------
	private static @Unique boolean TCDC_RCS_FLAG = false;
	// ==================================================
	@Inject(method = "render", at = @At("RETURN"))
	public void onPostRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci)
	{
		if(!client.skipGameRender)
		{
			//invoke the event
			GameRendererEvent.RENDER_POST.invoker().invoke(tickCounter.getTickProgress(false));
		}
	}
	// ==================================================
}
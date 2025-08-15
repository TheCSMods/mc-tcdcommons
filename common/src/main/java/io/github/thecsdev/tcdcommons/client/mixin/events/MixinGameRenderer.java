package io.github.thecsdev.tcdcommons.client.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.client.GameRendererEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class MixinGameRenderer
{
	// ==================================================
	private @Shadow Minecraft minecraft;
	// --------------------------------------------------
	private static @Unique boolean TCDC_RCS_FLAG = false;
	// ==================================================
	@Inject(method = "render", at = @At("RETURN"))
	public void onPostRender(DeltaTracker tickCounter, boolean tick, CallbackInfo ci)
	{
		if(!minecraft.noRender)
		{
			//invoke the event
			GameRendererEvent.RENDER_POST.invoker().invoke(tickCounter.getGameTimeDeltaPartialTick(false));
		}
	}
	// ==================================================
}
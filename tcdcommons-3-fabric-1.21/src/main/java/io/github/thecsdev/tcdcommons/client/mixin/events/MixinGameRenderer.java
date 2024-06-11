package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.KEY_RCS;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.events.client.GameRendererEvent;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;

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
			GameRendererEvent.RENDER_POST.invoker().invoke(tickCounter.getTickDelta(false));
			
			//handle the "Refresh current screen" key
			if(!KEY_RCS.isUnbound())
			{
				//check if the key is currently held
				final var isRcsHeldDown = InputUtil.isKeyPressed(
						client.getWindow().getHandle(),
						KeyBindingHelper.getBoundKeyOf(KEY_RCS).getCode());
				
				//if the key is now held, but wasn't "last frame", that means the
				//key was just pressed. handle the key here
				if(isRcsHeldDown && !TCDC_RCS_FLAG && client.currentScreen != null)
					GuiUtils.initScreen(client.currentScreen);
				
				//after we're done, update the flag so it reflects the
				//keybind's state from the "previous frame"
				TCDC_RCS_FLAG = isRcsHeldDown;
			}
		}
	}
	// ==================================================
}
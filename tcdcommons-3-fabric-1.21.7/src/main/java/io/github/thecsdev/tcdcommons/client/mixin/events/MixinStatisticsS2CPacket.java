package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries.HUD_SCREEN;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IStatsListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;

@Mixin(StatisticsS2CPacket.class)
public class MixinStatisticsS2CPacket
{
	/**
	 * Handles broadcasting {@link StatsListener} event to
	 * {@link TClientRegistries#HUD_SCREEN} {@link Screen}s.
	 */
	@Inject(method = "apply", at = @At("RETURN"), require = 1, remap = true)
	private void tcdcommons_onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo callbackInfo)
	{
		//IMPORTANT: Because this is the network thread, and GUI stuff has to be on main thread; synchronize!
		MC_CLIENT.executeSync(() ->
		{
			//obtain and handle the current screen
			final var currentScreen = MC_CLIENT.currentScreen;
			if(currentScreen instanceof IStatsListener currentStatsListener)
				currentStatsListener.onStatsReady(); //broadcast event to current screen if listening
			
			//iterate all registered hud-screens
			for(final var hudEntry : HUD_SCREEN)
			{
				//obtain screen
				final var hudScreen = hudEntry.getValue();
				
				//skip current screen and non-listeners
				if(hudScreen == currentScreen) continue;
				else if(hudScreen instanceof IStatsListener hudStatsListener)
					hudStatsListener.onStatsReady(); //broadcast event to listeners
			}
		});
	}
}
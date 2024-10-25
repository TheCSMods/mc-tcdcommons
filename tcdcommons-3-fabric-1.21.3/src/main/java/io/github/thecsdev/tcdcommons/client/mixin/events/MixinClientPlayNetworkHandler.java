package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.events.client.MinecraftClientEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
	@Inject(method = "onGameJoin", at = @At("RETURN"))
	public void onOnGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
	{
		//execute the client join world event
		//(ensure this is done on the main thread, and not the network thread)
		MC_CLIENT.executeSync(() ->
		{
			//re-initialize hud screens
			TClientRegistries.reInitHudScreens();
			
			//invoke event
			MinecraftClientEvent.JOINED_WORLD.invoker().invoke(MC_CLIENT, MC_CLIENT.world);
		});
	}
}
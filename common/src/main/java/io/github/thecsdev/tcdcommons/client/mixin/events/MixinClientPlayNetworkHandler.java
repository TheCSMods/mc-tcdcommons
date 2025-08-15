package io.github.thecsdev.tcdcommons.client.mixin.events;

import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.events.client.MinecraftClientEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetworkHandler
{
	@Inject(method = "handleLogin", at = @At("RETURN"))
	public void onOnGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
	{
		//execute the client join world event
		//(ensure this is done on the main thread, and not the network thread)
		MC_CLIENT.executeIfPossible(() ->
		{
			//re-initialize hud screens
			TClientRegistries.reInitHudScreens();
			
			//invoke event
			MinecraftClientEvent.JOINED_WORLD.invoker().invoke(MC_CLIENT, MC_CLIENT.level);
		});
	}
}
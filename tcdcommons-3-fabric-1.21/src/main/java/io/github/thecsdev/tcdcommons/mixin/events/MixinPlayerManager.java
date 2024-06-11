package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.server.PlayerManagerEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager
{
	@Inject(method = "onPlayerConnect", at = @At("TAIL"), require = 1, remap = true)
	public void onPlayerConnectPost(
			ClientConnection connection,
			ServerPlayerEntity player,
			ConnectedClientData ccd,
			CallbackInfo ci)
	{
		//invoke the event
		PlayerManagerEvent.PLAYER_CONNECTED.invoker().invoke(player);
	}
}
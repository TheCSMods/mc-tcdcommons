package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.server.PlayerManagerEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerManager
{
	@Inject(method = "placeNewPlayer", at = @At("TAIL"), require = 1, remap = true)
	public void onPlayerConnectPost(
			Connection connection,
			ServerPlayer player,
			CommonListenerCookie ccd,
			CallbackInfo ci)
	{
		//invoke the event
		PlayerManagerEvent.PLAYER_CONNECTED.invoker().invoke(player);
	}
}
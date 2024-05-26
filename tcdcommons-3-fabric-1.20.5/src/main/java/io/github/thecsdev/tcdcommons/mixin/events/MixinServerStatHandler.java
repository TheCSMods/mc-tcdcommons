package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.network.TcdcServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;

@Mixin(ServerStatHandler.class)
public abstract class MixinServerStatHandler
{
	@Inject(method = "sendStats", at = @At("RETURN"))
	public void onSendStats(ServerPlayerEntity player, CallbackInfo callback)
	{
		TcdcServerPlayNetworkHandler.of(player).sendPlayerBadges();
	}
}
package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.network.TcdcServerPlayNetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatsCounter.class)
public abstract class MixinServerStatHandler
{
	@Inject(method = "sendStats", at = @At("RETURN"))
	public void onSendStats(ServerPlayer player, CallbackInfo callback)
	{
		TcdcServerPlayNetworkHandler.of(player).sendPlayerBadges();
	}
}
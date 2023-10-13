package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.server.PlayerManagerEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

//using higher priority to allow other mods's mixins to cancel events before this mixin handles them
@Mixin(value = ServerPlayNetworkHandler.class, priority = 9001)
public abstract class MixinServerPlayNetworkHandler
{
	public @Shadow ServerPlayerEntity player;
	
	@Inject(method = "onChatMessage", at = @At("TAIL")) //note: has to be tail
	public void onOnChatMessage(ChatMessageC2SPacket packet, CallbackInfo callback)
	{
		PlayerManagerEvent.PLAYER_CHATTED.invoker().invoke(player, packet.chatMessage());
	}
}
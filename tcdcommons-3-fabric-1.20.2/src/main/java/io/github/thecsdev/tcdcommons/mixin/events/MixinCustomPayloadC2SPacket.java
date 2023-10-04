package io.github.thecsdev.tcdcommons.mixin.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadC2SPacket.class)
public abstract class MixinCustomPayloadC2SPacket
{
	private @Shadow CustomPayload payload;
	
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true, require = 1, remap = true)
	private void tcdcommons_onApply(ServerCommonPacketListener serverPlayPacketListener, CallbackInfo callbackInfo)
	{
		//make sure the payload is for this mod's network handler
		if(!(payload instanceof TCustomPayload)) return;
		callbackInfo.cancel();
		final var tPayload = (TCustomPayload)payload;
		
		//# IMPORTANT NOTE - we are currently on the network thread
		//obtain receiver and null-check it
		final var receiverC2S = AccessorCustomPayloadNetwork.getC2S().getOrDefault(tPayload.getPacketDataID(), null);
		if(receiverC2S == null) return;
		
		//obtain the server play network handler if it exists
		final ServerPlayNetworkHandler spnh = (serverPlayPacketListener instanceof ServerPlayNetworkHandler) ?
				(ServerPlayNetworkHandler)serverPlayPacketListener : null;
		final @Nullable PlayerEntity player = (spnh == null ? null : spnh.getPlayer());
		
		//execute receiver
		receiverC2S.receiveCustomPayload(new PacketContext()
		{
			public @Override @Nullable PlayerEntity getPlayer() { return player; }
			public @Override PacketListener getPacketListener() { return serverPlayPacketListener; }
			public @Override Identifier getPacketId() { return tPayload.getPacketDataID(); }
			public @Override PacketByteBuf getPacketBuffer() { return tPayload.getPacketData(); }
			public @Override NetworkSide getNetworkSide() { return NetworkSide.SERVERBOUND; }
		});
	}
}
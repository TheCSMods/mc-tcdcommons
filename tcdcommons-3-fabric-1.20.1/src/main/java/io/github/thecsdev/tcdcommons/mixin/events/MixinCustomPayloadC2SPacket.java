package io.github.thecsdev.tcdcommons.mixin.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadC2SPacket.class)
public abstract class MixinCustomPayloadC2SPacket
{
	public @Shadow PacketByteBuf data;
	public abstract @Shadow Identifier getChannel();
	//public abstract @Shadow PacketByteBuf getData(); -- let's not risk memory leaks, as MC returns copies
	
	// Injecting right after the call to NetworkThreadUtils.forceMainThread method
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true, require = 1, remap = true)
	private void tcdcommons_onApply(ServerPlayPacketListener serverPlayPacketListener, CallbackInfo callbackInfo)
	{
		//# IMPORTANT NOTE - we are currently on the network thread
		//obtain receiver and null-check it
		final var receiverC2S = AccessorCustomPayloadNetwork.getC2S().getOrDefault(getChannel(), null);
		if(receiverC2S == null) return;
		
		//obtain the server play network handler if it exists
		final ServerPlayNetworkHandler spnh = (serverPlayPacketListener instanceof ServerPlayNetworkHandler) ?
				(ServerPlayNetworkHandler)serverPlayPacketListener : null;
		
		//execute receiver
		receiverC2S.receiveCustomPayload(new PacketContext()
		{
			public @Override @Nullable PlayerEntity getPlayer() { return spnh == null ? null : spnh.getPlayer(); }
			public @Override PacketListener getPacketListener() { return serverPlayPacketListener; }
			public @Override Identifier getPacketId() { return getChannel(); }
			public @Override PacketByteBuf getPacketData() { return data; }
			public @Override NetworkSide getNetworkSide() { return NetworkSide.SERVERBOUND; }
		});
		
		//release the packet data, and cancel default
		if(data.refCnt() > 0) data.release();
		callbackInfo.cancel();
	}
}
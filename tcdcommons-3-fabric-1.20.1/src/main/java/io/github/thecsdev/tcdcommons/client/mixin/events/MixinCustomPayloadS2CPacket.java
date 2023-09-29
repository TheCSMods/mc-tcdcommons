package io.github.thecsdev.tcdcommons.client.mixin.events;

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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinCustomPayloadS2CPacket
{
	public @Shadow PacketByteBuf data;
	public abstract @Shadow Identifier getChannel();
	//public abstract @Shadow PacketByteBuf getData(); -- let's not risk memory leaks, as MC returns copies
	
	// Injecting right after the call to NetworkThreadUtils.forceMainThread method
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true, require = 1, remap = true)
	private void tcdcommons_onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo callbackInfo)
	{
		//# IMPORTANT NOTE - we are currently on the network thread
		//obtain receiver and null-check it
		final var receiverS2C = AccessorCustomPayloadNetwork.getS2C().getOrDefault(getChannel(), null);
		if(receiverS2C == null) return;
		
		//execute receiver, and "prevent-default"
		receiverS2C.receiveCustomPayload(new PacketContext()
		{
			public @Override @Nullable PlayerEntity getPlayer() { return null; }
			public @Override PacketListener getPacketListener() { return clientPlayPacketListener; }
			public @Override Identifier getPacketId() { return getChannel(); }
			public @Override PacketByteBuf getPacketBuffer() { return data; }
			public @Override NetworkSide getNetworkSide() { return NetworkSide.CLIENTBOUND; }
		});
		
		//release the packet data, and cancel default
		//if(data.refCnt() > 0) data.release(); -- causes incompatibility issues
		callbackInfo.cancel();
	}
}
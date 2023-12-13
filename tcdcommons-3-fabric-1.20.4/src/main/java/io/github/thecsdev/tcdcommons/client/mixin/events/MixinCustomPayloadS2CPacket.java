package io.github.thecsdev.tcdcommons.client.mixin.events;

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
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinCustomPayloadS2CPacket
{
	private @Shadow CustomPayload payload;
	
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true, require = 1, remap = true)
	private void tcdcommons_onApply(ClientCommonPacketListener clientPlayPacketListener, CallbackInfo callbackInfo)
	{
		//make sure the payload is for this mod's network handler
		if(!(payload instanceof TCustomPayload)) return;
		callbackInfo.cancel();
		final var tPayload = (TCustomPayload)payload;
		
		//# IMPORTANT NOTE - we are currently on the network thread
		//obtain receiver and null-check it
		final var receiverS2C = AccessorCustomPayloadNetwork.getS2C().getOrDefault(tPayload.getPacketDataID(), null);
		if(receiverS2C == null) return;
		
		//execute receiver, and "prevent-default"
		receiverS2C.receiveCustomPayload(new PacketContext()
		{
			public @Override @Nullable PlayerEntity getPlayer() { return null; }
			public @Override PacketListener getPacketListener() { return clientPlayPacketListener; }
			public @Override Identifier getPacketId() { return tPayload.getPacketDataID(); }
			public @Override PacketByteBuf getPacketBuffer() { return tPayload.getPacketData(); }
			public @Override NetworkSide getNetworkSide() { return NetworkSide.CLIENTBOUND; }
		});
	}
}
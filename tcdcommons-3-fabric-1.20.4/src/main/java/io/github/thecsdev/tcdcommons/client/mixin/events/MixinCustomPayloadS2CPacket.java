package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
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
	private void tcdcommons_onApply(ClientCommonPacketListener clientCommonPacketListener, CallbackInfo callbackInfo)
	{
		//make sure the payload is for this mod's network handler
		if(!(payload instanceof TCustomPayload)) return;
		callbackInfo.cancel();
		final var tPayload = (TCustomPayload)payload;
		
		//# IMPORTANT NOTE - we are currently on the network thread
		//obtain receiver and null-check it
		final var receiverS2C = AccessorCustomPayloadNetwork.getPlayS2C().getOrDefault(tPayload.getPacketId(), null);
		if(receiverS2C == null) return;
		
		//execute receiver
		final Runnable executor = () ->
		{
			//return if something changed or client disconnected
			if(MC_CLIENT.getNetworkHandler() != clientCommonPacketListener) return;
			
			//execute the reciever
			final var tPayload_packetId = tPayload.getPacketId();
			final var tPayload_packetPayload = new PacketByteBuf(tPayload.getPacketPayload());
			receiverS2C.receiveCustomPayload(new PacketContext()
			{
				public @Override @Nullable PlayerEntity getPlayer() { return MC_CLIENT.player; }
				public @Override PacketListener getPacketListener() { return clientCommonPacketListener; }
				public @Override Identifier getPacketId() { return tPayload_packetId; }
				public @Override PacketByteBuf getPacketBuffer() { return tPayload_packetPayload; }
				public @Override NetworkSide getNetworkSide() { return NetworkSide.CLIENTBOUND; }
			});
		};
		
		if(MC_CLIENT.player != null) executor.run();
		else TaskScheduler.executeOnce(MC_CLIENT, () -> MC_CLIENT.player != null, executor);
		//^ Warning: Potential to execute on the main thread, which will likely happen as the player is joining
	}
}
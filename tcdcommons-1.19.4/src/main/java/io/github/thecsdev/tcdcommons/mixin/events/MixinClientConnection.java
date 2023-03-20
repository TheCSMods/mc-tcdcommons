package io.github.thecsdev.tcdcommons.mixin.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.TNetworkEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;

@Mixin(value = ClientConnection.class)
public abstract class MixinClientConnection
{
	// ==================================================
	protected @Shadow NetworkSide side;
	protected abstract @Shadow boolean isOpen();
	// ==================================================
	@Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
	public void onPreSendImmediately(Packet<?> packet, @Nullable PacketCallbacks pCallbacks, CallbackInfo callback)
	{
		//check if the connection is open
		if(!isOpen()) return;
		//invoke the event
		if(TNetworkEvent.SEND_PACKET_PRE.invoker().sendPacketPre(packet, side).isFalse())
		{
			//cancel sending the packet if the event is canceled (not recommended) 
			callback.cancel();
			return;
		}
	}
	
	@Inject(
		    method = "channelRead0",
		    at = @At(value = "INVOKE",
		    shift = At.Shift.BEFORE,
		    target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V"),
		    cancellable = true
		)
	private void onPreHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callback)
	{
		//check if the connection is open
		if(!isOpen()) return;
		//invoke the event
		if(TNetworkEvent.RECEIVE_PACKET_PRE.invoker().receivePacketPre(packet, side).isFalse())
		{
			//cancel handling of the packet if the event is canceled (not recommended) 
			callback.cancel();
			return;
		}
	}
	// ==================================================
}
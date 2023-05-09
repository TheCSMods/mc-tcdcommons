package io.github.thecsdev.tcdcommons.mixin.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.TNetworkEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

@Mixin(value = ClientConnection.class)
public abstract class MixinClientConnection
{
	// ==================================================
	protected @Shadow NetworkSide side;
	protected abstract @Shadow boolean isOpen();
	// ==================================================
	@Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
	public void onPreSendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci)
	{
		//check if the connection is open
		if(!isOpen()) return;
		//invoke the event
		if(TNetworkEvent.SEND_PACKET_PRE.invoker().sendPacketPre(packet, side).isFalse())
		{
			//cancel sending the packet if the event is canceled (not recommended) 
			ci.cancel();
			return;
		}
	}
	
	@Inject(method = "handlePacket", at = @At(value = "HEAD"), cancellable = true)
	private static void onPreHandlePacket(Packet<?> packet, PacketListener listener, CallbackInfo callback)
	{
		//check if the connection is open
		if(!listener.getConnection().isOpen()) return;
		//invoke the event
		if(TNetworkEvent.RECEIVE_PACKET_PRE.invoker().receivePacketPre(packet, listener.getConnection().getSide()).isFalse())
		{
			//cancel handling of the packet if the event is canceled (not recommended) 
			callback.cancel();
			return;
		}
	}
	
	@Inject(method = "channelRead0", at = @At("RETURN"))
	private void onPostHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callback)
	{
		//check if the connection is open
		if(!isOpen()) return;
		//invoke the event
		TNetworkEvent.RECEIVE_PACKET_POST.invoker().receivePacketPost(packet, side);
	}
	// ==================================================
}
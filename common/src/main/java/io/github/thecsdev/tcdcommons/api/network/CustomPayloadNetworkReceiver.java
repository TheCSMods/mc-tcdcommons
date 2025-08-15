package io.github.thecsdev.tcdcommons.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * An interface used for registering {@link CustomPayloadNetwork} receivers.
 * @see CustomPayloadNetwork
 */
@FunctionalInterface
public interface CustomPayloadNetworkReceiver
{
	/**
	 * Invoked when a custom payload {@link Packet} is received on a given {@link PacketFlow}.
	 * @param packetContext The context of the received {@link Packet}.
	 * @see PacketContext
	 */
	public void receiveCustomPayload(PacketContext packetContext);
	
	/**
	 * Provides information about the context under which
	 * a given custom payload {@link Packet} was received.
	 */
	public interface PacketContext
	{
		/**
		 * Returns the {@link PacketListener} that received the given custom payload packet.
		 * @see net.minecraft.client.multiplayer.ClientPacketListener
		 * @see net.minecraft.server.network.ServerGamePacketListenerImpl
		 */
		public PacketListener getPacketListener();
		public PacketFlow getNetworkSide();
		public ResourceLocation getPacketId();
		public FriendlyByteBuf getPacketBuffer();
		public @Nullable Player getPlayer();
	}
}
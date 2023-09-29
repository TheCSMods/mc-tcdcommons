package io.github.thecsdev.tcdcommons.api.network;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

/**
 * An interface used for registering {@link CustomPayloadNetwork} receivers.
 * @see CustomPayloadNetwork
 * @see CustomPayloadNetwork#registerReceiver(NetworkSide, Identifier, CustomPayloadNetworkReceiver)
 * @apiNote Code structure is similar to <a href="https://github.com/architectury/architectury-api">Architectury API</a>'s
 * custom payload network handling, for parity and simplicity reasons.
 */
public interface CustomPayloadNetworkReceiver
{
	/**
	 * Invoked when a custom payload {@link Packet} is received on a given {@link NetworkSide}.
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
		 * @see net.minecraft.client.network.ClientPlayNetworkHandler
		 * @see net.minecraft.server.network.ServerPlayNetworkHandler
		 */
		public PacketListener getPacketListener();
		public NetworkSide getNetworkSide();
		public Identifier getPacketId();
		public PacketByteBuf getPacketBuffer();
		public @Nullable PlayerEntity getPlayer();
	}
}
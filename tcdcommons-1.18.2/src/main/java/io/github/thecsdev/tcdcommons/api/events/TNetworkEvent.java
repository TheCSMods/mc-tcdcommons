package io.github.thecsdev.tcdcommons.api.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;

public interface TNetworkEvent
{
	// ==================================================
	/**
	 * See {@link SendPacketPre#sendPacketPre(Packet, NetworkSide)}
	 */
	Event<SendPacketPre> SEND_PACKET_PRE = EventFactory.createEventResult();
	
	/**
	 * See {@link ReceivePacketPre#receivePacketPre(Packet, NetworkSide)}
	 */
	Event<ReceivePacketPre> RECEIVE_PACKET_PRE = EventFactory.createEventResult();
	// ==================================================
	interface SendPacketPre
	{
		/**
		 * An event that is invoked when one logical side sends a {@link Packet}
		 * to the other logical side. For example client sending to server.<br/>
		 * <br/>
		 * <b>Important to note:</b><br/>
		 * If a packet is labeled as "clientbound", it means that it is being sent
		 * from the server to the client. Conversely, if a packet is labeled as "serverbound",
		 * it means that it is being sent from the client to the server.
		 * @param packet The {@link Packet} that is about to be sent.
		 * @param networkSide The current network side. It is important to read the note above.
		 */
		EventResult sendPacketPre(Packet<?> packet, NetworkSide networkSide);
	}
	
	interface ReceivePacketPre
	{
		/**
		 * An event that is invoked when one logical side receives a {@link Packet}
		 * from the other logical side. For example server receiving from client.<br/>
		 * <br/>
		 * <b>Important to note:</b><br/>
		 * If a packet is labeled as "clientbound", it means that it is being sent
		 * from the server to the client. Conversely, if a packet is labeled as "serverbound",
		 * it means that it is being sent from the client to the server.
		 * @param packet The {@link Packet} that was received and is about to be handled by the game.
		 * @param networkSide The current network side. It is important to read the note above.
		 */
		EventResult receivePacketPre(Packet<?> packet, NetworkSide networkSide);
	}
	// ==================================================
}
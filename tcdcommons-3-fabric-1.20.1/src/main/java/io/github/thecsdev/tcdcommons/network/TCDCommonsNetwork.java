package io.github.thecsdev.tcdcommons.network;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.client.network.TcdcClientPlayNetworkHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final @Internal class TCDCommonsNetwork
{
	// ==================================================
	private TCDCommonsNetwork() {}
	// --------------------------------------------------
	public static final int COMMON_MAX_CUSTOM_PAYLOAD_SIZE = 1024 * 5;
	//
	public static final Identifier S2C_PLAYER_BADGES = new Identifier(getModID(), "player_badges");
	//
	public static final Identifier S2C2C_FCPNP = new Identifier(getModID(), "f"); //fractured CPN packet
	//^ fractured CPN packets divide a single large CPN packet into several smaller ones
	// ==================================================
	public static final void init() {}
	static
	{
		// ---------- SINGLEPLAYER/DEDICATED SERVER HANDLERS
		//vanilla custom payload registration for the T-Custom-Payload
		ServerPlayNetworking.registerGlobalReceiver(TCustomPayload.ID, (server, player, playerNh, payload, sender) ->
			TcdcServerPlayNetworkHandler.of(player).onCustomPayloadNetwork(TCustomPayload.read(payload)));
		
		//fractured custom payload network packets
		CustomPayloadNetwork.registerPlayReceiver(NetworkSide.SERVERBOUND, S2C2C_FCPNP, context ->
			TcdcServerPlayNetworkHandler.of((ServerPlayerEntity)context.getPlayer()).onFracturedCustomPayloadNetwork(context));
		
		// ---------- PURE CLIENT-SIDE HANDLERS
		if(TCDCommons.isClient())
		{
			//vanilla custom payload registration for the T-Custom-Payload
			ClientPlayNetworking.registerGlobalReceiver(TCustomPayload.ID, (client, playerNh, payload, sender) ->
				TcdcClientPlayNetworkHandler.of(client.player).onCustomPayloadNetwork(TCustomPayload.read(payload)));
			
			//fractured custom payload network packets
			CustomPayloadNetwork.registerPlayReceiver(NetworkSide.CLIENTBOUND, S2C2C_FCPNP, context ->
				TcdcClientPlayNetworkHandler.of((ClientPlayerEntity)context.getPlayer()).onFracturedCustomPayloadNetwork(context));
			
			//client-sided player badge statistics packet handler
			CustomPayloadNetwork.registerPlayReceiver(NetworkSide.CLIENTBOUND, TCDCommonsNetwork.S2C_PLAYER_BADGES, context ->
				TcdcClientPlayNetworkHandler.of((ClientPlayerEntity)context.getPlayer()).onPlayerBadges(context));
		}
	}
	// ==================================================
	public static final @Internal void sendFracturedCpnPacket(
			TCustomPayload fullPacket,
			long fracturedPacketId,
			Consumer<TCustomPayload> packetSender)
	{
		//requirements
		Objects.requireNonNull(fullPacket);
		Objects.requireNonNull(packetSender);
		if(fracturedPacketId < 1)
			throw new IllegalArgumentException("Fractured packet ID is < 1");
		
		final var fullPacketPayload = fullPacket.getPacketPayload();
		
		//begin
		{
			//send a payload indicating the start of this process
			final var b = new PacketByteBuf(Unpooled.buffer());
			b.writeByte(1);
			b.writeLongLE(fracturedPacketId);
			b.writeString(fullPacket.getPacketId().toString());
			try { packetSender.accept(new TCustomPayload(S2C2C_FCPNP, b)); }
			catch(Exception exc) { return; }
		}
		
		//fractured data transmission
		while(fullPacketPayload.readableBytes() > 0)
		{
			//read the next chunk of data
			final int chunkDataLen = Math.min(fullPacketPayload.readableBytes(), COMMON_MAX_CUSTOM_PAYLOAD_SIZE);
			final var chunkData = fullPacketPayload.readSlice(chunkDataLen);
			
			//send the next chunk of data
			final var b = new PacketByteBuf(Unpooled.buffer());
			b.writeByte(2);
			b.writeLongLE(fracturedPacketId);
			b.writeIntLE(chunkDataLen);
			b.writeBytes(chunkData);
			try { packetSender.accept(new TCustomPayload(S2C2C_FCPNP, b)); }
			catch(Exception exc) { return; }
			
			//...repeat until done
		}
		
		//end
		{
			//send a payload indicating the end of this process
			final var b = new PacketByteBuf(Unpooled.buffer());
			b.writeByte(3);
			b.writeLongLE(fracturedPacketId);
			try { packetSender.accept(new TCustomPayload(S2C2C_FCPNP, b)); }
			catch(Exception exc) { return; }
		}
	}
	
	/**
	 * When a {@link TCustomPayload} is "too large", it needs to be broken up into
	 * smaller individual packets called "fractured CPN packets". These fractured
	 * packets are sent over the network one by one, until they assemble a full
	 * {@link TCustomPayload} packet once all is done.
	 * @param context The {@link PacketContext}.
	 * @param storage The place where fractured CPN packets are temporarily stored.
	 * @param handler The {@link TCustomPayload} handler for once the entire packet is complete.
	 */
	public static final @Internal void handleFracturedCpnPacket(
			PacketContext context,
			Cache<Long, TCustomPayload> storage,
			Consumer<TCustomPayload> handler)
	{
		//obtain the buffer
		final var buffer = context.getPacketBuffer();
		
		//read the "state", and process it;
		switch(buffer.readByte())
		{
			//CASE 1 - The other side is beginning to send over a fractured CPN packet
			case 1:
			{
				//read data
				final var id     = buffer.readLongLE(); //read fractured packet numerical ID
				final var cpnPid = buffer.readString(); //read the CPN packet's ID
				
				//create and start keeping track of the fractured CPN packet
				try
				{
					final var cpnp = new TCustomPayload(
							new Identifier(cpnPid),
							new PacketByteBuf(Unpooled.buffer()));
					storage.put(id, cpnp);
				}
				catch(Exception e) {}
			}
			break;
			//CASE 2 - The other side is sending over the data of a fractured CPN packet
			case 2:
			{
				//read data
				final var id      = buffer.readLongLE(); //read fractured packet numerical ID
				final var dataLen = buffer.readIntLE();  //read fractured packet's data chunk length
				final var data    = buffer.readSlice(dataLen); //read fractured packet's chunk data
				
				//handle the data
				final @Nullable var cpnp = storage.getIfPresent(id);
				if(cpnp != null) cpnp.getPacketPayload().writeBytes(data);
			}
			break;
			//CASE 3 - The other side finished sending over a fractured CPN packet
			case 3:
			{
				//read data
				final var id = buffer.readLongLE(); //read fractured packet numerical ID
				
				//handle the data
				final @Nullable var cpnp = storage.getIfPresent(id);
				if(cpnp != null)
				{
					//invalidate the cache, as it's no longer needed. the transfer is done
					storage.invalidate(id);
					//handle the completed custom payload network packet
					handler.accept(cpnp);
				}
			}
			break;
			default: break;
		}
	}
	// ==================================================
}
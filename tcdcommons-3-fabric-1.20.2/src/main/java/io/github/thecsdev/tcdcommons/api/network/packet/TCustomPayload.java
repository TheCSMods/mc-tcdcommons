package io.github.thecsdev.tcdcommons.api.network.packet;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * A {@link CustomPayload} implementation used by {@link TCDCommons}'s {@link CustomPayloadNetwork}.
 */
public final class TCustomPayload implements CustomPayload
{
	// ==================================================
	/**
	 * The unique {@link Identifier} of {@link TCDCommons}'s {@link CustomPayload}
	 * packets for the {@link CustomPayloadNetwork}.
	 */
	public static final Identifier ID = new Identifier(getModID(), "cpn");
	// --------------------------------------------------
	private final Identifier packetDataID;
	private final PacketByteBuf packetData;
	private final boolean closeOnWrite;
	// ==================================================
	/**
	 * Use this when receiving a {@link TCustomPayload} from the other side.
	 */
	public TCustomPayload(PacketByteBuf receivedPacketBuffer)
	{
		this(receivedPacketBuffer.readIdentifier(),
			new PacketByteBuf(receivedPacketBuffer.readSlice(receivedPacketBuffer.readIntLE())));
	}
	
	/**
	 * Use this when intending to send a {@link TCustomPayload} to the other side.
	 */
	public TCustomPayload(Identifier packetDataID, PacketByteBuf packetData) { this(packetDataID, packetData, true); }
	
	/**
	 * Use this when intending to send a {@link TCustomPayload} to the other side.
	 */
	public TCustomPayload(Identifier packetDataID, PacketByteBuf packetData, boolean closeOnWrite)
	{
		this.packetDataID = Objects.requireNonNull(packetDataID);
		this.packetData = Objects.requireNonNull(packetData);
		this.closeOnWrite = closeOnWrite;
	}
	// ==================================================
	/**
	 * @apiNote Not to be confused with {@link #id()}, aka {@link #ID}.
	 */
	public final Identifier getPacketDataID() { return this.packetDataID; }
	public final PacketByteBuf getPacketData() { return this.packetData; }
	// --------------------------------------------------
	public final @Override Identifier id() { return ID; }
	public final @Override void write(PacketByteBuf buf)
	{
		//if the data is released, write nothing
		if(packetData.refCnt() == 0)
			return;
		
		//write the data
		buf.writeIdentifier(this.packetDataID); //write the data ID
		buf.writeIntLE(this.packetData.readableBytes()); //write the data length
		buf.writeBytes(this.packetData); //and finally, write the data
		
		//handle closing
		if(closeOnWrite && this.packetData.refCnt() > 0)
			this.packetData.release();
	}
	// ==================================================
	/**
	 * Sends this {@link TCustomPayload} from the {@link MinecraftClient} to the connected server.
	 * @apiNote Attempting to execute this from a dedicated server will result in a crash!
	 */
	public final void sendC2S()
	{
		final var packet = new CustomPayloadC2SPacket(this);
		TCDCommonsClient.MC_CLIENT.getNetworkHandler().sendPacket(packet);
	}
	// --------------------------------------------------
	/**
	 * Sends this {@link TCustomPayload} from a {@link MinecraftServer} to a connected client.
	 * @param player The client aka player to send this packet to.
	 */
	public final void sendS2C(ServerPlayerEntity player)
	{
		final var packet = new CustomPayloadS2CPacket(this);
		player.networkHandler.sendPacket(packet);
	}
	// ==================================================
}
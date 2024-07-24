package io.github.thecsdev.tcdcommons.api.network.packet;

import static io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork.CPN_PACKET_ID;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * An internal implementation of "CustomPayload" for {@link TCDCommons}.
 * @apiNote A "CustomPayload" implementation for the {@link CustomPayloadNetwork}.
 * @apiNote {@link Internal}. Please do not try to interact with this directly.
 */
@Internal
public final class TCustomPayload
{
	// ==================================================
	public static final Identifier ID = CPN_PACKET_ID;
	// --------------------------------------------------
	private final Identifier packetId;
	private final ByteBuf    packetPayload;
	// ==================================================
	public TCustomPayload(Identifier packetId, ByteBuf packetPayload)
	{
		this.packetId      = Objects.requireNonNull(packetId);
		this.packetPayload = Objects.requireNonNull(packetPayload);
	}
	// --------------------------------------------------
	@SuppressWarnings("removal")
	protected final @Override void finalize() throws Throwable
	{
		//finalize this
		try
		{
			//release payload if unreleased
			if(this.packetPayload.refCnt() > 0)
				this.packetPayload.release();
		}
		catch (Exception e) {}
		finally { super.finalize(); } //finalize super
	}
	// ==================================================
	public final Identifier getPacketId() { return  this.packetId; }
	public final ByteBuf getPacketPayload() { return this.packetPayload; }
	// --------------------------------------------------
	public final void write(PacketByteBuf buf)
	{
		//if the data is released, write nothing
		if(packetPayload.refCnt() == 0)
			return;
		
		//write the data
		buf.writeIdentifier(this.packetId); //write the data ID
		buf.writeIntLE(this.packetPayload.readableBytes()); //write the data length
		buf.writeBytes(this.packetPayload); //and finally, write the data
		
		//handle closing
		if(this.packetPayload.refCnt() > 0)
			this.packetPayload.release();
	}
	// --------------------------------------------------
	public static final TCustomPayload read(PacketByteBuf buf)
	{
		final var packetId         = buf.readIdentifier();
		final byte[] packetPayload = new byte[buf.readIntLE()];
		buf.readBytes(packetPayload);
		return new TCustomPayload(packetId, Unpooled.wrappedBuffer(packetPayload));
	}
	// ==================================================
}
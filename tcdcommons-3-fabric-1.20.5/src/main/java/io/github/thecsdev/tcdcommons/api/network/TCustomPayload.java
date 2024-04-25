package io.github.thecsdev.tcdcommons.api.network;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Objects;

import static io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork.CPN_PACKET_ID;

/**
 * An internal implementation of {@link CustomPayload} for {@link TCDCommons}.
 * @apiNote There's nothing useful here. You may ignore this file.
 */
@ApiStatus.Internal
public final class TCustomPayload implements CustomPayload
{
	// ==================================================
	public static final Id<TCustomPayload> ID = new Id<>(CPN_PACKET_ID);
	public static final PacketCodec<PacketByteBuf, TCustomPayload> CODEC;
	// --------------------------------------------------
	private final Identifier packetId;
	private final ByteBuf packetPayload;
	// ==================================================
	static
	{
		CODEC = PacketCodec.of(TCustomPayload::encode, TCustomPayload::decode);
	}
	// --------------------------------------------------
	public TCustomPayload(Identifier packetId, ByteBuf packetPayload)
	{
		this.packetId = Objects.requireNonNull(packetId);
		this.packetPayload = Objects.requireNonNull(packetPayload);
	}
	// ==================================================
	public final @Override Id<? extends CustomPayload> getId() { return ID; }
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
		finally { super.finalize(); } //finialize super
	}
	// ==================================================
	public final Identifier getPacketId() { return  this.packetId; }
	public final ByteBuf getPacketPayload() { return this.packetPayload; }
	// ==================================================
	/**
	 * Encodes a {@link TCustomPayload} to a {@link PacketByteBuf}.
	 * @param value The {@link TCustomPayload} to encode.
	 * @param buffer The {@link PacketByteBuf} to encode to.
	 */
	private static void encode(TCustomPayload value, PacketByteBuf buffer)
	{
		//null-checks
		Objects.requireNonNull(value);
		Objects.requireNonNull(buffer);

		//obtain info that is to be encoded
		final var packetId = value.packetId;
		final var packetData = value.packetPayload;

		//encode the info
		buffer.writeIdentifier(packetId);
		buffer.writeIntLE(packetData.readableBytes());
		buffer.writeBytes(packetData);
	}
	// --------------------------------------------------
	/**
	 * Decodes a {@link TCustomPayload} from a {@link PacketByteBuf}.
	 * @param buffer The {@link PacketByteBuf} to decode from.
	 * @return The decoded {@link TCustomPayload}.
	 */
	private static TCustomPayload decode(PacketByteBuf buffer)
	{
		final var packetId = buffer.readIdentifier();
		final var packetDataLen = buffer.readIntLE();
		final var packetDataBytes = new byte[packetDataLen];
		buffer.readBytes(packetDataBytes);
		return new TCustomPayload(packetId, Unpooled.wrappedBuffer(packetDataBytes));
	}
	// ==================================================
}
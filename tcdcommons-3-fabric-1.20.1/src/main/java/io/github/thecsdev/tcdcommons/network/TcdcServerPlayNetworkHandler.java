package io.github.thecsdev.tcdcommons.network;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.network.TCDCommonsNetwork.COMMON_MAX_CUSTOM_PAYLOAD_SIZE;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * TCDCommons server play network handler.<br/>
 * Keeps track of {@link ServerPlayerEntity} data that is related to {@link TCDCommons}.
 */
public final @Internal class TcdcServerPlayNetworkHandler
{
	// ==================================================
	/**
	 * The unique {@link Identifier} for obtaining an instance of this network
	 * handler for a given player entity, via {@link EntityHooks#getCustomData(Entity)}.
	 */
	private static final Identifier CUSTOM_DATA_ID = new Identifier(getModID(), "server_play_network_handler");
	// ==================================================
	private final ServerPlayerEntity player;
	// --------------------------------------------------
	private long nextS2CFracturedCpnPacketId = 0;
	private final Cache<Long, TCustomPayload> fracturedC2SCpnPackets;
	// ==================================================
	private TcdcServerPlayNetworkHandler(ServerPlayerEntity player) throws NullPointerException
	{
		this.player = Objects.requireNonNull(player);
		this.fracturedC2SCpnPackets = CacheBuilder.newBuilder()
				.expireAfterAccess(1, TimeUnit.MINUTES)
				.build();
		TaskScheduler.schedulePeriodicCacheCleanup(this.fracturedC2SCpnPackets);
	}
	// --------------------------------------------------
	public final ServerPlayerEntity getPlayer() { return this.player; }
	public final long nextS2CFracturedCpnPacketId()
	{
		//increase next ID by 1, and handle overflows (do not allow values < 1)
		return this.nextS2CFracturedCpnPacketId =
				Math.max(this.nextS2CFracturedCpnPacketId + 1, 1);
	}
	// ==================================================
	/**
	 * Handles a client sending {@link TCustomPayload} packets to this server.
	 * Aka handles the {@link CustomPayloadNetwork}.
	 */
	public final void onCustomPayloadNetwork(TCustomPayload payload)
	{
		//read the payload id and data
		final var packetId = payload.getPacketId();
		final var packetData = new PacketByteBuf(payload.getPacketPayload());

		//find the handler
		final @Nullable var handler = AccessorCustomPayloadNetwork.getPlayC2S().getOrDefault(packetId, null);
		if(handler == null) return;

		//handle the event
		handler.receiveCustomPayload(new PacketContext()
		{
			public ServerPlayerEntity getPlayer() { return player; }
			public PacketListener getPacketListener() { return player.networkHandler; }
			public NetworkSide getNetworkSide() { return NetworkSide.SERVERBOUND; }

			public Identifier getPacketId() { return packetId; }
			public PacketByteBuf getPacketBuffer() { return packetData; }
		});
	}

	/**
	 * Handles "fractured" {@link CustomPayloadNetwork} packets.
	 * @see TCDCommonsNetwork#handleFracturedCpnPacket(PacketContext, Cache, Consumer)
	 */
	public final void onFracturedCustomPayloadNetwork(PacketContext context)
	{
		TCDCommonsNetwork.handleFracturedCpnPacket(
				context,
				this.fracturedC2SCpnPackets,
				this::onCustomPayloadNetwork);
	}
	// ==================================================
	/**
	 * Sends a {@link TCustomPayload} packet to the other side.
	 * @see CustomPayloadNetwork#sendS2C(ServerPlayerEntity, Identifier, ByteBuf)
	 */
	public final void sendCustomPayloadNetwork(Identifier packetId, ByteBuf packetData)
			throws IllegalStateException, NullPointerException
	{
		//requirements
		Objects.requireNonNull(packetId);
		Objects.requireNonNull(packetData);
		if(packetData.refCnt() < 1) throw new IllegalStateException("REF_CNT");
		
		//prepare the payload
		final var payload = new TCustomPayload(packetId, packetData);
		
		//handle events where the payload is too large
		//(in those cases, they aren't sent directly in full)
		if(packetData.readableBytes() > COMMON_MAX_CUSTOM_PAYLOAD_SIZE)
		{
			TCDCommonsNetwork.sendFracturedCpnPacket(
					payload,
					nextS2CFracturedCpnPacketId(),
					fp -> this.player.networkHandler.sendPacket(new CustomPayloadS2CPacket(
							fp.getPacketId(),
							new PacketByteBuf(fp.getPacketPayload())
						)));
			return;
		}
		
		//send data in full, at once, if possible
		else this.player.networkHandler.sendPacket(new CustomPayloadS2CPacket(packetId, new PacketByteBuf(packetData)));
		//else ServerPlayNetworking.send(this.player, packetId, new PacketByteBuf(packetData));
	}
	
	/**
	 * Sends the {@link #player} their player badge statistics.
	 * @return {@code true} if the packet was sent, and {@code false} if the packet was not
	 * sent because the player doesn't have any {@link PlayerBadge}s assigned to them.
	 */
	public final boolean sendPlayerBadges()
	{
		return ServerPlayerBadgeHandler.getServerBadgeHandler(this.player).sendStats(this.player);
	}
	// ==================================================
	/**
	 * Returns an instance of {@link TcdcServerPlayNetworkHandler} for a given
	 * {@link ServerPlayerEntity}. Creates one if it doesn't exist yet.
	 * @param player The {@link ServerPlayerEntity}.
	 */
	public static final TcdcServerPlayNetworkHandler of(ServerPlayerEntity player) throws NullPointerException
	{
		final var cd = EntityHooks.getCustomData(Objects.requireNonNull(player));
		@Nullable TcdcServerPlayNetworkHandler spnh = cd.getProperty(CUSTOM_DATA_ID);
		if(spnh == null)
		{
			spnh = new TcdcServerPlayNetworkHandler(player);
			cd.setProperty(CUSTOM_DATA_ID, spnh);
		}
		return spnh;
	}
	// ==================================================
}
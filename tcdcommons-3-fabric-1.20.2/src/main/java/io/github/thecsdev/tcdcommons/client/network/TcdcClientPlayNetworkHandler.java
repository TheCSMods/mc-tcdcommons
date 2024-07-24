package io.github.thecsdev.tcdcommons.client.network;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;
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
import io.github.thecsdev.tcdcommons.api.client.badge.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.network.PlayerBadgeNetworkListener;
import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

/**
 * TCDCommons server play network handler.<br/>
 * Keeps track of {@link ClientPlayerEntity} data that is related to {@link TCDCommons}.
 */
public final @Internal class TcdcClientPlayNetworkHandler
{
	// ==================================================
	/**
	 * The unique {@link Identifier} for obtaining an instance of this network
	 * handler for a given player entity, via {@link EntityHooks#getCustomData(Entity)}.
	 */
	private static final Identifier CUSTOM_DATA_ID = new Identifier(getModID(), "client_play_network_handler");
	// ==================================================
	private final ClientPlayerEntity player;
	// --------------------------------------------------
	private long nextC2SFracturedCpnPacketId = 0;
	private final Cache<Long, TCustomPayload> fracturedS2CCpnPackets;
	// ==================================================
	private TcdcClientPlayNetworkHandler(ClientPlayerEntity player) throws NullPointerException
	{
		this.player = Objects.requireNonNull(player);
		this.fracturedS2CCpnPackets = CacheBuilder.newBuilder()
				.expireAfterAccess(1, TimeUnit.MINUTES)
				.build();
		TaskScheduler.schedulePeriodicCacheCleanup(this.fracturedS2CCpnPackets);
	}
	// --------------------------------------------------
	public final ClientPlayerEntity getPlayer() { return this.player; }
	public final long nextC2SFracturedCpnPacketId()
	{
		//increase next ID by 1, and handle overflows (do not allow values < 1)
		return this.nextC2SFracturedCpnPacketId =
				Math.max(this.nextC2SFracturedCpnPacketId + 1, 1);
	}
	// ==================================================
	/**
	 * Handles a server sending {@link TCustomPayload} packets to this client.
	 * Aka handles the {@link CustomPayloadNetwork}.
	 */
	public final void onCustomPayloadNetwork(TCustomPayload payload)
	{
		//read the payload id and data
		final var packetId = payload.getPacketId();
		final var packetData = new PacketByteBuf(payload.getPacketPayload());

		//find the handler
		final @Nullable var handler = AccessorCustomPayloadNetwork.getPlayS2C().getOrDefault(packetId, null);
		if(handler == null) return;

		//handle the event
		handler.receiveCustomPayload(new PacketContext()
		{
			public ClientPlayerEntity getPlayer() { return player; }
			public PacketListener getPacketListener() { return player.networkHandler; }
			public NetworkSide getNetworkSide() { return NetworkSide.CLIENTBOUND; }

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
				this.fracturedS2CCpnPackets,
				this::onCustomPayloadNetwork);
	}
	// --------------------------------------------------
	/**
	 * Handles a server sending a custom payload containing
	 * player badge statistics of the {@link #player}.
	 */
	public final void onPlayerBadges(PacketContext context)
	{
		final var payload = context.getPacketBuffer();
		
		// ---------- KEEPING TRACK OF THE STATS CLIENT-SIDE
		//prepare
		final var statHandler = ClientPlayerBadge.getClientPlayerBadgeHandler(this.player);
		
		//read how many badges are in the payload,
		//and then read and add the badges to the array
		var badgeCount = payload.readInt();
		for(int index = 0; index < badgeCount; index++)
		{
			//DoS attack prevention
			if(payload.readableBytes() < 2) break;
			
			//read badge
			final Identifier badgeId = payload.readIdentifier();
			final int badgeValue = payload.readVarInt();
			
			final @Nullable PlayerBadge badge = PLAYER_BADGE.getValue(badgeId).orElse(null);
			if(badge == null || badge instanceof ClientPlayerBadge)
				continue;
			
			//add badge
			statHandler.setValue(badgeId, badgeValue);
		}
		
		// ---------- PASSING THE EVENT TO ANY POTENTIAL LISTENER SCREENS
		MC_CLIENT.executeSync(() ->
		{
			//obtain the client instance, and check if a listener Screen is present
			final var currentScreen = MC_CLIENT.currentScreen;
			if(currentScreen instanceof PlayerBadgeNetworkListener listener)
				//invoke the listener event (on the main thread, to prevent threading and concurrency issues)
				listener.onPlayerBadgesReady();
		});
	}
	// ==================================================
	/**
	 * Sends a {@link TCustomPayload} packet to the other side.
	 * @see CustomPayloadNetwork#sendC2S(Identifier, ByteBuf)
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
					nextC2SFracturedCpnPacketId(),
					fp -> this.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(fp)));
			return;
		}
		
		//send data in full, at once, if possible
		else this.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(payload));
		//else ClientPlayNetworking.send(packetId, new PacketByteBuf(packetData));
	}
	// ==================================================
	/**
	 * Returns an instance of {@link TcdcClientPlayNetworkHandler} from a given
	 * {@link ClientPlayerEntity}. Creates one if it doesn't exist.
	 * @param player The {@link ClientPlayerEntity}.
	 */
	public static final TcdcClientPlayNetworkHandler of(ClientPlayerEntity player) throws NullPointerException
	{
		final var cd = EntityHooks.getCustomData(Objects.requireNonNull(player));
		@Nullable TcdcClientPlayNetworkHandler cpnh = cd.getProperty(CUSTOM_DATA_ID);
		if(cpnh == null)
		{
			cpnh = new TcdcClientPlayNetworkHandler(player);
			cd.setProperty(CUSTOM_DATA_ID, cpnh);
		}
		return cpnh;
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.network;

import io.github.thecsdev.tcdcommons.api.network.TCustomPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadNetwork;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class TCDCommonsNetworkHandler extends Object
{
	// ==================================================
	/**
	 * The unique {@link Identifier} of the {@link CustomPayloadS2CPacket}
	 * that sends a client their list of {@link PlayerBadge}s.
	 */
	public static final Identifier S2C_PLAYER_BADGES = new Identifier(TCDCommons.getModID(), "player_badges");
	// ==================================================
	private TCDCommonsNetworkHandler() {}
	public static @Internal void init() {/*calls static*/}
	static
	{
		//register TCDCommons's Custom Payload
		PayloadTypeRegistry.configurationC2S().register(TCustomPayload.ID, TCustomPayload.CODEC);
		PayloadTypeRegistry.configurationS2C().register(TCustomPayload.ID, TCustomPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(TCustomPayload.ID, TCustomPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(TCustomPayload.ID, TCustomPayload.CODEC);

		//obtain CPN packet ID
		//final var cpnPacketId = AccessorCustomPayloadNetwork.getCpnPacketId();
		final var c2s = AccessorCustomPayloadNetwork.getC2S();
		final var s2c = AccessorCustomPayloadNetwork.getS2C();
		
		//register receivers
		if(TCDCommons.isClient()) ClientPlayNetworking.registerGlobalReceiver(TCustomPayload.ID, (payload, context) ->
		{
			//immediately try to cast the player
			final var player = (ClientPlayerEntity)context.player();

			//read the payload id and data
			final var packetId = payload.getPacketId();
			final var packetData = new PacketByteBuf(payload.getPacketPayload());

			//find the handler
			final @Nullable var handler = s2c.getOrDefault(packetId, null);
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
		});
		ServerPlayNetworking.registerGlobalReceiver(TCustomPayload.ID, (payload, context) ->
		{
			//immediately try to cast the player
			final var player = (ServerPlayerEntity)context.player();

			//read the payload id and data
			final var packetId = payload.getPacketId();
			final var packetData = new PacketByteBuf(payload.getPacketPayload());

			//find the handler
			final @Nullable var handler = c2s.getOrDefault(packetId, null);
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
		});
	}
	// ==================================================
	/**
	 * Sends a given {@link ServerPlayerEntity} a list of
	 * their {@link PlayerBadge}s that have been assigned to them.
	 * @param player The target {@link ServerPlayerEntity}.
	 * @return True if the packet was sent, and false if the packet was not sent
	 * because the player doesn't have any {@link PlayerBadge}s assigned to them.
	 */
	public static boolean s2c_sendPlayerBadges(ServerPlayerEntity player)
	{
		return ServerPlayerBadgeHandler.getServerBadgeHandler(player).sendStats(player);
	}
	// ==================================================
}
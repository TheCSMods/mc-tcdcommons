package io.github.thecsdev.tcdcommons.network;

import java.util.HashMap;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.collect.ImmutableMap;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadC2SPacket;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorCustomPayloadS2CPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
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
		/* because the vanilla game makes their maps immutable,
		 * this here is created with the intent to override vanilla, and make the maps mutable
		 */
		//obtain the maps and ensure they are mutable
		final var ogC2S = AccessorCustomPayloadC2SPacket.getIdToReader();
		final var ogS2C = AccessorCustomPayloadS2CPacket.getIdToReader();
		final boolean immutable = (ogC2S instanceof ImmutableMap) || (ogS2C instanceof ImmutableMap);
		
		final var c2s = immutable ? new HashMap<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>>() : ogC2S;
		final var s2c = immutable ? new HashMap<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>>() : ogS2C;
		
		//put vanilla and possibly modded entries into the new maps
		c2s.putAll(AccessorCustomPayloadC2SPacket.getIdToReader());
		s2c.putAll(AccessorCustomPayloadS2CPacket.getIdToReader());
		
		//register TCDCommons's payload
		c2s.put(TCustomPayload.ID, TCustomPayload::new);
		s2c.put(TCustomPayload.ID, TCustomPayload::new);
		
		//override vanilla's immutable maps
		if(immutable)
		{
			AccessorCustomPayloadC2SPacket.setIdToReader(c2s);
			AccessorCustomPayloadS2CPacket.setIdToReader(s2c);
		}
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
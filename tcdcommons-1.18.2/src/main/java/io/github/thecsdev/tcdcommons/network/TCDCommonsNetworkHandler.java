package io.github.thecsdev.tcdcommons.network;

import static io.github.thecsdev.tcdcommons.TCDCommons.LOGGER;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.features.player.badges.ServerPlayerBadgeHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TCDCommonsNetworkHandler
{
	// ==================================================
	/**
	 * The unique {@link Identifier} of the {@link CustomPayloadS2CPacket}
	 * that sends a client their list of {@link PlayerBadge}s.
	 */
	public static final Identifier S2C_PLAYER_BADGES = new Identifier(TCDCommons.getModID(), "player_badges");
	// ==================================================
	protected TCDCommonsNetworkHandler() {}
	public static void init() {/*calls static*/}
	// ==================================================
	static {}
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
		//obtain player badges
		final var badges = ServerPlayerBadgeHandler.getSessionBadgeHandler(player).getBages().toArray(new Identifier[0]);
		if(badges.length == 0) return false; //network optimization - BEWARE
		
		//write player badges to a buffer
		final var data = new PacketByteBuf(Unpooled.buffer());
		data.writeInt(badges.length);
		for(Identifier badgeId : badges)
			if(badgeId != null) data.writeIdentifier(badgeId);
		//create and send packet
		final var packet = new CustomPayloadS2CPacket(S2C_PLAYER_BADGES, data);
		try { player.networkHandler.sendPacket(packet); }
		catch(Exception e) { LOGGER.debug("Failed to send " + S2C_PLAYER_BADGES + " packet; " + e.getMessage()); }
		return true;
	}
	// ==================================================
}

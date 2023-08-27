package io.github.thecsdev.tcdcommons.network;

import static io.github.thecsdev.tcdcommons.TCDCommons.LOGGER;

import java.util.Arrays;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
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
	public static void init() {/*calls static*/}
	static
	{
		//nothing in here for now...
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
		//check if badges are enabled - don't send packed if disabled
		if(!TCDCommons.getInstance().getConfig().enablePlayerBadges)
			return false;
		
		//obtain player badges
		final var badges = ServerPlayerBadgeHandler.getServerBadgeHandler(player).toArray();
		if(badges.length == 0) return false; //network optimization - BEWARE
		
		// Split badges into chunks and send each chunk
		//this is done to avoid hitting packet length limits
		final int chunkSize = 15;
		for (int i = 0; i < badges.length; i += chunkSize)
		{
			int end = Math.min(i + chunkSize, badges.length);
			Identifier[] badgeChunk = Arrays.copyOfRange(badges, i, end);
			s2c_sendPlayerBadges(player, badgeChunk);
		}
		
		//return true once done
		return true;
	}
	// --------------------------------------------------
	/**
	 * Sends a smaller "chunk" of {@link PlayerBadge} {@link Identifier}s, rather than
	 * a whole collection, so as to avoid hitting the maximum packet length limit.
	 */
	private static void s2c_sendPlayerBadges(ServerPlayerEntity player, Identifier[] badgeChunk)
	{
		//write player badges to a buffer
		final var data = new PacketByteBuf(Unpooled.buffer());
		data.writeInt(badgeChunk.length);
		for(Identifier badgeId : badgeChunk) { if(badgeId != null) data.writeIdentifier(badgeId); }
		
		//create and send packet
		try
		{
			final var packet = new CustomPayloadS2CPacket(S2C_PLAYER_BADGES, data);
			player.networkHandler.sendPacket(packet);
		}
		catch(Exception e)
		{
			LOGGER.debug("Failed to send " + S2C_PLAYER_BADGES + " packet; " + e.getMessage());
			throw e;
		}
	}
	// ==================================================
}
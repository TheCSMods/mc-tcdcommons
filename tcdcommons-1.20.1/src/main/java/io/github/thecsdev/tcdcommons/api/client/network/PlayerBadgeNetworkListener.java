package io.github.thecsdev.tcdcommons.api.client.network;

import com.google.common.collect.BiMap;

import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.client.network.TCDCommonsClientNetworkHandler;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetworkHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

/**
 * A network listener for {@link Screen}s that wish to
 * listen for {@link PlayerBadge} S2C packets.
 */
public interface PlayerBadgeNetworkListener
{
	// ==================================================
	/**
	 * Called by {@link TCDCommonsClientNetworkHandler}
	 * when the server sends the client their {@link PlayerBadge} list.<br/>
	 * <br/>
	 * <b>Important:</b><br/>
	 * The server will <b>not</b> send a {@link PlayerBadge} packet if the
	 * player does not have any {@link PlayerBadge}s assigned to them. This
	 * is done for network optimization reasons. Always assume there are no
	 * badges assigned until the server sends a {@link PlayerBadge} packet.
	 * @param badgeIDs The array of {@link Identifier}s for the {@link PlayerBadge}s.
	 * @see TCDCommonsNetworkHandler#S2C_PLAYER_BADGES
	 * @see PlayerBadge#getBadgeId()
	 * @see PlayerBadge#getBadgeId(com.google.common.collect.BiMap)
	 */
	public void onPlayerBadgesReady(BiMap<Identifier, PlayerBadge> badges);
	// ==================================================
}
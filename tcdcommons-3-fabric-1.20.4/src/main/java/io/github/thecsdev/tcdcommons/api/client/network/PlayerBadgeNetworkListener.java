package io.github.thecsdev.tcdcommons.api.client.network;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.badge.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.client.network.TCDCommonsClientNetworkHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * A network listener for {@link Screen}s that wish to
 * listen for {@link PlayerBadge} S2C packets.
 */
public interface PlayerBadgeNetworkListener
{
	// ==================================================
	/**
	 * Called by {@link TCDCommonsClientNetworkHandler} when the
	 * server sends the client their {@link PlayerBadge} statistics.
	 * 
	 * @apiNote <b>Important:</b> The server will <b>not</b> send a
	 * {@link PlayerBadge} packet if the player does not have any
	 * {@link PlayerBadge}s assigned to them. This is done for network
	 * optimization reasons. Always assume there are no badges assigned
	 * until the server sends a {@link PlayerBadge} packet.
	 * 
	 * @apiNote Use {@link ClientPlayerBadge#getClientPlayerBadgeHandler(ClientPlayerEntity)}
	 * to obtain the {@link PlayerBadge} statistics.
	 */
	public void onPlayerBadgesReady();
	// ==================================================
}
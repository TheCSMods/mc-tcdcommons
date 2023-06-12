package io.github.thecsdev.tcdcommons.client.network;

import static io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry.PlayerBadges;

import com.google.common.collect.HashBiMap;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.Side;
import io.github.thecsdev.tcdcommons.api.client.features.player.badges.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.network.PlayerBadgeNetworkListener;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class TCDCommonsClientNetworkHandler
{
	// ==================================================
	protected TCDCommonsClientNetworkHandler() {}
	public static void init() {/*calls static*/}
	// ==================================================
	static
	{
		// ---------- register network handlers
		//receiving player badges
		NetworkManager.registerReceiver(Side.S2C, TCDCommonsNetworkHandler.S2C_PLAYER_BADGES, (payload, context) ->
		{
			//obtain the client instance, and check if a listener Screen is present
			final var client = MinecraftClient.getInstance();
			if(!(client.currentScreen instanceof PlayerBadgeNetworkListener))
				return;
			final var listener = (PlayerBadgeNetworkListener)client.currentScreen;
			
			//prepare a list
			final HashBiMap<Identifier, PlayerBadge> pBadges = HashBiMap.create();
			
			//read how many badges are in the payload,
			//and then read and add the badges to the array
			var badgeCount = payload.readInt();
			for(int index = 0; index < badgeCount; index++)
			{
				//DoS attack prevention
				if(!payload.isReadable()) break;
				
				//read badge
				var badgeId = payload.readIdentifier();
				var badge = PlayerBadges.get(badgeId);
				if(badge == null || badge instanceof ClientPlayerBadge)
					continue;
				
				//add badge
				pBadges.put(badgeId, badge);
			}
			//invoke event
			listener.onPlayerBadgesReady(pBadges);
		});
	}
	// ==================================================
}
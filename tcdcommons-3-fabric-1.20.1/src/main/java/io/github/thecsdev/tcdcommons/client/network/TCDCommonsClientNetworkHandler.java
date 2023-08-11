package io.github.thecsdev.tcdcommons.client.network;

import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;

import com.google.common.collect.HashBiMap;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.badge.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.network.PlayerBadgeNetworkListener;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public final class TCDCommonsClientNetworkHandler extends Object
{
	// ==================================================
	private TCDCommonsClientNetworkHandler() {}
	public static void init() {/*calls static*/}
	static
	{
		// ---------- register network handlers
		//receiving player badges
		CustomPayloadNetwork.registerReceiver(NetworkSide.CLIENTBOUND, TCDCommonsNetworkHandler.S2C_PLAYER_BADGES, context ->
		{
			final var payload = context.getPacketData();
			
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
				final Identifier badgeId = payload.readIdentifier();
				final PlayerBadge badge = PLAYER_BADGE.getValue(badgeId).orElse(null);
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
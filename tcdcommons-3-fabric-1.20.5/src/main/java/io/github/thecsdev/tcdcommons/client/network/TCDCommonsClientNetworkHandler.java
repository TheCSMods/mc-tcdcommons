package io.github.thecsdev.tcdcommons.client.network;

import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.badge.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.network.PlayerBadgeNetworkListener;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetworkHandler;
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
			final var payload = context.getPacketBuffer();
			
			// ========== KEEPING TRACK OF THE STATS CLIENT-SIDE
			//prepare
			final var statHandler = ClientPlayerBadge.getClientPlayerBadgeHandler(MC_CLIENT.player);
			
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
			
			// ========== PASSING THE EVENT TO ANY POTENTIAL LISTENER SCREENS
			//obtain the client instance, and check if a listener Screen is present
			final var currentScreen = MC_CLIENT.currentScreen;
			if(!(currentScreen instanceof PlayerBadgeNetworkListener))
				return;
			final var listener = (PlayerBadgeNetworkListener)currentScreen;

			//invoke the listener event (on the main thread, to prevent threading and concurrency issues)
			MC_CLIENT.executeSync(listener::onPlayerBadgesReady);
		});
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.client.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.Side;
import io.github.thecsdev.tcdcommons.api.client.network.PlayerBadgeNetworkListener;
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
			
			//read how many badges are in the payload
			var badgeCount = payload.readInt();
			//create an Identifier array for storing all the read badges
			var badges = new Identifier[badgeCount];
			//read and add the badges to the array
			for(int index = 0; index < badgeCount; index++)
			{
				if(payload.isReadable()) //vulnerability fix
					badges[index] = payload.readIdentifier();
				else break;
			}
			//invoke event
			listener.onPlayerBadgesReady(badges);
		});
	}
	// ==================================================
}
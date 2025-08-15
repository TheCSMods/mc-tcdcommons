package io.github.thecsdev.tcdcommons.network;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.client.network.TcdcClientPlayNetworkHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

@SuppressWarnings("removal")
public final @Internal class TCDCommonsNetwork
{
	// ==================================================
	private TCDCommonsNetwork() {}
	// --------------------------------------------------
	public static final ResourceLocation S2C_PLAYER_BADGES = ResourceLocation.fromNamespaceAndPath(getModID(), "player_badges");
	// ==================================================
	public static final void init() {}
	static
	{
		// ---------- PURE CLIENT-SIDE HANDLERS
		if(TCDCommons.isClient())
		{
			//client-sided player badge statistics packet handler
			CustomPayloadNetwork.registerReciever(PacketFlow.CLIENTBOUND, TCDCommonsNetwork.S2C_PLAYER_BADGES, context ->
				TcdcClientPlayNetworkHandler.of((LocalPlayer)context.getPlayer()).onPlayerBadges(context));
		}
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.network;

import static io.github.thecsdev.tcdcommons.network.TcdcServerPlayNetworkHandler.S2C_PLAYER_BADGES;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.api.network.TCustomPayload;
import io.github.thecsdev.tcdcommons.client.network.TcdcClientPlayNetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.NetworkSide;

public final @Internal class TCDCommonsNetwork
{
	// ==================================================
	private TCDCommonsNetwork() {}
	// ==================================================
	public static final void init() {}
	static
	{
		//register TCDCommons's Custom Payload
		PayloadTypeRegistry.configurationC2S().register(TCustomPayload.ID, TCustomPayload.CODEC);
		PayloadTypeRegistry.configurationS2C().register(TCustomPayload.ID, TCustomPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(TCustomPayload.ID, TCustomPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(TCustomPayload.ID, TCustomPayload.CODEC);
		
		// ---------- SINGLEPLAYER/DEDICATED SERVER HANDLERS
		ServerPlayNetworking.registerGlobalReceiver(TCustomPayload.ID, (payload, context) ->
			TcdcServerPlayNetworkHandler.of(context.player()).onCpn(payload));
		
		// ---------- PURE CLIENT-SIDE HANDLERS
		if(TCDCommons.isClient())
		{
			ClientPlayNetworking.registerGlobalReceiver(TCustomPayload.ID, (payload, context) ->
				TcdcClientPlayNetworkHandler.of(context.player()).onCpn(payload));
			
			CustomPayloadNetwork.registerPlayReceiver(NetworkSide.CLIENTBOUND, S2C_PLAYER_BADGES, context ->
				TcdcClientPlayNetworkHandler.of((ClientPlayerEntity)context.getPlayer()).onPlayerBadges(context));
		}
	}
	// ==================================================
}
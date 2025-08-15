package io.github.thecsdev.tcdcommons.client.network;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.badge.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.network.PlayerBadgeNetworkListener;
import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver.PacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

/**
 * TCDCommons server play network handler.<br/>
 * Keeps track of {@link LocalPlayer} data that is related to {@link TCDCommons}.
 */
public final @Internal class TcdcClientPlayNetworkHandler
{
	// ==================================================
	/**
	 * The unique {@link ResourceLocation} for obtaining an instance of this network
	 * handler for a given player entity, via {@link EntityHooks#getCustomData(Entity)}.
	 */
	private static final ResourceLocation CUSTOM_DATA_ID = ResourceLocation.fromNamespaceAndPath(getModID(), "client_play_network_handler");
	// ==================================================
	private final LocalPlayer player;
	// ==================================================
	private TcdcClientPlayNetworkHandler(LocalPlayer player) throws NullPointerException {
		this.player = Objects.requireNonNull(player);
	}
	// --------------------------------------------------
	public final LocalPlayer getPlayer() { return this.player; }
	// ==================================================
	/**
	 * Handles a server sending a custom payload containing
	 * player badge statistics of the {@link #player}.
	 */
	public final void onPlayerBadges(PacketContext context)
	{
		final var payload = context.getPacketBuffer();
		
		// ---------- KEEPING TRACK OF THE STATS CLIENT-SIDE
		//prepare
		final var statHandler = ClientPlayerBadge.getClientPlayerBadgeHandler(this.player);
		
		//read how many badges are in the payload,
		//and then read and add the badges to the array
		var badgeCount = payload.readInt();
		for(int index = 0; index < badgeCount; index++)
		{
			//DoS attack prevention
			if(payload.readableBytes() < 2) break;
			
			//read badge
			final ResourceLocation badgeId = payload.readResourceLocation();
			final int badgeValue = payload.readVarInt();
			
			final @Nullable PlayerBadge badge = PLAYER_BADGE.getValue(badgeId).orElse(null);
			if(badge == null || badge instanceof ClientPlayerBadge)
				continue;
			
			//add badge
			statHandler.setValue(badgeId, badgeValue);
		}
		
		// ---------- PASSING THE EVENT TO ANY POTENTIAL LISTENER SCREENS
		MC_CLIENT.executeIfPossible(() ->
		{
			//obtain the client instance, and check if a listener Screen is present
			final var currentScreen = MC_CLIENT.screen;
			if(currentScreen instanceof PlayerBadgeNetworkListener listener)
				//invoke the listener event (on the main thread, to prevent threading and concurrency issues)
				listener.onPlayerBadgesReady();
		});
	}
	// ==================================================
	/**
	 * Sends a custom payload packet to the other side.
	 */
	public final void sendCustomPayloadNetwork(ResourceLocation id, ByteBuf buffer)
			throws IllegalStateException, NullPointerException
	{
		//requirements
		Objects.requireNonNull(id);
		Objects.requireNonNull(buffer);
		if(buffer.refCnt() < 1) throw new IllegalStateException("REF_CNT");

		//send data in full, at once, if possible
		CustomPayloadNetwork.sendC2S(id, buffer);
	}
	// ==================================================
	/**
	 * Returns an instance of {@link TcdcClientPlayNetworkHandler} from a given
	 * {@link LocalPlayer}. Creates one if it doesn't exist.
	 * @param player The {@link LocalPlayer}.
	 */
	public static final TcdcClientPlayNetworkHandler of(LocalPlayer player) throws NullPointerException
	{
		final var cd = EntityHooks.getCustomData(Objects.requireNonNull(player));
		@Nullable TcdcClientPlayNetworkHandler cpnh = cd.getProperty(CUSTOM_DATA_ID);
		if(cpnh == null)
		{
			cpnh = new TcdcClientPlayNetworkHandler(player);
			cd.setProperty(CUSTOM_DATA_ID, cpnh);
		}
		return cpnh;
	}
	// ==================================================
}
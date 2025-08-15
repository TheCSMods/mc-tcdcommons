package io.github.thecsdev.tcdcommons.network;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

/**
 * TCDCommons server play network handler.<br/>
 * Keeps track of {@link ServerPlayer} data that is related to {@link TCDCommons}.
 */
public final @Internal class TcdcServerPlayNetworkHandler
{
	// ==================================================
	/**
	 * The unique {@link ResourceLocation} for obtaining an instance of this network
	 * handler for a given player entity, via {@link EntityHooks#getCustomData(Entity)}.
	 */
	private static final ResourceLocation CUSTOM_DATA_ID = ResourceLocation.fromNamespaceAndPath(getModID(), "server_play_network_handler");
	// ==================================================
	private final ServerPlayer player;
	// ==================================================
	private TcdcServerPlayNetworkHandler(ServerPlayer player) throws NullPointerException {
		this.player = Objects.requireNonNull(player);
	}
	// --------------------------------------------------
	public final ServerPlayer getPlayer() { return this.player; }
	// ==================================================
	/**
	 * Sends a custom payload packet to the other side.
	 * @see CustomPayloadNetwork#sendS2C(ServerPlayer, ResourceLocation, ByteBuf)
	 */
	public final void sendCustomPayloadNetwork(ResourceLocation id, ByteBuf buffer)
			throws IllegalStateException, NullPointerException
	{
		//requirements
		Objects.requireNonNull(id);
		Objects.requireNonNull(buffer);
		if(buffer.refCnt() < 1) throw new IllegalStateException("REF_CNT");

		//send data in full, at once, if possible
		CustomPayloadNetwork.sendS2C(this.player, id, buffer);
	}
	
	/**
	 * Sends the {@link #player} their player badge statistics.
	 * @return {@code true} if the packet was sent, and {@code false} if the packet was not
	 * sent because the player doesn't have any {@link PlayerBadge}s assigned to them.
	 */
	public final boolean sendPlayerBadges() {
		return ServerPlayerBadgeHandler.getServerBadgeHandler(this.player).sendStats(this.player);
	}
	// ==================================================
	/**
	 * Returns an instance of {@link TcdcServerPlayNetworkHandler} for a given
	 * {@link ServerPlayer}. Creates one if it doesn't exist yet.
	 * @param player The {@link ServerPlayer}.
	 */
	public static final TcdcServerPlayNetworkHandler of(ServerPlayer player) throws NullPointerException
	{
		final var cd = EntityHooks.getCustomData(Objects.requireNonNull(player));
		@Nullable TcdcServerPlayNetworkHandler spnh = cd.getProperty(CUSTOM_DATA_ID);
		if(spnh == null)
		{
			spnh = new TcdcServerPlayNetworkHandler(player);
			cd.setProperty(CUSTOM_DATA_ID, spnh);
		}
		return spnh;
	}
	// ==================================================
}
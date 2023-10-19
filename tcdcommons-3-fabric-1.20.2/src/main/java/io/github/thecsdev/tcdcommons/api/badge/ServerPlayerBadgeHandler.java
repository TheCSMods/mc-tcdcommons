package io.github.thecsdev.tcdcommons.api.badge;

import static io.github.thecsdev.tcdcommons.TCDCommons.LOGGER;
import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks.getCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks.setCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import static io.github.thecsdev.tcdcommons.network.TCDCommonsNetworkHandler.S2C_PLAYER_BADGES;

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.TCDCommonsConfig;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Same as a {@link PlayerBadgeHandler}, but it also keeps track
 * of which {@link ServerPlayerEntity} it is assigned to.
 */
public final class ServerPlayerBadgeHandler extends PlayerBadgeHandler
{
	// ==================================================
	private static final TCDCommonsConfig CONFIG = Objects.requireNonNull(TCDCommons.getInstance().getConfig());
	// ==================================================
	/**
	 * The {@link ServerPlayerEntity} this {@link ServerPlayerBadgeHandler} is associated with.
	 */
	protected final ServerPlayerEntity player;
	
	/**
	 * For network optimization purposes, this map will keep track
	 * of entries that are yet to be sent to the client in a stats packet.
	 */
	protected final Object2IntMap<Identifier> pendingStatMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());
	// ==================================================
	public ServerPlayerBadgeHandler(ServerPlayerEntity player)
	{
		this.player = Objects.requireNonNull(player);
	}
	// ==================================================
	/**
	 * Returns the {@link ServerPlayerEntity} associated with
	 * this {@link ServerPlayerBadgeHandler}.
	 */
	public ServerPlayerEntity getPlayer()  { return this.player; }
	// --------------------------------------------------
	public final @Override void setValue(Identifier badgeId, int value) throws NullPointerException
	{
		final var oldValue = this.statMap.getInt(badgeId);
		super.setValue(badgeId, value);
		this.pendingStatMap.put(badgeId, value);
		if(value > oldValue) announceEarnedBadge(this.player, badgeId);
	}
	// --------------------------------------------------
	/**
	 * Collects pending statistics entries and clears the {@link #pendingStatMap}.
	 * @return A copy of the pending statistics entries.
	 */
	protected final Object2IntMap<Identifier> takePendingStats()
	{
		final var copy = new Object2IntOpenHashMap<>(this.pendingStatMap);
		this.pendingStatMap.clear();
		return copy;
	}
	// ==================================================
	/**
	 * Used to load badge data for the current {@link ServerPlayerEntity}.
	 * @param nbt The {@link NbtCompound} that belongs to the {@link #player} that is about to be loaded.
	 * @throws NullPointerException when an argument is null.
	 * @see #player
	 */
	public final void loadFromPlayerNbt(NbtCompound nbt)
	{
		//check if there are any badges stored
		final var modId = getModID();
		if(!nbt.contains(modId, NbtElement.COMPOUND_TYPE)) return;
		final var nbt_modId = nbt.getCompound(modId);
		
		//read player badges
		// ========== LEGACY DATA LOADING - FOR BACKWARDS COMPATIBILITY
		if(nbt_modId.contains("player_badges", NbtElement.STRING_TYPE))
		{
			final var badgeListStr = nbt_modId.getString("player_badges").trim();
			if(StringUtils.isBlank(badgeListStr)) return; //do not read blank
			final var badgeList = badgeListStr.split("\\R");
			for(final String badgeItem : badgeList)
				if(!StringUtils.isBlank(badgeItem))
					increaseValue(new Identifier(badgeItem), 1);
		}
		// ========== NEW DATA LOADING METHOD
		else if(nbt_modId.contains("player_badges", NbtElement.COMPOUND_TYPE))
		{
			final var badgeList = nbt_modId.getCompound("player_badges");
			for(final String badgeIdStr : badgeList.getKeys())
			{
				//skip invalid data; value must be an integer
				if(!badgeList.contains(badgeIdStr, NbtElement.INT_TYPE)) continue;
				
				//obtain value, and put value
				setValue(new Identifier(badgeIdStr), badgeList.getInt(badgeIdStr));
			}
		}
	}
	
	/**
	 * Used to save badge data for the current {@link ServerPlayerEntity}.
	 * @param nbt The {@link NbtCompound} that belongs to the {@link #player} that is about to be saved.
	 * @return The {@link NbtCompound} that was passed as the argument.
	 * @throws NullPointerException when an argument is null.
	 * @see #player
	 */
	public final NbtCompound saveToPlayerNbt(NbtCompound nbt)
	{
		//check if there are any badges
		if(this.statMap.isEmpty()) return nbt;
		
		//get or create modId compound
		final String modId = getModID();
		if(!nbt.contains(modId, NbtElement.COMPOUND_TYPE))
			nbt.put(modId, new NbtCompound());
		final NbtCompound nbt_modId = nbt.getCompound(modId);
		
		//obtain mod list compound
		if(!nbt_modId.contains("player_badges", NbtElement.COMPOUND_TYPE))
			nbt_modId.put("player_badges", new NbtCompound());
		final var badgeList = nbt_modId.getCompound("player_badges");
		
		//iterate player badges, and put them
		for(final var badgeEntry : this)
			badgeList.putInt(Objects.toString(badgeEntry.getKey()), badgeEntry.getIntValue());
		
		//return the passed nbt
		return nbt;
	}
	// --------------------------------------------------
	/**
	 * Sends a given {@link ServerPlayerEntity} a list of
	 * their {@link PlayerBadge}s that have been assigned to them.
	 * @param player The target {@link ServerPlayerEntity}.
	 * @return True if the packet was sent, and false if the packet was not sent
	 * because the player doesn't have any {@link PlayerBadge}s assigned to them.
	 */
	public final boolean sendStats(ServerPlayerEntity player)
	{
		//check if badges are enabled - don't send packed if disabled
		if(!TCDCommons.getInstance().getConfig().enablePlayerBadges)
			return false;
		
		//obtain player badges (take pending stats for network optimization purposes)
		final var badges = Lists.newArrayList(takePendingStats().object2IntEntrySet().iterator());
		if(badges.size() == 0) return false; //network optimization - BEWARE
		
		// Split badges into chunks and send each chunk
		//this is done to avoid hitting packet length limits
		final int chunkSize = 15;
		final var badgePartitions = Lists.partition(badges, chunkSize);
		
		//send each chunk
		for(final var badgePartition : badgePartitions)
			sendStats(player, badgePartition);
		
		//return true once done
		return true;
	}
	
	/**
	 * Sends a smaller "chunk" of {@link PlayerBadge}s, rather than a whole
	 * {@link Collection}, so as to avoid hitting the maximum {@link Packet} length limit.
	 */
	private static void sendStats(ServerPlayerEntity player, Collection<Object2IntMap.Entry<Identifier>> badgeChunk)
	{
		//write player badges to a buffer
		final var data = new PacketByteBuf(Unpooled.buffer());
		data.writeInt(badgeChunk.size());
		for(final var badgeEntry : badgeChunk)
		{
			if(badgeEntry.getKey() == null) continue;
			data.writeIdentifier(badgeEntry.getKey());
			data.writeVarInt(badgeEntry.getIntValue());
		}
		
		//create and send packet
		try { new TCustomPayload(S2C_PLAYER_BADGES, data).sendS2C(player); }
		catch(Exception e)
		{
			LOGGER.debug("Failed to send " + S2C_PLAYER_BADGES + " packet; " + e.getMessage());
			throw e;
		}
	}
	// ==================================================
	/**
	 * Retrieves the {@link PlayerBadgeHandler} for a given {@link ServerPlayerEntity}.<br/>
	 * If one doesn't exist, a new one is created, assigned, and then returned.
	 * @param player The {@link ServerPlayerEntity} in question.
	 * @throws NullPointerException When an argument is null.
	 */
	@SuppressWarnings("deprecation")
	public static ServerPlayerBadgeHandler getServerBadgeHandler(ServerPlayerEntity player)
	{
		//obtain
		ServerPlayerBadgeHandler pbh = getCustomDataEntryG(player, PlayerBadgeHandler.PBH_CUSTOM_DATA_ID);
		//create if null
		if(pbh == null)
			pbh = setCustomDataEntryG(
					player,
					PlayerBadgeHandler.PBH_CUSTOM_DATA_ID,
					new ServerPlayerBadgeHandler(player));
		//return
		return pbh;
	}
	// --------------------------------------------------
	/**
	 * Broadcasts a server-wide message announcing a {@link ServerPlayerEntity} earning a {@link PlayerBadge}.
	 * @param player The player in question.
	 * @param badgeId The {@link Identifier} of the {@link PlayerBadge} they earned.
	 * @return {@code true} if the {@link #CONFIG} does not block the broadcast.
	 */
	public static boolean announceEarnedBadge(ServerPlayerEntity player, Identifier badgeId)
	{
		//check config
		if(!CONFIG.enablePlayerBadges || !CONFIG.broadcastEarningPlayerBadges)
			return false;
		
		//obtain and format badge name
		final var badge = PLAYER_BADGE.getValue(badgeId).orElse(null);
		var badgeName = (badge != null) ? literal("").append(badge.getName()) : literal(Objects.toString(badgeId));
		badgeName = __formatBadgeName(badgeName, badgeId, badge);
		
		//construct message, and broadcast it
		final var message = translatable("commands.badges.chat_grant", player.getName(), badgeName);
		player.getServer().getPlayerManager().broadcast(message, false);
		
		//return
		return true;
	}
	private static final MutableText __formatBadgeName(
			MutableText badgeName,
			@Nullable Identifier badgeId, @Nullable PlayerBadge badge)
	{
		//define the root message
		final var root = literal("");
		root.append(literal("[")).formatted(Formatting.YELLOW);
		root.append(badgeName).formatted(Formatting.YELLOW);
		root.append(literal("]")).formatted(Formatting.YELLOW);
		
		//define the hover text
		final var hover = literal("");
		hover.append(((badge != null) ? badge.getName() : literal(Objects.toString(badgeId)).formatted(Formatting.YELLOW)));
		hover.append("\n");
		hover.append(literal(Objects.toString(badgeId)).formatted(Formatting.GRAY));
		if(badge != null)
		{
			hover.append("\n\n");
			hover.append(badge.getDescription());
		}
		
		//assign the hover text
		final var hoverEvent = new HoverEvent(Action.SHOW_TEXT, hover);
		root.setStyle(root.getStyle().withHoverEvent(hoverEvent));
		
		//return the root message
		return root;
	}
	// ==================================================
}
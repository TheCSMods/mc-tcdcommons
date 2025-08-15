package io.github.thecsdev.tcdcommons.api.badge;

import com.google.common.collect.Lists;
import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.TCDCommonsConfig;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetwork;
import io.github.thecsdev.tcdcommons.util.TCDCT;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

import static io.github.thecsdev.tcdcommons.TCDCommons.LOGGER;
import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks.getCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks.setCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

/**
 * Same as a {@link PlayerBadgeHandler}, but it also keeps track
 * of which {@link ServerPlayer} it is assigned to.
 */
public final class ServerPlayerBadgeHandler extends PlayerBadgeHandler
{
	// ==================================================
	private static final TCDCommonsConfig CONFIG = Objects.requireNonNull(TCDCommons.getInstance().getConfig());
	// ==================================================
	/**
	 * The {@link ServerPlayer} this {@link ServerPlayerBadgeHandler} is associated with.
	 */
	protected final ServerPlayer player;
	
	/**
	 * For network optimization purposes, this map will keep track
	 * of entries that are yet to be sent to the client in a stats packet.
	 */
	protected final Object2IntMap<ResourceLocation> pendingStatMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());
	// ==================================================
	public ServerPlayerBadgeHandler(ServerPlayer player)
	{
		this.player = Objects.requireNonNull(player);
	}
	// ==================================================
	/**
	 * Returns the {@link ServerPlayer} associated with
	 * this {@link ServerPlayerBadgeHandler}.
	 */
	public ServerPlayer getPlayer()  { return this.player; }
	// --------------------------------------------------
	public final @Override void setValue(ResourceLocation badgeId, int value) throws NullPointerException
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
	protected final Object2IntMap<ResourceLocation> takePendingStats()
	{
		final var copy = new Object2IntOpenHashMap<>(this.pendingStatMap);
		this.pendingStatMap.clear();
		return copy;
	}
	// ==================================================
	/**
	 * Used to load badge data for the current {@link ServerPlayer}.
	 * @param nbt The {@link CompoundTag} that belongs to the {@link #player} that is about to be loaded.
	 * @throws NullPointerException when an argument is null.
	 * @see #player
	 */
	public final void loadFromPlayerNbt(CompoundTag nbt)
	{
		//check if there are any badges stored
		final var modId = getModID();
		if(!nbt.contains(modId)) return;
		final var nbt_modId = nbt.getCompound(modId).orElse(new CompoundTag());
		
		//read player badges
		// ========== LEGACY DATA LOADING - FOR BACKWARDS COMPATIBILITY
		if(nbt_modId.contains("player_badges"))
		{
			final var badgeListStr = nbt_modId.getString("player_badges").orElse("").trim();
			if(StringUtils.isBlank(badgeListStr)) return; //do not read blank
			final var badgeList = badgeListStr.split("\\R");
			for(final String badgeItem : badgeList)
				if(!StringUtils.isBlank(badgeItem))
					increaseValue(ResourceLocation.parse(badgeItem), 1);
		}
		// ========== NEW DATA LOADING METHOD
		else if(nbt_modId.contains("player_badges"))
		{
			final var badgeList = nbt_modId.getCompound("player_badges").orElse(new CompoundTag());
			for(final String badgeIdStr : badgeList.keySet())
			{
				//skip invalid data; value must be an integer
				if(!badgeList.contains(badgeIdStr)) continue;
				
				//obtain value, and put value
				setValue(ResourceLocation.parse(badgeIdStr), badgeList.getInt(badgeIdStr).orElse(0));
			}
		}
	}
	
	/**
	 * Used to save badge data for the current {@link ServerPlayer}.
	 * @param nbt The {@link CompoundTag} that belongs to the {@link #player} that is about to be saved.
	 * @return The {@link CompoundTag} that was passed as the argument.
	 * @throws NullPointerException when an argument is null.
	 * @see #player
	 */
	public final CompoundTag saveToPlayerNbt(CompoundTag nbt)
	{
		//check if there are any badges
		if(this.statMap.isEmpty()) return nbt;
		
		//get or create modId compound
		final String modId = getModID();
		if(!nbt.contains(modId))
			nbt.put(modId, new CompoundTag());
		final CompoundTag nbt_modId = nbt.getCompound(modId).orElse(new CompoundTag());
		
		//obtain mod list compound
		if(!nbt_modId.contains("player_badges"))
			nbt_modId.put("player_badges", new CompoundTag());
		final var badgeList = nbt_modId.getCompound("player_badges").orElse(new CompoundTag());
		
		//iterate player badges, and put them
		for(final var badgeEntry : this)
			badgeList.putInt(Objects.toString(badgeEntry.getKey()), badgeEntry.getIntValue());
		
		//return the passed nbt
		return nbt;
	}
	// --------------------------------------------------
	/**
	 * Sends a given {@link ServerPlayer} a list of
	 * their {@link PlayerBadge}s that have been assigned to them.
	 * @param player The target {@link ServerPlayer}.
	 * @return True if the packet was sent, and false if the packet was not sent
	 * because the player doesn't have any {@link PlayerBadge}s assigned to them.
	 */
	public final boolean sendStats(ServerPlayer player)
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
	private static void sendStats(ServerPlayer player, Collection<Object2IntMap.Entry<ResourceLocation>> badgeChunk)
	{
		//write player badges to a buffer
		final var data = new FriendlyByteBuf(Unpooled.buffer());
		data.writeInt(badgeChunk.size());
		for(final var badgeEntry : badgeChunk)
		{
			if(badgeEntry.getKey() == null) continue;
			data.writeResourceLocation(badgeEntry.getKey());
			data.writeVarInt(badgeEntry.getIntValue());
		}
		
		//create and send packet
		try { CustomPayloadNetwork.sendS2C(player, TCDCommonsNetwork.S2C_PLAYER_BADGES, data); }
		catch(Exception e)
		{
			LOGGER.debug("Failed to send " + TCDCommonsNetwork.S2C_PLAYER_BADGES + " packet; " + e.getMessage());
			throw e;
		}
	}
	// ==================================================
	/**
	 * Retrieves the {@link PlayerBadgeHandler} for a given {@link ServerPlayer}.<br/>
	 * If one doesn't exist, a new one is created, assigned, and then returned.
	 * @param player The {@link ServerPlayer} in question.
	 * @throws NullPointerException When an argument is null.
	 */
	public static ServerPlayerBadgeHandler getServerBadgeHandler(ServerPlayer player)
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
	 * Broadcasts a server-wide message announcing a {@link ServerPlayer} earning a {@link PlayerBadge}.
	 * @param player The player in question.
	 * @param badgeId The {@link ResourceLocation} of the {@link PlayerBadge} they earned.
	 * @return {@code true} if the {@link #CONFIG} does not block the broadcast.
	 */
	public static boolean announceEarnedBadge(ServerPlayer player, ResourceLocation badgeId)
	{
		//check config
		if(!CONFIG.enablePlayerBadges || !CONFIG.broadcastEarningPlayerBadges)
			return false;
		
		//obtain and format badge name
		final var badge = PLAYER_BADGE.getValue(badgeId).orElse(null);
		var badgeName = (badge != null) ? literal("").append(badge.getName()) : literal(Objects.toString(badgeId));
		badgeName = __formatBadgeName(badgeName, badgeId, badge);
		
		//construct message, and broadcast it
		final var message = TCDCT.cmd_pb_chatGrant(player.getName(), badgeName);
		player.getServer().getPlayerList().broadcastSystemMessage(message, false);
		
		//return
		return true;
	}
	private static final MutableComponent __formatBadgeName(
			MutableComponent badgeName,
			@Nullable ResourceLocation badgeId, @Nullable PlayerBadge badge)
	{
		//define the root message
		final var root = literal("");
		root.append(literal("[")).withStyle(ChatFormatting.YELLOW);
		root.append(badgeName).withStyle(ChatFormatting.YELLOW);
		root.append(literal("]")).withStyle(ChatFormatting.YELLOW);
		
		//define the hover text
		final var hover = literal("");
		hover.append(((badge != null) ? badge.getName() : literal(Objects.toString(badgeId)).withStyle(ChatFormatting.YELLOW)));
		hover.append("\n");
		hover.append(literal(Objects.toString(badgeId)).withStyle(ChatFormatting.GRAY));
		if(badge != null)
		{
			hover.append("\n\n");
			hover.append(badge.getDescription());
		}
		
		//assign the hover text
		final var hoverEvent = new HoverEvent.ShowText(hover);
		root.setStyle(root.getStyle().withHoverEvent(hoverEvent));
		
		//return the root message
		return root;
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.badge;

import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

/**
 * A handler for managing the {@link PlayerBadge}s assigned to a player.
 * <p>
 * This class serves as a container for keeping track of which badges a player has at any given time.
 */
public class PlayerBadgeHandler implements ObjectIterable<Entry<ResourceLocation>>
{
	// ==================================================
	/**
	 * The unique {@link ResourceLocation} used to associate
	 * {@link PlayerBadgeHandler}s with {@link ServerPlayer}s.
	 * @see EntityHooks#getCustomDataEntryG(net.minecraft.world.entity.Entity, ResourceLocation)
	 * @see EntityHooks#setCustomDataEntryG(net.minecraft.world.entity.Entity, ResourceLocation, Object)
	 */
	public static final ResourceLocation PBH_CUSTOM_DATA_ID = ResourceLocation.fromNamespaceAndPath(getModID(), "player_badges");
	// --------------------------------------------------
	/**
	 * A set that stores the {@link ResourceLocation}s of the {@link PlayerBadge}s assigned to this player.
	 */
	protected final Object2IntMap<ResourceLocation> statMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());
	// ==================================================
	public PlayerBadgeHandler() { this.statMap.defaultReturnValue(0); }
	// --------------------------------------------------
	public final @Override ObjectIterator<Entry<ResourceLocation>> iterator()
	{
		return this.statMap.object2IntEntrySet().iterator();
	}
	// ==================================================
	/**
	 * Obtains the {@link Integer} value associated with a {@link PlayerBadge}'s {@link ResourceLocation}.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link ResourceLocation}.
	 * @apiNote Default return value is {@code 0}.
	 */
	public final int getValue(ResourceLocation badgeId) { return this.statMap.getInt(badgeId); }
	
	/**
	 * Sets the {@link Integer} value associated with a {@link PlayerBadge}'s {@link ResourceLocation}.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link ResourceLocation}.
	 * @param value The new value.
	 */
	public void setValue(ResourceLocation badgeId, int value) throws NullPointerException
	{
		if(value < 1) this.statMap.removeInt(badgeId);
		else this.statMap.put(Objects.requireNonNull(badgeId), value);
	}
	
	/**
	 * Increases the {@link Integer} value associated with a {@link PlayerBadge}'s {@link ResourceLocation},
	 * by a given {@link Integer} amount.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link ResourceLocation}.
	 * @param by The amount to increase the value by.
	 */
	public final void increaseValue(ResourceLocation badgeId, int by)
	{
	    final int i = (int)Math.min((long)getValue(badgeId) + by, 2147483647L);
		setValue(badgeId, i);
	}
	// --------------------------------------------------
	/*
	 * Removes a {@link PlayerBadge} stat from the {@link #statMap}.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link Identifier}.
	 *
	@Deprecated(forRemoval = true) //use setValue and pass 0 instead
	public final void removeBadge(Identifier badgeId) { this.statMap.removeInt(badgeId); }*/
	
	/**
	 * Clears all {@link PlayerBadge} stats from the {@link #statMap}.
	 */
	public final void clearBadges()
	{
		//has to be done this way, so the server stat handler can track changes...
		final var keys = new HashSet<ResourceLocation>(this.statMap.keySet());
		for(final var key : keys) setValue(key, 0);
		
		//...and then after it tracks changes, we can safely clear
		this.statMap.clear();
	}
	// ==================================================
	/**
	 * Creates a new {@link Map}, maps the {@link PlayerBadge} {@link ResourceLocation}s
	 * by their corresponding "mod IDs", and returns the {@link Map}.
	 * @param badgeIDs An {@link Iterable} {@link Object} containing the set of {@link PlayerBadge} {@link ResourceLocation}s.
	 */
	public static final Map<String, List<ResourceLocation>> toMapByModId(Iterable<ResourceLocation> badgeIDs)
	{
		return StreamSupport.stream(badgeIDs.spliterator(), false)
	            .collect(Collectors.groupingBy(ResourceLocation::getNamespace, HashMap::new, Collectors.toList()));
	}
	// ==================================================
}

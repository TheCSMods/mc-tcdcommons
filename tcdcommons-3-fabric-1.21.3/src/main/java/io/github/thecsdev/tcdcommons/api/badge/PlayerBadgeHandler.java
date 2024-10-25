package io.github.thecsdev.tcdcommons.api.badge;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * A handler for managing the {@link PlayerBadge}s assigned to a player.
 * <p>
 * This class serves as a container for keeping track of which badges a player has at any given time.
 */
public class PlayerBadgeHandler implements ObjectIterable<Entry<Identifier>>
{
	// ==================================================
	/**
	 * The unique {@link Identifier} used to associate
	 * {@link PlayerBadgeHandler}s with {@link ServerPlayerEntity}s.
	 * @see EntityHooks#getCustomDataEntryG(net.minecraft.entity.Entity, Identifier)
	 * @see EntityHooks#setCustomDataEntryG(net.minecraft.entity.Entity, Identifier, Object)
	 */
	public static final Identifier PBH_CUSTOM_DATA_ID = Identifier.of(getModID(), "player_badges");
	// --------------------------------------------------
	/**
	 * A set that stores the {@link Identifier}s of the {@link PlayerBadge}s assigned to this player.
	 */
	protected final Object2IntMap<Identifier> statMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());
	// ==================================================
	public PlayerBadgeHandler() { this.statMap.defaultReturnValue(0); }
	// --------------------------------------------------
	public final @Override ObjectIterator<Entry<Identifier>> iterator()
	{
		return this.statMap.object2IntEntrySet().iterator();
	}
	// ==================================================
	/**
	 * Obtains the {@link Integer} value associated with a {@link PlayerBadge}'s {@link Identifier}.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link Identifier}.
	 * @apiNote Default return value is {@code 0}.
	 */
	public final int getValue(Identifier badgeId) { return this.statMap.getInt(badgeId); }
	
	/**
	 * Sets the {@link Integer} value associated with a {@link PlayerBadge}'s {@link Identifier}.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link Identifier}.
	 * @param value The new value.
	 */
	public void setValue(Identifier badgeId, int value) throws NullPointerException
	{
		if(value < 1) this.statMap.removeInt(badgeId);
		else this.statMap.put(Objects.requireNonNull(badgeId), value);
	}
	
	/**
	 * Increases the {@link Integer} value associated with a {@link PlayerBadge}'s {@link Identifier},
	 * by a given {@link Integer} amount.
	 * @param badgeId The {@link PlayerBadge}'s unique {@link Identifier}.
	 * @param by The amount to increase the value by.
	 */
	public final void increaseValue(Identifier badgeId, int by)
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
		final var keys = new HashSet<Identifier>(this.statMap.keySet());
		for(final var key : keys) setValue(key, 0);
		
		//...and then after it tracks changes, we can safely clear
		this.statMap.clear();
	}
	// ==================================================
	/**
	 * Creates a new {@link Map}, maps the {@link PlayerBadge} {@link Identifier}s
	 * by their corresponding "mod IDs", and returns the {@link Map}.
	 * @param badgeIDs An {@link Iterable} {@link Object} containing the set of {@link PlayerBadge} {@link Identifier}s.
	 */
	public static final Map<String, List<Identifier>> toMapByModId(Iterable<Identifier> badgeIDs)
	{
		return StreamSupport.stream(badgeIDs.spliterator(), false)
	            .collect(Collectors.groupingBy(Identifier::getNamespace, HashMap::new, Collectors.toList()));
	}
	// ==================================================
}

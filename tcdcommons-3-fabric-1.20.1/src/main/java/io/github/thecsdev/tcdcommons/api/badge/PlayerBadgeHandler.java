package io.github.thecsdev.tcdcommons.api.badge;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * A handler for managing the {@link PlayerBadge}s assigned to a player.
 * <p>
 * This class serves as a container for keeping track of which badges a player has at any given time.
 */
public class PlayerBadgeHandler implements Iterable<Identifier>
{
	// ==================================================
	/**
	 * The unique {@link Identifier} used to associate
	 * {@link PlayerBadgeHandler}s with {@link ServerPlayerEntity}s.
	 * @see EntityHooks#getCustomDataEntryG(net.minecraft.entity.Entity, Identifier)
	 * @see EntityHooks#setCustomDataEntryG(net.minecraft.entity.Entity, Identifier, Object)
	 */
	public static final Identifier PBH_CUSTOM_DATA_ID = new Identifier(getModID(), "player_badge_handler");
	// --------------------------------------------------
	/**
	 * A set that stores the {@link Identifier}s of the {@link PlayerBadge}s assigned to this player.
	 */
	protected final Set<Identifier> badges = Collections.synchronizedSet(new HashSet<>());
	// ==================================================
	public final @Override Iterator<Identifier> iterator() { return this.badges.iterator(); }
	// ==================================================	
	/*
	 * Returns the {@link #badges} {@link HashSet}.<br/>
	 * <b>Important:</b> Do not store null values in there. It could cause errors.
	 *
	@Deprecated //direct access to the protected Set may be a bad idea
	public final Set<Identifier> getBadges() { this.badges.remove(null); return this.badges; }*/
	// --------------------------------------------------
	/**
	 * Checks if the player has a specific badge.
	 *
	 * @param badgeId The unique {@link Identifier} of the badge to check for.
	 * @return True if the player has the badge, false otherwise.
	 */
	public final boolean containsBadge(Identifier badgeId)  { return this.badges.contains(badgeId); }
	
	/**
	 * Adds a specific badge to the player. If the badge is already present, it will not be added again.
	 *
	 * @param badgeId The unique {@link Identifier} of the badge to add.
	 * @return True if the badge was added, false if the badge was already
	 * present or if the provided identifier is null.
	 */
	public final boolean addBadge(Identifier badgeId)
	{
		if(badgeId == null) return false;
		else return this.badges.add(badgeId);
	}

	/**
	 * Removes a specific badge from the player.
	 *
	 * @param badgeId The unique {@link Identifier} of the badge to remove.
	 * @return True if the badge was successfully removed, false if the badge was not present.
	 */
	public final boolean removeBadge(Identifier badgeId)  {  return this.badges.remove(badgeId); }

	/**
	 * Clears all badges from the player.
	 */
	public final void clearBadges()  { this.badges.clear(); }
	
	/**
	 * Returns the number of badges assigned to this {@link PlayerBadgeHandler}.
	 */
	public final int size() { return this.badges.size(); }
	
	/**
	 * Returns the set of badges, in form of a new {@link Identifier} array.
	 */
	public final Identifier[] toArray() { return this.badges.toArray(new Identifier[0]); }
	// ==================================================
	/**
	 * Creates a new {@link Map}, maps the {@link PlayerBadge} {@link Identifier}s
	 * by their corresponding "mod IDs", and returns the {@link Map}.
	 * @param badgeIDs An {@link Iterable} {@link Object} containing the set of {@link PlayerBadge} {@link Identifier}s.
	 */
	public static final Map<String, Collection<Identifier>> toMapByModId(Iterable<Identifier> badgeIDs)
	{
		//create the map
		final HashMap<String, Collection<Identifier>> map = new HashMap<>();
		
		//add badges, one by one
		for(final var badgeId : badgeIDs)
		{
			final var modId = badgeId.getNamespace();
			if(!map.containsKey(modId)) map.put(modId, new ArrayList<>());
			map.get(modId).add(badgeId);
		}
		
		//return the map
		return map;
	}
	// ==================================================
}

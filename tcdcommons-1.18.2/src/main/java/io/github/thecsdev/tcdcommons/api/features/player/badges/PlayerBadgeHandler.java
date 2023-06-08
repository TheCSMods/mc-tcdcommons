package io.github.thecsdev.tcdcommons.api.features.player.badges;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.hooks.TEntityHooks.getCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.hooks.TEntityHooks.setCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry.PlayerSessionBadges;

import java.util.HashSet;

import com.google.common.collect.Sets;

import io.github.thecsdev.tcdcommons.api.hooks.TEntityHooks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * A handler for managing the {@link PlayerBadge}s assigned to a player.
 * <p>
 * This class serves as a container for keeping track of which badges a player has at any given time.
 */
public class PlayerBadgeHandler
{
	// ==================================================
	/**
	 * The unique {@link Identifier} used to associate
	 * {@link PlayerBadgeHandler}s with {@link ServerPlayerEntity}ies.
	 * @see TEntityHooks#getCustomDataEntryG(net.minecraft.entity.Entity, Identifier)
	 * @see TEntityHooks#setCustomDataEntryG(net.minecraft.entity.Entity, Identifier, Object)
	 */
	public static final Identifier PBH_CUSTOM_DATA_ID = new Identifier(getModID(), "player_badge_handler");
	// --------------------------------------------------
	/**
	 * A set that stores the {@link Identifier}s of the {@link PlayerBadge}s assigned to this player.
	 */
	protected final HashSet<Identifier> badges;
	// ==================================================
	/**
	 * Constructs a new PlayerBadgeHandler instance, initializing the badge set.
	 */
	public PlayerBadgeHandler() {  this.badges = Sets.newHashSet(); }
	
	/**
	 * Returns the {@link #badges} {@link HashSet}.<br/>
	 * <b>Important:</b> Do not store null values in there. It could cause errors.
	 */
	public final HashSet<Identifier> getBages() { this.badges.remove(null); return this.badges; }
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
	// ==================================================
	/**
	 * Retrieves the {@link PlayerBadgeHandler} for a given {@link ServerPlayerEntity}.<br/>
	 * If one doesn't exist, a new one is created, assigned, and then returned.
	 * @param player The {@link ServerPlayerEntity} in question.
	 * @throws NullPointerException When an argument is null.
	 */
	public static ServerPlayerBadgeHandler getSessionBadgeHandler(ServerPlayerEntity player)
	{
		//obtain
		ServerPlayerBadgeHandler pbh = getCustomDataEntryG(player, PBH_CUSTOM_DATA_ID);
		//create if null
		if(pbh == null)
			pbh = setCustomDataEntryG(
					player,
					PBH_CUSTOM_DATA_ID,
					new ServerPlayerBadgeHandler(PlayerSessionBadges, player));
		//return
		return pbh;
	}
	// ==================================================
}

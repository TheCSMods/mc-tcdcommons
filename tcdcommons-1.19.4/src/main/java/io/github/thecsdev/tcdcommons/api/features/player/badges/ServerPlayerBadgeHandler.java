package io.github.thecsdev.tcdcommons.api.features.player.badges;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry.PlayerSessionBadges;

import java.util.Objects;

import com.google.common.collect.BiMap;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Same as a {@link PlayerBadgeHandler}, but it also keeps track
 * of which {@link ServerPlayerEntity} it is assigned to.
 */
public final class ServerPlayerBadgeHandler extends PlayerBadgeHandler
{
	// ==================================================
	/**
	 * The badge registry associated with this badge handler.
	 */
	protected final BiMap<Identifier, PlayerBadge> badgeRegistry;
	
	/**
	 * The {@link ServerPlayerEntity} this {@link ServerPlayerBadgeHandler} is associated with.
	 */
	protected final ServerPlayerEntity player;
	// ==================================================
	/** @throws NullPointerException When an argument is null. */
	public ServerPlayerBadgeHandler(ServerPlayerEntity player) { this(PlayerSessionBadges, player); }
	
	/** @throws NullPointerException When an argument is null. */
	public ServerPlayerBadgeHandler(BiMap<Identifier, PlayerBadge> badgeRegistry, ServerPlayerEntity player)
	{
		this.badgeRegistry = Objects.requireNonNull(badgeRegistry);
		this.player = Objects.requireNonNull(player);
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link ServerPlayerEntity} associated with
	 * this {@link ServerPlayerBadgeHandler}.
	 */
	public ServerPlayerEntity getPlayer()  { return this.player; }
	// ==================================================
	/**
	 * Used to load badge data for the current {@link ServerPlayerEntity}.
	 * @param nbt The {@link NbtCompound} that belongs to the {@link #player} that is about to be loaded.
	 * @throws NullPointerException when an argument is null.
	 * @see #player
	 */
	public void readNbt(NbtCompound nbt)
	{
		//check if there are any badges
		final var modId = getModID();
		if(!nbt.contains(modId, NbtElement.COMPOUND_TYPE)) return;
		final var nbt_modId = nbt.getCompound(modId);
		if(!nbt_modId.contains("player_badges", NbtElement.STRING_TYPE)) return;
		
		//read player badges
		final var badges = getBages();
		final var badgeList = nbt_modId.getString("player_badges").trim().split("\\R");
		for(String badgeItem : badgeList)
		{
			try { badges.add(new Identifier(badgeItem)); }
			catch(Exception e) {}
		}
	}
	
	/**
	 * Used to save badge data for the current {@link ServerPlayerEntity}.
	 * @param nbt The {@link NbtCompound} that belongs to the {@link #player} that is about to be saved.
	 * @return The {@link NbtCompound} that was passed as the argument.
	 * @throws NullPointerException when an argument is null.
	 * @see #player
	 */
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		//check if there are any badges
		final var badges = getBages();
		if(badges.isEmpty()) return nbt;
		
		//get or create compound
		final var modId = getModID();
		if(!nbt.contains(modId, NbtElement.COMPOUND_TYPE))
			nbt.put(modId, new NbtCompound());
		NbtCompound nbt_modId = nbt.getCompound(modId);
		
		//put badge identifiers in a string, because NBTs don't support arrays
		var badgeList = "";
		for(Identifier badgeId : badges) //iterate all badge identifiers
		{
			//add badge if it isn't null
			if(badgeId == null)
				continue;
			//add badge if it "should save"
			//(must use && operator here, to keep non-existing badge progress)
			final var badgeById = this.badgeRegistry.get(badgeId);
			if(badgeById != null && !badgeById.shouldSave())
				continue;
			//finally, add badge
			badgeList += badgeId.toString() + "\n";
		}
		
		//put the string into the nbt_modId
		badgeList = badgeList.trim();
		nbt_modId.putString("player_badges", badgeList);
		
		//return the passed nbt
		return nbt;
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.badge;

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.registry.TRegistries;
import io.github.thecsdev.tcdcommons.api.registry.TSimpleRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a badge that can be assigned to a player.<br/>
 * A {@link PlayerBadge} is similar to an advancement in that it signifies
 * an achievement or status. However, unlike advancements, badges can be
 * temporary and are typically used to denote special or unique accomplishments.<br/>
 * <br/>
 * Each badge has a constant name and description, which can be retrieved using
 * the {@link #getName()} and {@link #getDescription()} methods. Whether or not
 * a badge should be saved with the player's data is determined by the {@link #shouldSave()} method.
 * 
 * @see TRegistries#PLAYER_BADGE
 */
public abstract class PlayerBadge
{
	// ==================================================
	static
	{
		//this shouldn't happen
		//it's just there to prevent me from making dumb changes that break other things
		if(!(TRegistries.PLAYER_BADGE instanceof TSimpleRegistry))
		{
			final var a = PlayerBadge.class.getSimpleName();
			final var b = TSimpleRegistry.class.getSimpleName();
			throw new ExceptionInInitializerError(a + " depends on the registry being a " + b);
		}
	}
	// ==================================================
	protected PlayerBadge() {}
	// --------------------------------------------------
	private @Internal Identifier __id = null; //caching for performance reasons
	
	/**
	 * Returns the {@link Identifier} of this {@link PlayerBadge} in
	 * accordance with the {@link TRegistries#PLAYER_BADGE} registry.
	 * @apiNote To self: Depends on {@link TRegistries#PLAYER_BADGE} being a {@link TSimpleRegistry}.
	 */
	public final Optional<Identifier> getId()
	{
		if(this.__id != null) return Optional.of(this.__id);
		else return Optional.ofNullable(this.__id = TRegistries.PLAYER_BADGE.getKey(this).orElse(null));
	}
	// ==================================================
	/** 
	 * Returns the display name of the badge.<br/>
	 * The returned name should be constant, meaning it should not
	 * change over the lifetime of a badge instance.<br/><br/>
	 * <b>Must be constant.</b>
	 */
	public abstract Text getName();

	/** 
	 * Returns the description of the badge.<br/>
	 * The returned description should be constant, meaning it should not
	 * change over the lifetime of a badge instance.<br/><br/>
	 * <b>Must be constant.</b>
	 */
	public abstract Text getDescription();
	// --------------------------------------------------
	/**
	 * Determines if the badge assigned to a player should persist in the player's save data.<br/>
	 * When this method returns true, the badge is stored with the player's save data, ensuring
	 * its persistence across sessions. If this method returns false, the badge is considered
	 * temporary and will not be saved.<br/>
	 * The result of this method should be constant for a given badge, as changing the result
	 * can lead to inconsistent saving behavior.<br/><br/>
	 * <b>Must be constant.</b>
	 */
	public abstract boolean shouldSave();
	// ==================================================
}
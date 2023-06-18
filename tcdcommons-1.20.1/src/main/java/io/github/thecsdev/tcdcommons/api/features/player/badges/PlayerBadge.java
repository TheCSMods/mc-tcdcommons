package io.github.thecsdev.tcdcommons.api.features.player.badges;

import static io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry.PlayerBadges;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TDrawContext;
import io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry;
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
 */
public abstract class PlayerBadge
{
	// ==================================================
	protected PlayerBadge() {}
	// --------------------------------------------------
	/**
	 * Returns the {@link Identifier} of this {@link PlayerBadge}
	 * using the {@link TCDCommonsRegistry#PlayerBadges} registry.
	 * @return Null if the registry does not have this {@link PlayerBadge}
	 * instance registered, and an {@link Identifier} otherwise.
	 */
	public final @Nullable Identifier getBadgeId() { return PlayerBadges.inverse().get(this); }
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
	/**
	 * <b>Warning:</b><br/>
	 * This method is only intended for client-side use. It must be invoked from the render-thread.
	 * Attempting to invoke this method on the server side <b>will</b> result in an {@link Error} being thrown,
	 * which can cause the game to crash.<br/>
	 * <br/>
	 * Renders this {@link PlayerBadge} on the client-side screen.
	 * @param pencil The rendering {@link TDrawContext}.
	 * @param x The starting on-screen X position where to start drawing the badge.
	 * @param y The starting on-screen Y position where to start drawing the badge.
	 * @param width The width of the {@link PlayerBadge} on the screen.
	 * @param height The height of the {@link PlayerBadge} on the screen.
	 * @param deltaTime The time elapsed since the last frame was rendered.
	 * @throws Error If this method is invoked anywhere but on the client-side or not from the render-thread.
	 */
	public abstract void renderOnClientScreen(TDrawContext pencil, int x, int y, int width, int height, float deltaTime);
	// ==================================================
}

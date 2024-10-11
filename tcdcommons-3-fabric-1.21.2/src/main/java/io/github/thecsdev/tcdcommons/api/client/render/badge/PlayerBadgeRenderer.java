package io.github.thecsdev.tcdcommons.api.client.render.badge;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;

/**
 * A {@link PlayerBadgeRenderer} is a unique component that tells
 * the game how a given {@link PlayerBadge} is supposed to be rendered.
 * <p>
 * Because {@link PlayerBadge}s are present on both sides (client and server),
 * and rendering is a client-side-only thing, the rendering has been
 * separated into a separate component called {@link PlayerBadgeRenderer}.
 * 
 * @see TClientRegistries#PLAYER_BADGE_RENDERER
 */
public abstract class PlayerBadgeRenderer<T extends PlayerBadge>
{
	// ==================================================
	protected final Class<T> badgeType;
	// ==================================================
	protected PlayerBadgeRenderer(Class<T> badgeType) { this.badgeType = Objects.requireNonNull(badgeType); }
	// --------------------------------------------------
	/**
	 * Returns the {@link Class} representing the type of
	 * {@link PlayerBadge} this {@link PlayerBadgeRenderer} renders.
	 */
	public final Class<T> getBadgeType() { return this.badgeType; }
	// ==================================================
	/**
	 * Renders this {@link PlayerBadge} on the client-side {@link Screen}.
	 * @param pencil The rendering {@link DrawContext}.
	 * @param x The starting on-screen X position where to start drawing the badge.
	 * @param y The starting on-screen Y position where to start drawing the badge.
	 * @param width The width of the {@link PlayerBadge} on the screen.
	 * @param height The height of the {@link PlayerBadge} on the screen.
	 * @param deltaTime The time elapsed since the last frame was rendered.
	 * @apiNote The {@link PlayerEntity} may not always be present.
	 */
	public abstract void render(
			DrawContext pencil,
			int x, int y, int width, int height,
			int mouseX, int mouseY,
			float deltaTime);
	// ==================================================
}
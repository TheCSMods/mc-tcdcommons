package io.github.thecsdev.tcdcommons.api.client.registry;

import io.github.thecsdev.tcdcommons.api.client.render.badge.PlayerBadgeRenderer;
import io.github.thecsdev.tcdcommons.api.registry.TSimpleRegistry;

public final class TClientRegistries
{
	private TClientRegistries() {}
	
	/**
	 * Contains {@link PlayerBadgeRenderer}s that were registered for this session.
	 */
	public static final TSimpleRegistry<PlayerBadgeRenderer<?>> PLAYER_BADGE_RENDERER = new TSimpleRegistry<>();
}
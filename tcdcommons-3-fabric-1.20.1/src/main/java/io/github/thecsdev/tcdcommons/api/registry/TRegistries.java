package io.github.thecsdev.tcdcommons.api.registry;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;

public final class TRegistries
{
	private TRegistries() {}
	
	/**
	 * Contains {@link PlayerBadge}s that were registered for this session.
	 * @apiNote Don't forget to register their corresponding renderers.
	 */
	public static final TSimpleRegistry<PlayerBadge> PLAYER_BADGE = new TSimpleRegistry<>();
}
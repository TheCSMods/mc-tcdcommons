package io.github.thecsdev.tcdcommons.api.registry;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;

public final class TRegistries
{
	private TRegistries() {}
	
	/**
	 * Contains {@link PlayerBadge}s that were registered for this session.
	 */
	public static final TRegistry<PlayerBadge> PLAYER_BADGE = new TSimpleRegistry<>();
}
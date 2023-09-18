package io.github.thecsdev.tcdcommons.api.registry;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;

public final class TRegistries
{
	private TRegistries() {}
	
	/**
	 * Contains {@link PlayerBadge}s that were registered for this session.
	 * @apiNote Don't forget to register their corresponding renderers.
	 * @apiNote To self: Has to be a {@link TSimpleRegistry}, otherwise {@link PlayerBadge#getId()} would break.
	 */
	public static final TSimpleRegistry<PlayerBadge> PLAYER_BADGE = new TSimpleRegistry<>();
}
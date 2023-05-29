package io.github.thecsdev.tcdcommons.api.util;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

public final class TUtils
{
	// ==================================================
	protected TUtils() {}
	// ==================================================
	/**
	 * Returns the <b>offline</b> {@link UUID} for a given player nickname.<br/>
	 * This {@link UUID} will not work for referencing "online" players.
	 * @param nickname The player nickname.
	 */
	public static UUID getOfflinePlayerUuid(String nickname)
	{
		return PlayerEntity.getOfflinePlayerUuid(nickname);
	}
	// ==================================================
}
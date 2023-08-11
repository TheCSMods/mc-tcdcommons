package io.github.thecsdev.tcdcommons.api.util;

import java.util.UUID;

import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public final class TUtils
{
	// ==================================================
	private TUtils() {}
	// ==================================================
	/**
	 * Crashes the game.
	 * @param message The message to include alongside the crash, in the crash report.
	 * @param cause The {@link Throwable} that caused whatever issue took place.
	 * @throws CrashException Always.
	 */
	public static void crashGame(String message, Throwable cause) throws CrashException
	{
		throw new CrashException(new CrashReport(message, cause));
	}
	
	/**
	 * Returns the <b>offline</b> {@link UUID} for a given player nickname.<br/>
	 * This {@link UUID} will not work for referencing "online" players.
	 * @param nickname The player nickname.
	 */
	public static UUID getOfflinePlayerUuid(String nickname)
	{
		return Uuids.getOfflinePlayerUuid(nickname);
	}
	// ==================================================
}
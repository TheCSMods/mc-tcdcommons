package io.github.thecsdev.tcdcommons.api.util;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfoProvider;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public final class TUtils
{
	// ==================================================
	private TUtils() {}
	// ==================================================
	/**
	 * Throws a {@link CrashException}.
	 * @param message The message to include alongside the crash, in the crash report.
	 * @param cause The {@link Throwable} that caused whatever issue took place.
	 * @throws CrashException Always.
	 */
	public static void throwCrash(String message, Throwable cause) throws CrashException
	{
		final var exc = new CrashException(new CrashReport(message, cause));
		throw exc;
	}
	// --------------------------------------------------
	/**
	 * Returns the <b>offline</b> {@link UUID} for a given player nickname.<br/>
	 * This {@link UUID} will not work for referencing "online" players.
	 * @param nickname The player nickname.
	 */
	public static UUID getOfflinePlayerUuid(String nickname)
	{
		return Uuids.getOfflinePlayerUuid(nickname);
	}
	// --------------------------------------------------
	/**
	 * Returns the name of a given mod by it's mod id.
	 * @param modId The unique ID of the mod.
	 * @return The name of the mod, or "*" if the argument is {@code null}.
	 */
	public static String getModName(String modId)
	{
		if(StringUtils.isAllBlank(modId)) return "*";
		return ModInfoProvider.getInstance().getModInfo(modId).getName().getString();
	}
	// ==================================================
}
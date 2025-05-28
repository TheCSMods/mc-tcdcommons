package io.github.thecsdev.tcdcommons.api.util;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfoProvider;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

/**
 * Contains miscellaneous functions that provide extra utility.
 */
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
		final @Nullable var mi = ModInfoProvider.getInstance().getModInfo(modId);
		if(mi != null) return mi.getName().getString();
		else return modId;
	}
	// --------------------------------------------------
	/**
	 * A safer implementation of {@link List#subList(int, int)}.<br/>
	 * Credit: https://stackoverflow.com/a/31003453
	 * @param list The {@link List} to sub-list from.
	 * @param fromIndex Low endpoint (inclusive) of the subList.
	 * @param toIndex High endpoint (exclusive) of the subList.
	 */
	public static <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex)
	{
		final int size = list.size();
		if (fromIndex >= size || toIndex <= 0 || fromIndex >= toIndex)
			return Collections.emptyList();
		
		fromIndex = Math.max(0, fromIndex);
		toIndex = Math.min(size, toIndex);
		
		try { return list.subList(fromIndex, toIndex); }
		catch(Exception e) { return Collections.emptyList(); }
	}
	// ==================================================
}
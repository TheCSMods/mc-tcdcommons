package io.github.thecsdev.tcdcommons.api.util.io.mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * A "wrapper" utility used by {@link TCDCommons} when obtaining
 * information about currently installed mods.
 */
public abstract class ModInfoProvider
{
	// ==================================================
	private static ModInfoProvider INSTANCE = new FabricModInfoProvider();
	// ==================================================
	/**
	 * Returns the current {@link ModInfoProvider} {@link #INSTANCE}.
	 */
	public static final ModInfoProvider getInstance() { return INSTANCE; }
	
	/**
	 * Sets the current {@link ModInfoProvider} {@link #INSTANCE}.
	 * @param instance The new {@link ModInfoProvider} {@link #INSTANCE}.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @apiNote Not recommended to call unless there's a good reason for it.
	 */
	public static final void setInstance(ModInfoProvider instance) throws NullPointerException
	{
		INSTANCE = Objects.requireNonNull(instance);
	}
	// --------------------------------------------------
	/**
	 * A "static" method for {@link ModInfoProvider#getModInfo(String)}, so
	 * {@link #getInstance()} doesn't have to be called every time.
	 * @param modId The unique "id" of the mod.
	 * @throws NullPointerException If {@link ModInfoProvider#getModInfo(String)} throws it.
	 * @throws IllegalStateException If {@link #getInstance()} returns {@code null}.
	 */
	@Deprecated(forRemoval = true)
	public static final @Nullable ModInfo getModInfoS(String modId) throws NullPointerException, IllegalStateException
	{
		if(INSTANCE == null)
			throw new IllegalStateException(ModInfoProvider.class.getSimpleName() + " instance is missing.");
		return INSTANCE.getModInfo(modId);
	}
	// ==================================================
	/**
	 * Returns the {@link ModInfo} object for a given currently
	 * installed mod, or {@code null} if no such mod is installed.
	 * @param modId The unique "id" of the mod.
	 * @throws NullPointerException When the argument is {@code null}.
	 */
	public abstract @Nullable ModInfo getModInfo(String modId) throws NullPointerException;
	
	/**
	 * Returns an array of mod "IDs" representing all currently installed mods.
	 */
	public abstract String[] getLoadedModIDs();
	
	/**
	 * Returns a {@link Boolean} indicating whether or not a
	 * specific mod is currently loaded.
	 */
	public @Virtual boolean isModLoaded(String modId) throws NullPointerException
	{
		return Arrays.asList(getLoadedModIDs()).contains(Objects.requireNonNull(modId));
	}
	
	/**
	 * Similar to {@link #getLoadedModIDs()}, but the mod IDs
	 * are grouped in a {@link Map} where the keys are alphabetically ordered characters.
	 */
	public final Map<Character, Collection<String>> getLoadedModIDsGrouped() { return getLoadedModIDsGrouped(null); }
	
	/**
	 * Same as {@link #getLoadedModIDsGrouped()}, but with a {@link Predicate} allowing
	 * you to filter out any unwanted mods from the returned {@link Map}.
	 */
	public final Map<Character, Collection<String>> getLoadedModIDsGrouped(@Nullable Predicate<String> predicate)
	{
		return Arrays.stream(getLoadedModIDs())
			.filter(mod -> !StringUtils.isBlank(mod) && (predicate == null || predicate.test(mod)))
			.sorted()
			.collect(Collectors.toMap(
				mod -> mod.substring(0, 1).toUpperCase().charAt(0),
				mod -> new ArrayList<>(Collections.singletonList(mod)), // Create an ArrayList to enable combining
				(list1, list2) -> {
					List<String> combinedList = new ArrayList<>(list1);
					combinedList.addAll(list2);
					return combinedList;
				},
				TreeMap::new // this will create a TreeMap which is sorted by key in natural order
			));
	}
	// ==================================================
}
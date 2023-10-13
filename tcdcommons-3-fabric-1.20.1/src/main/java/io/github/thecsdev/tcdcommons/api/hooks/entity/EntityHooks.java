package io.github.thecsdev.tcdcommons.api.hooks.entity;

import java.util.HashMap;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.util.collections.GenericProperties;
import io.github.thecsdev.tcdcommons.mixin.addons.MixinEntityAddon;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public final class EntityHooks
{
	// ==================================================
	private EntityHooks() {}
	// ==================================================
	/**
	 * Returns a {@link HashMap} containing the custom data for a given {@link Entity}.
	 * This feature is introduced through the tcdcommons mod, as Vanilla Minecraft does not 
	 * support a "custom data" feature as of 1.19.x.
	 *
	 * @param entity The target {@link Entity}.
	 * @return The {@link HashMap} containing the custom data for the given {@link Entity}.
	 * @throws NullPointerException When the argument is null.
	 */
	public static GenericProperties<Identifier> getCustomData(Entity entity)
	{
		return ((MixinEntityAddon)entity).tcdcommons_getCustomData();
	}
	// --------------------------------------------------
	/**
	 * Retrieves a custom data entry for a given {@link Entity} and key.<br/>
	 * The method attempts to cast the value to the provided type, returning null if the cast fails.<br/>
	 * The "G" in the method name stands for "Generic".
	 *
	 * @param <T> The generic type of custom data entry to retrieve.
	 * @param entity The target {@link Entity}.
	 * @param entryId The unique identifier for the custom data entry.
	 * @return The custom data entry associated with the given key, cast to the provided type, or null if the cast fails.
	 * @throws NullPointerException When the {@link Identifier} is {@code null}.
	 */
	@Deprecated //for inconsistency with GenericProperties
	public static <T> T getCustomDataEntryG(Entity entity, Identifier entryId)
	{
		Objects.requireNonNull(entryId);
		return getCustomData(entity).getPropertyOrDefault(entryId, null);
	}
	
	/**
	 * Sets a custom data entry for a given {@link Entity} and key, then returns the value that was set.<br/>
	 * This method allows for method chaining during assignment due to its return value.<br/>
	 * The "G" in the method name stands for "Generic".
	 *
	 * @param <T> The type of the value to be stored in the custom data entry.
	 * @param entity The target {@link Entity}.
	 * @param entryId The unique identifier for the custom data entry.
	 * @param entryValue The value to be stored in the custom data entry.
	 * @return The value that was set in the custom data entry.
	 * @throws NullPointerException When the {@link Identifier} is null.
	 */
	@Deprecated //for inconsistency with GenericProperties
	public static <T> T setCustomDataEntryG(Entity entity, Identifier entryId, T entryValue)
	{
		Objects.requireNonNull(entryId);
		getCustomData(entity).setProperty(entryId, entryValue);
		return entryValue;
	}
	// ==================================================
}
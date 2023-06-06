package io.github.thecsdev.tcdcommons.mixin.addons;

import java.util.HashMap;
import io.github.thecsdev.tcdcommons.mixin.events.MixinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * The {@link MixinEntity} mixin injects a custom field into the {@link Entity} class 
 * to hold custom data. This interface provides a method to access that custom field.
 */
public interface MixinEntity_AddOn
{
	/**
	 * Gets the {@link HashMap} that stores custom data for a given {@link Entity} instance.
	 * The {@link Identifier}s serve as unique keys that can be associated with different mods, 
	 * and the {@link Object} values represent the custom data stored by those mods.
	 */
	public HashMap<Identifier, Object> tcdcommons_getCustomData();
}

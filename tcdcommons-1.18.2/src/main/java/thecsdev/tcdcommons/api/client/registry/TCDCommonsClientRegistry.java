package thecsdev.tcdcommons.api.client.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import thecsdev.tcdcommons.api.registry.TCDCommonsRegistry;

public final class TCDCommonsClientRegistry extends TCDCommonsRegistry
{
	// ==================================================
	protected TCDCommonsClientRegistry() {}
	// ==================================================
	public static final HashMap<Class<? extends Entity>, Supplier<Double>> TEntityRenderer_SizeOffsets;
	// --------------------------------------------------
	/**
	 * Calls the static constructor for this class
	 * if it hasn't been called yet.
	 */
	public static void init() {}
	static
	{
		//define the registries
		TEntityRenderer_SizeOffsets = Maps.newHashMap();
		
		//the default settings
		TEntityRenderer_SizeOffsets.put(EnderDragonEntity.class, () -> 4d);
	}
	// ==================================================
	public static <T extends Entity> double getEntityRendererSizeOffset(Class<T> entityClass)
	{
		//default outcome is 1d (100%), aka no offset
		var supplier = TEntityRenderer_SizeOffsets.getOrDefault(entityClass, () -> 1d);
		return supplier.get();
	}
	// ==================================================
}

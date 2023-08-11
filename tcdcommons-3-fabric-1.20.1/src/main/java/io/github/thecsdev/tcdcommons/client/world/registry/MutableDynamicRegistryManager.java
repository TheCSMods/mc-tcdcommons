package io.github.thecsdev.tcdcommons.client.world.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.Beta;
import com.mojang.serialization.Lifecycle;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

/**
 * A {@link DynamicRegistryManager} that creates {@link Registry}s
 * on demand when calling {@link #getOptional(RegistryKey)} instead
 * of returning {@link Optional#empty()}.
 */
@Beta
public @Virtual class MutableDynamicRegistryManager implements DynamicRegistryManager
{
	// ==================================================
	protected final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries;
	// ==================================================
	public MutableDynamicRegistryManager() { this(new ArrayList<>()); }
	public MutableDynamicRegistryManager(List<? extends Registry<?>> registries)
	{
		this.registries = registries
				.stream()
				.collect(Collectors.toMap(Registry::getKey, registry -> registry));
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link Registry} {@link Map} associated
	 * with this {@link MutableDynamicRegistryManager}.
	 */
	public final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> getRegistries()
	{
		return this.registries;
	}
	// ==================================================
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final @Override <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key)
	{
		//obtain registry
		var reg = this.registries.get(key);
		
		//create registry if it doesn't exist
		if(reg == null)
		{
			reg = new SimpleRegistry<E>((RegistryKey<? extends Registry<E>>) key, Lifecycle.stable(), false);
			try
			{
				//due to Map#put not allowing putting the key due to type mismatch,
				//and casting not working, there's no other choice but to resort to reflection
				Method putMethod = this.registries.getClass().getMethod("put", Object.class, Object.class);
				putMethod.invoke(this.registries, key, reg);
			}
			catch (Exception exc) { throw new RuntimeException("Failed to put create and store Registry instance", exc); }
		}
		
		//return the registry as optional
		return Optional.<Registry>ofNullable(reg).map(registry -> registry);
	}
	// --------------------------------------------------
	public final @Override Stream<Entry<?>> streamAllRegistries()
	{
		return this.registries.entrySet().stream().map(MutableDynamicRegistryManager::createEntry);
	}
	// ==================================================
	/**
	 * Creates a {@link DynamicRegistryManager.Entry} based on the given parameters.<br/>
	 * Primarily used in {@link #streamAllRegistries()}.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T, R extends Registry<? extends T>> Entry<T> createEntry
	(Map.Entry<? extends RegistryKey<? extends Registry<?>>, R> entry)
	{
		return createEntry(entry.getKey(), (Registry)entry.getValue());
	}
	// --------------------------------------------------
	/**
	 * Creates a {@link DynamicRegistryManager.Entry} based on the given parameters.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Entry<T> createEntry(RegistryKey<? extends Registry<?>> key, Registry<?> value)
	{
		return new Entry<>((RegistryKey)key, (Registry)value);
	}
	// ==================================================
}
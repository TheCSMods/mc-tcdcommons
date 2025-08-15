package io.github.thecsdev.tcdcommons.api.registry;

import com.google.common.collect.Iterators;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

/**
 * A custom implementation of Minecraft's {@link Registry} system
 * that aims to be simpler and more convenient to use.
 */
public interface TRegistry<T> extends Iterable<Map.Entry<ResourceLocation, T>>
{
	/**
	 * Returns the number of registered entries in this {@link TRegistry}.
	 */
	default int size() { return Iterators.size(iterator()); }
	
	/**
	 * Registers an entry to this {@link TRegistry}.
	 * @param id The unique {@link ResourceLocation} of the entry being registered.
	 * @param entry The entry being registered.
	 * @return The registered entry.
	 * @throws UnsupportedOperationException If this {@link TRegistry} does not support registering new entries.
	 * @throws NullPointerException If any given argument is null.
	 * @throws IllegalStateException If the entry is already registered,
	 * and this {@link TRegistry} doesn't support overriding existing entries.
	 */
	T register(ResourceLocation id, T entry) throws UnsupportedOperationException, NullPointerException, IllegalStateException;
	
	/**
	 * UnRegisters an existing entry that is part of this {@link TRegistry}.
	 * @param id The unique {@link ResourceLocation} of the entry being unregistered. 
	 * @return The entry value that was previously associated with the given unique
	 * {@link ResourceLocation}, or null if there was no such entry.
	 * @throws UnsupportedOperationException If this {@link TRegistry} does not support
	 * unregistering existing entries.
	 * @throws NullPointerException If any given argument is null.
	 */
	T unregister(ResourceLocation id) throws UnsupportedOperationException, NullPointerException;
	
	/**
	 * Returns the unique {@link ResourceLocation} that corresponds to
	 * a registered entry, wrapped in an {@link Optional}.
	 * @param entry The registered entry.
	 * @apiNote The return value must never be null. If the entry key does not
	 * exist, return {@link Optional#ofNullable(Object)} and pass 'null' into it.
	 */
	Optional<ResourceLocation> getKey(T entry);
	
	/**
	 * Returns a registered entry, wrapped in an {@link Optional}.
	 * @param id The unique {@link ResourceLocation} of the entry that was registered.
	 * @apiNote The return value must never be null. If the entry does not
	 * exist, return {@link Optional#ofNullable(Object)} and pass 'null' into it.
	 */
	Optional<T> getValue(ResourceLocation id);
	
	boolean containsKey(ResourceLocation id);
	boolean containsValue(T entry);
}
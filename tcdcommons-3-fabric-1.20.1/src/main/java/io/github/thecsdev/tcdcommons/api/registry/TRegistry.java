package io.github.thecsdev.tcdcommons.api.registry;

import java.util.Map;
import java.util.Optional;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * A custom implementation of Minecraft's {@link Registry} system
 * that aims to be simpler and more convenient to use.
 */
public interface TRegistry<T> extends Iterable<Map.Entry<Identifier, T>>
{
	/**
	 * Registers an entry to this {@link TRegistry}.
	 * @param id The unique {@link Identifier} of the entry being registered.
	 * @param entry The entry being registered.
	 * @return The registered entry.
	 * @throws UnsupportedOperationException If this {@link TRegistry} does not support registering new entries.
	 * @throws NullPointerException If any given argument is null.
	 * @throws IllegalStateException If the entry is already registered,
	 * and this {@link TRegistry} doesn't support overriding existing entries.
	 */
	T register(Identifier id, T entry) throws UnsupportedOperationException, NullPointerException, IllegalStateException;
	
	/**
	 * UnRegisters an existing entry that is part of this {@link TRegistry}.
	 * @param id The unique {@link Identifier} of the entry being unregistered. 
	 * @return The entry value that was previously associated with the given unique
	 * {@link Identifier}, or null if there was no such entry.
	 * @throws UnsupportedOperationException If this {@link TRegistry} does not support
	 * unregistering existing entries.
	 * @throws NullPointerException If any given argument is null.
	 */
	T unregister(Identifier id) throws UnsupportedOperationException, NullPointerException;
	
	/**
	 * Returns the unique {@link Identifier} that corresponds to
	 * a registered entry, wrapped in an {@link Optional}.
	 * @param entry The registered entry.
	 * @apiNote The return value must never be null. If the entry key does not
	 * exist, return {@link Optional#ofNullable(Object)} and pass 'null' into it.
	 */
	Optional<Identifier> getKey(T entry);
	
	/**
	 * Returns a registered entry, wrapped in an {@link Optional}.
	 * @param id The unique {@link Identifier} of the entry that was registered.
	 * @apiNote The return value must never be null. If the entry does not
	 * exist, return {@link Optional#ofNullable(Object)} and pass 'null' into it.
	 */
	Optional<T> getValue(Identifier id);
	
	boolean containsKey(Identifier id);
	boolean containsValue(T entry);
}
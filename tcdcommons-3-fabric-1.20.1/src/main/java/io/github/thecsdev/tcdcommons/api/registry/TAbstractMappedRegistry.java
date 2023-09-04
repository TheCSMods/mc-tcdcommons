package io.github.thecsdev.tcdcommons.api.registry;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.google.common.collect.HashBiMap;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.util.Identifier;

/**
 * A {@link TRegistry} that uses {@link HashBiMap}s to store data about registered entries.
 * @see TSimpleRegistry
 * @see TMutableRegistry
 * @see TImmutableRegistry
 */
public abstract class TAbstractMappedRegistry<T> implements TRegistry<T>
{
	// ==================================================
	static final String ERR_CANNOT_ADD = "This registry does not support registering new entries.";
	static final String ERR_CANNOT_REMOVE = "This registry does not support unregistering existing entries.";
	// --------------------------------------------------
	protected final Map<Identifier, T> map = new LinkedHashMap<>();
	// ==================================================
	public final @Override Iterator<Entry<Identifier, T>> iterator() { return this.map.entrySet().iterator(); }
	// ==================================================
	public @Virtual @Override T register(Identifier id, T entry)
	throws UnsupportedOperationException, NullPointerException, IllegalStateException
	{
		if(this.map.containsKey(Objects.requireNonNull(id)))
			throw new IllegalStateException("An entry with the ID '" + id + "' is already registered.");
		this.map.put(id, Objects.requireNonNull(entry));
		return entry;
	}
	// --------------------------------------------------
	public @Virtual @Override T unregister(Identifier id) throws UnsupportedOperationException, NullPointerException
	{
		final var entry = this.map.getOrDefault(Objects.requireNonNull(id), null);
		this.map.remove(id);
		return entry;
	}
	// ==================================================
	public final @Override Optional<Identifier> getKey(T entry)
	{
		for(final var mapEntry : this)
			if(Objects.equals(entry, mapEntry.getValue()))
				return Optional.of(mapEntry.getKey());
		return Optional.empty();
	}
	// --------------------------------------------------
	public final @Override Optional<T> getValue(Identifier id)
	{
		return Optional.ofNullable(this.map.getOrDefault(id, null));
	}
	// ==================================================
	public final @Override boolean containsKey(Identifier id) { return this.map.containsKey(id); }
	public final @Override boolean containsValue(T entry) { return this.map.containsValue(entry); }
	// ==================================================
	public final @Override void forEach(Consumer<? super Entry<Identifier, T>> action) { TRegistry.super.forEach(action); }
	public final @Override Spliterator<Entry<Identifier, T>> spliterator() { return TRegistry.super.spliterator(); }
	// ==================================================
}
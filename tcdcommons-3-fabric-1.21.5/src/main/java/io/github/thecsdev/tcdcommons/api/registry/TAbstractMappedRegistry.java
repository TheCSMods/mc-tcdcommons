package io.github.thecsdev.tcdcommons.api.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.google.common.collect.HashBiMap;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
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
	protected final Map<Identifier, T> map = Collections.synchronizedMap(new LinkedHashMap<>());
	// --------------------------------------------------
	public final TEvent<MappedRegistryEvent<T>> eRegistered = TEventFactory.createLoop();
	public final TEvent<MappedRegistryEvent<T>> eUnRegistered = TEventFactory.createLoop();
	// ==================================================
	public final @Override Iterator<Entry<Identifier, T>> iterator() { return this.map.entrySet().iterator(); }
	// --------------------------------------------------
	public final int size() { return this.map.size(); } //override for better performance
	// ==================================================
	public @Virtual @Override T register(Identifier id, T entry)
	throws UnsupportedOperationException, NullPointerException, IllegalStateException
	{
		//check if already registered
		if(this.map.containsKey(Objects.requireNonNull(id)))
			throw new IllegalStateException("An entry with the ID '" + id + "' is already registered.");
		
		//register and invoke event
		this.map.put(id, Objects.requireNonNull(entry));
		this.eRegistered.invoker().invoke(id, entry);
		
		//return
		return entry;
	}
	// --------------------------------------------------
	public @Virtual @Override T unregister(Identifier id) throws UnsupportedOperationException, NullPointerException
	{
		//obtain previous value
		final var entry = this.map.getOrDefault(Objects.requireNonNull(id), null);
		
		//unregister and invoke event
		this.map.remove(id); //allow attempts to clear null keys
		if(id != null) //only invoke event for non-null keys
			this.eUnRegistered.invoker().invoke(id, entry);
		
		//return previously registered entry
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
	/**
	 * @see MappedRegistryEvent#invoke(Identifier, Object)
	 */
	public static interface MappedRegistryEvent<T>
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link TAbstractMappedRegistry}
		 * entry is either registered or un-registered.
		 * 
		 * @param entryId The {@link Identifier} of the entry being (un)registered.
		 * @param entry The entry being (un)registered.
		 * 
		 * @see TRegistry#register(Identifier, Object)
		 * @see TRegistry#unregister(Identifier)
		 * 
		 * @apiNote For {@link TAbstractMappedRegistry#eUnRegistered}, the entry
		 * {@link Identifier} and/or the entry itself may be {@code null}, beware.
		 */
		public void invoke(Identifier entryId, T entry);
	}
	// ==================================================
}
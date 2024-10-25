package io.github.thecsdev.tcdcommons.api.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import net.minecraft.util.Identifier;

/**
 * A {@link TRegistry} that does not allow for registering new entries
 * or unregistering existing entries.
 * @see TSimpleRegistry
 * @see TMutableRegistry
 */
public final class TImmutableRegistry<T> extends TAbstractMappedRegistry<T>
{
	// ==================================================
	/**
	 * Creates an instance of {@link TImmutableRegistry}.
	 * @param entries The {@link Collection} of entries this {@link TRegistry} will contain.
	 * @throws NullPointerException If a key or value in the entry {@link Collection} is null.
	 */
	public TImmutableRegistry(Collection<Map.Entry<Identifier, T>> entries)
	{
		for(var entry : entries)
			this.map.put(Objects.requireNonNull(entry.getKey()), Objects.requireNonNull(entry.getValue()));
	}
	// ==================================================
	public final @Override T register(Identifier id, T entry) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(ERR_CANNOT_ADD);
	}
	// --------------------------------------------------
	public final @Override T unregister(Identifier id) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(ERR_CANNOT_REMOVE);
	}
	// ==================================================
}
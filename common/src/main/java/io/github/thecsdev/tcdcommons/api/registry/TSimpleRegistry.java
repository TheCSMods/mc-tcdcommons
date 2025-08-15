package io.github.thecsdev.tcdcommons.api.registry;

import net.minecraft.resources.ResourceLocation;

/**
 * A simple {@link TRegistry} that allows registering new entries, but
 * prohibits unregistering existing entries.
 * @see TMutableRegistry
 * @see TImmutableRegistry
 */
public final class TSimpleRegistry<T> extends TAbstractMappedRegistry<T>
{
	public final @Override T unregister(ResourceLocation id) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(ERR_CANNOT_REMOVE);
	}
}
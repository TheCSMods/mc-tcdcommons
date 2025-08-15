package io.github.thecsdev.tcdcommons.api.event;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;

/**
 * Similar to {@link TEvent}, except this one stores its listeners
 * in a {@link Map} in key-value pairs. This allows listeners to
 * be "identifiable", so they can easily be added, accessed, and removed.<br/>
 * <br/>
 * The event handler insertion order is preserved.
 * @see io.github.thecsdev.tcdcommons.api.event
 */
public interface TKeyedEvent<T>
{
	T invoker();
	
	default boolean register(ResourceLocation key, T listener) { return register(Objects.toString(key), listener); }
	boolean register(String key, T listener);
	
	default boolean unregister(ResourceLocation key) { return unregister(Objects.toString(key)); }
	boolean unregister(String key);
	
	default boolean isRegistered(ResourceLocation key) { return isRegistered(Objects.toString(key)); }
	boolean isRegistered(String key);
	
	void clearListeners();
}
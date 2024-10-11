package io.github.thecsdev.tcdcommons.api.event;

import java.util.Map;
import java.util.Objects;

import net.minecraft.util.Identifier;

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
	
	default boolean register(Identifier key, T listener) { return register(Objects.toString(key), listener); }
	boolean register(String key, T listener);
	
	default boolean unregister(Identifier key) { return unregister(Objects.toString(key)); }
	boolean unregister(String key);
	
	default boolean isRegistered(Identifier key) { return isRegistered(Objects.toString(key)); }
	boolean isRegistered(String key);
	
	void clearListeners();
}
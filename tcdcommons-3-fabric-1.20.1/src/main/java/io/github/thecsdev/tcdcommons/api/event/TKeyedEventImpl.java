package io.github.thecsdev.tcdcommons.api.event;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * An implementation of {@link TKeyedEvent}.
 * @see TKeyedEvent
 */
final class TKeyedEventImpl<T> implements TKeyedEvent<T>
{
	// ==================================================
	private final LinkedHashMap<String, T> listeners;
	private final T invoker;
	// ==================================================
	public TKeyedEventImpl(LinkedHashMap<String, T> listeners, T invoker)
	{
		this.listeners = Objects.requireNonNull(listeners);
		this.invoker = Objects.requireNonNull(invoker);
	}
	// ==================================================
	public @Override boolean register(String key, T listener)
	{
		if(key == null || listener == null || isRegistered(key))
			return false;
		return this.listeners.put(key, listener) != null || true;
	}
	public @Override boolean unregister(String key) { return this.listeners.remove(key) != null; }
	public @Override boolean isRegistered(String key) { return this.listeners.containsKey(key); }
	// --------------------------------------------------
	public @Override void clearListeners() { this.listeners.clear(); }
	// --------------------------------------------------
	public @Override T invoker() { return this.invoker; }
	// ==================================================
}
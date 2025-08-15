package io.github.thecsdev.tcdcommons.api.event;

import java.util.List;
import java.util.Objects;

/**
 * An implementation of {@link TEvent}.
 * @see TEvent
 */
final class TEventImpl<T> implements TEvent<T>
{
	// ==================================================
	private final List<T> listeners;
	private final T invoker;
	// ==================================================
	/**
	 * @apiNote Make sure the {@link List} is {@code synchronized}.
	 */
	public TEventImpl(List<T> listeners, T invoker)
	{
		this.listeners = Objects.requireNonNull(listeners);
		this.invoker = Objects.requireNonNull(invoker);
	}
	// ==================================================
	public @Override boolean register(T listener)
	{
		if(listener == null || isRegistered(listener))
			return false;
		return this.listeners.add(listener);
	}
	public @Override boolean unregister(T listener) { return this.listeners.remove(listener); }
	public @Override boolean isRegistered(T listener) { return this.listeners.contains(listener); }
	// --------------------------------------------------
	public @Override void clearListeners() { this.listeners.clear(); }
	// --------------------------------------------------
	public @Override T invoker() { return this.invoker; }
	// ==================================================
}
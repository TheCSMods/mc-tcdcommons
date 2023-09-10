package io.github.thecsdev.tcdcommons.api.event;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.util.collections.IdealList;

final class TWeakEventImpl<T> implements TEvent<T>
{
	// ==================================================
	private final List<WeakReference<T>> listeners;
	private final T invoker;
	// ==================================================
	/**
	 * @apiNote {@link List} must be a {@code synchronized} {@link IdealList}.
	 */
	public TWeakEventImpl(List<WeakReference<T>> listeners, T invoker)
	{
		this.listeners = Objects.requireNonNull(listeners);
		this.invoker = Objects.requireNonNull(invoker);
	}
	// ==================================================
	public @Override boolean register(T listener)
	{
		if(listener == null || isRegistered(listener))
			return false;
		return this.listeners.add(new WeakReference<>(listener));
	}
	public @Override boolean unregister(T listener) { return this.listeners.removeIf(wr -> wr.refersTo(listener)); }
	public @Override boolean isRegistered(T listener)
	{
		for(final var wr : this.listeners)
			if(wr.refersTo(listener)) return true;
		return false;
	}
	// --------------------------------------------------
	public @Override void clearListeners() { this.listeners.clear(); }
	// --------------------------------------------------
	public @Override T invoker() { return this.invoker; }
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class HookedMap<K, V> implements Map<K, V>
{
	// ==================================================
	private final Map<K, V> delegate;
	private final Consumer<Map<K, V>> accessHook;
	// ==================================================
	private HookedMap(Map<K, V> delegate, Consumer<Map<K, V>> accessHook)
	{
		this.delegate = delegate;
		this.accessHook = accessHook;
	}
	// --------------------------------------------------
	private void invokeHook() { if (this.accessHook != null) this.accessHook.accept(this.delegate); }
	// ==================================================
	/**
	 * Creates and returns a new {@link HookedMap} instance that is based on another {@link Map}.
	 * @param <K> The {@link Map} key type.
	 * @param <V> The {@link Map} value type.
	 * @param map The {@link Map} to "hook".
	 * @param accessHook The "hook" that will be invoked upon accessing the {@link Map}.
	 * @apiNote All methods part of the {@link Map} {@code interface} will invoke the "hook".
	 * @apiNote For conventional reasons, it is not recommended to cast the return value to {@link HookedMap}.
	 */
	public static <K, V> Map<K, V> of(Map<K, V> map, Consumer<Map<K, V>> accessHook)
	{
		return new HookedMap<>(map, accessHook);
	}
	// ==================================================
	public final @Override V get(Object key) { invokeHook(); return this.delegate.get(key); }
	public final @Override V put(K key, V value) { invokeHook(); return this.delegate.put(key, value); }
	public final @Override int size() { invokeHook(); return this.delegate.size(); }
	public final @Override boolean isEmpty() { invokeHook(); return this.delegate.isEmpty(); }
	public final @Override boolean containsKey(Object key) { invokeHook(); return this.delegate.containsKey(key); }
	public final @Override boolean containsValue(Object value) { invokeHook(); return this.delegate.containsValue(value); }
	public final @Override void putAll(Map<? extends K, ? extends V> m) { invokeHook(); this.delegate.putAll(m); }
	public final @Override V remove(Object key) { invokeHook(); return this.delegate.remove(key); }
	public final @Override void clear() { invokeHook(); this.delegate.clear(); }
	public final @Override Set<K> keySet() { invokeHook(); return this.delegate.keySet(); }
	public final @Override Collection<V> values() { invokeHook(); return this.delegate.values(); }
	public final @Override Set<Entry<K, V>> entrySet() { invokeHook(); return this.delegate.entrySet(); }
	// ==================================================
}
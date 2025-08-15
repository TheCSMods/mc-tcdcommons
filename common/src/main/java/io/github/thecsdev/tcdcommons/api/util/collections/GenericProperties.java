package io.github.thecsdev.tcdcommons.api.util.collections;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Properties;

/**
 * A {@link HashMap} that stores {@link Object}s that represent "property" values,
 * and the keys represent "property" names.<br/>
 * This implementation allows for storing and retrieving properties and their values using generics.
 * @see Properties
 */
public @Virtual class GenericProperties<K> extends HashMap<K, Object>
{
	private static final long serialVersionUID = 7086758792901773483L;
	
	/**
	 * Uses {@link #get(Object)} to obtain the value of a given property,
	 * after which it is casted to {@code <V>} and returned.
	 * @param key The property key.
	 * @throws ClassCastException If a value is present, but it cannot be cast to type {@code <V>}.
	 * @see #get(Object)
	 */
	@SuppressWarnings("unchecked")
	public final @Nullable <V> V getProperty(K key) throws ClassCastException { return (V) this.get(key); }
	
	
	/**
	 * Uses {@link #getProperty(Object)} to obtain the value of a given property.
	 * If {@link #getProperty(Object)} returns {@code null}, the "default value" will be returned instead.
	 * @param key The property key.
	 * @param defaultValue The value that will be returned if {@link #getProperty(Object)} returns {@code null}.
	 * @throws ClassCastException If {@link #getProperty(Object)} throws it.
	 */
	public final @Nullable <V> V getProperty(K key, V defaultValue) throws ClassCastException
	{
		final V val = getProperty(key);
		return (val != null) ? val : defaultValue;
	}
	
	/**
	 * Uses {@link #getProperty(Object)} to obtain the value of a given property.
	 * If {@link #getProperty(Object)} returns {@code null} or throws a {@link ClassCastException},
	 * {@link #setProperty(Object, Object...)} will be used to set the property to the "default value",
	 * after which the "default value" will be returned.
	 * @param key The property key.
	 * @param defaultValue The value that will be returned if {@link #getProperty(Object)} returns {@code null}.
	 */
	public final @Nullable <V> V getPropertyOrDefault(K key, V defaultValue)
	{
		V val = null;
		try { val = getProperty(key); } catch(ClassCastException e) {}
		if(val == null) setProperty(key, val = defaultValue);
		return val;
	}
	
	/**
	 * Assigns a new value {@code <V>} for a given property.
	 * @param key The property key.
	 * @param value The new value for the given property. <b>Must pass a single {@link Object} here.</b>
	 * Do not pass any arrays, null arrays, or arrays whose length is not 1.
	 * @return The previous value of the given property, or {@code null} if no value of the given type was assigned.
	 * @throws IllegalArgumentException If the "value" array is invalid, null, or its length is not 1.
	 */
	@SuppressWarnings("unchecked")
	public final @SafeVarargs @Nullable <V> V setProperty(K key, V... value) throws IllegalArgumentException
	{
		//checks
		if(value == null || value.length != 1)
			throw new IllegalArgumentException("value");
		//put and return previous
		final Object prev = this.put(key, value[0]);
		return (value.getClass().getComponentType().isInstance(prev)) ? (V) prev : null;
	}
}
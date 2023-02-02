package io.github.thecsdev.tcdcommons.api.util;

import java.util.HashMap;
import java.util.Properties;

/**
 * It's like {@link Properties}, but you can retrieve
 * data from it using generics.
 */
public class GenericProperties
{
	// ==================================================
	protected final HashMap<String, Object> properties;
	// ==================================================
	public GenericProperties() { this.properties = new HashMap<>(); }
	// --------------------------------------------------
	/**
	 * Returns the {@link HashMap} where all the
	 * keys and values are stored.
	 */
	public final HashMap<String, Object> getProperties() { return this.properties; }
	// ==================================================
	/**
	 * Associates a specific value with a specific key.
	 * @param key The key used to store and retrieve the value.
	 * @param value The value to store.
	 */
	public void set(String key, Object value) { this.properties.put(key, value); }
	// --------------------------------------------------
	/**
	 * Retrieves the value associated with the given key.
	 * @param key The key used to retrieve the value.
	 */
	public Object get(String key) { return this.properties.get(key); }
	
	/**
	 * Same as {@link #get(String)}, but you can specify the
	 * default value that will be returned if the {@link #get(String)} returns null.
	 * @param key The key used to retrieve the value.
	 * @param defaultValue The value that will be returned if the {@link #get(String)} returns null.
	 */
	public Object get(String key, Object defaultValue)
	{
		var val = this.properties.get(key);
		if(val == null) return defaultValue;
		else return val;
	}
	// --------------------------------------------------
	/**
	 * Similar to {@link #get(String)}, but it also lets you
	 * cast the value to a specific type before it gets returned.
	 * @param key The key used to retrieve the value.
	 * @param asWhat What the value will be casted to before being returned.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAs(String key, Class<T> asWhat)
	{
		var val = this.properties.get(key);
		try { return (val != null) ? (T)val : null; }
		catch(Exception e) { return null; }
	}
	
	/**
	 * Similar to {@link #getAs(String, Class)}, but it also lets you
	 * specify the default value that will be returned if the {@link #get(String)} returns null.
	 * @param key The key used to retrieve the value.
	 * @param asWhat What the value will be casted to before being returned.
	 * @param defaultValue The value that will be returned if the {@link #get(String)} returns null.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAs(String key, Class<T> asWhat, T defaultValue)
	{
		var val = this.properties.get(key);
		try { return (val != null) ? (T)val : defaultValue; }
		catch(Exception e) { return defaultValue; }
	}
	// ==================================================
	public String getAsString(String key) { return getAs(key, String.class, null); }
	public int getAsInt(String key) { return getAs(key, Integer.class, 0); }
	// ==================================================
}

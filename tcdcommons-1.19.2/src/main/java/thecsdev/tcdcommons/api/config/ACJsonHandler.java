package thecsdev.tcdcommons.api.config;

import com.google.gson.JsonElement;

/**
 * Handles saving and loading {@link Object}
 * data using {@link JsonElement}s.
 */
public interface ACJsonHandler<T extends JsonElement>
{
	/**
	 * Saves the data from this object into a new {@link JsonElement},
	 * and then returns the {@link JsonElement}.
	 */
	public T saveToJson();
	
	/**
	 * Loads the data from a given {@link JsonElement} to this
	 * object, and then returns true if loading was successful.
	 * @param jsonElement The {@link JsonElement} to load the data from.
	 */
	public boolean loadFromJson(T jsonElement);
	
	/**
	 * Same as {@link #loadFromJson(JsonElement)}, but it is
	 * a workaround for handling generic types... This method will
	 * cast the given {@link JsonElement} to generic type (T), and then
	 * call {@link #loadFromJson(JsonElement)}.
	 * @param jsonElement The {@link JsonElement} to load the data from.
	 * @return Same as {@link #loadFromJson(JsonElement)}. Will return false
	 * if casting the input {@link JsonElement} to the generic type fails.
	 */
	@SuppressWarnings("unchecked")
	default boolean loadFromJsonElement(JsonElement jsonElement)
	{
		try { return loadFromJson((T) jsonElement); }
		catch(Exception exc) { return false; }
	}
}
package io.github.thecsdev.tcdcommons.api.config;

import com.google.gson.*;
import io.github.thecsdev.tcdcommons.api.config.annotation.NonSerialized;
import io.github.thecsdev.tcdcommons.api.config.annotation.SerializedAs;
import io.github.thecsdev.tcdcommons.api.util.TUtils;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static io.github.thecsdev.tcdcommons.TCDCommons.LOGGER;

/**
 * A config utility that represents a config JSON file.
 * @see SerializedAs
 * @see NonSerialized
 */
public @Virtual class AutoConfig implements ACJsonHandler<JsonObject>
{
	// ==================================================
	@NonSerialized
	protected static final transient String FILE_EXTENSION = "json";
	
	/**
	 * The {@link Gson} used by {@link AutoConfig} to
	 * serialize and deserialize properties.
	 */
	@NonSerialized
	protected static final transient Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	// --------------------------------------------------
	/**
	 * The name of the {@link AutoConfig} file.
	 */
	@NonSerialized
	public final transient String fileName;
	
	/**
	 * The final path of the {@link AutoConfig} file.
	 * This is where {@link #saveToFile(boolean)} will save to,
	 * and where {@link #loadFromFile(boolean)} will load from.
	 */
	@NonSerialized
	public final transient Path filePath;
	// ==================================================
	/**
	 * Constructs an {@link AutoConfig} instance.
	 * @param name The name of the config file.
	 * @exception InvalidPathException If the fileName is invalid.
	 */
	public AutoConfig(String name) throws InvalidPathException
	{
		this.fileName = name.strip().replaceAll("[\\\\/:*?\"<>|]", "") + "." + FILE_EXTENSION;
		this.filePath = Path.of(System.getProperty("user.dir"), "config", fileName);
	}
	// ==================================================
	/**
	 * Returns all valid property fields for this object.
	 * Both declared fields as well as inherited fields are included.
	 * Property fields must be public non-static primitive types or
	 * {@link ACJsonHandler}s.
	 */
	public final List<Field> getPropertyFields()
	{
		return Arrays.asList(getClass().getFields()).stream()
			.filter(field ->
			{
				final var fieldMods = field.getModifiers();
				return  //perform checks
						// - modifiers go first, to avoid errors
						!Modifier.isStatic(fieldMods) &&
						!Modifier.isFinal(fieldMods) &&
						!Modifier.isTransient(fieldMods) &&
						// - after modifiers, check everything else
						field.canAccess(this) &&
						(field.getType().isPrimitive() || ACJsonHandler.class.isAssignableFrom(field.getType())) &&
						field.getAnnotation(NonSerialized.class) == null;
			})
			.toList();
	}
	// --------------------------------------------------
	/**
	 * Serializes a {@link Field} that belongs to this
	 * {@link AutoConfig} object to a {@link JsonElement}.
	 * @param property The property {@link Field} from this {@link AutoConfig}.
	 * @return null if serialization fails or if the given
	 * {@link Field} type is unsupported.
	 */
	protected final @Nullable JsonElement serializeProperty(Field property)
	{
		try
		{
			Object val = property.get(this);
			if(ACJsonHandler.class.isAssignableFrom(property.getType()))
				return ((ACJsonHandler<?>)val).saveToJson();
			else return GSON.toJsonTree(val);
		}
		catch (Exception e) {}
		return null;
	}
	
	/**
	 * Deserializes a {@link JsonElement} to a given property
	 * (field) that belongs to this {@link AutoConfig}.
	 * @param property The property {@link Field} from this {@link AutoConfig}.
	 * @param jEl The {@link JsonElement} to deserialize.
	 * @return true if nothing went wrong while deserializing
	 * the given property {@link Field}.
	 */
	protected final boolean deserializeProperty(Field property, JsonElement jEl)
	{
		try
		{
			if(ACJsonHandler.class.isAssignableFrom(property.getType()))
			{
				Object objVal = property.get(jEl);
				if(objVal == null) throw new NullPointerException();
				ACJsonHandler<?> val = (ACJsonHandler<?>) objVal;
				val.loadFromJsonElement(jEl);
			}
			else property.set(this, GSON.fromJson(jEl, property.getType()));
			return true;
		}
		catch(Exception e) {}
		return false;
	}
	
	/**
	 * Checks if a given property {@link Field} has the
	 * {@link SerializedAs} annotation, and returns the
	 * name of the {@link Field} based on that.
	 * @param property The property {@link Field} from this {@link AutoConfig}.
	 * @return {@link Field#getName()} if the field doesn't
	 * have the {@link SerializedAs} annotation.
	 */
	public final String getPropertyName(Field property)
	{
		SerializedAs sa = property.getAnnotation(SerializedAs.class);
		if(sa != null) return sa.value();
		else return property.getName();
	}
	// ==================================================
	public @Virtual @Override JsonObject saveToJson()
	{
		JsonObject result = new JsonObject();
		for(Field property : getPropertyFields())
		{
			JsonElement serializedElement = serializeProperty(property);
			if(serializedElement != null)
				result.add(getPropertyName(property), serializedElement);
		}
		return result;
	}
	// --------------------------------------------------
	public @Virtual @Override boolean loadFromJson(JsonObject json)
	{
		for(Field property : getPropertyFields())
		{
			final var propertyName = getPropertyName(property);
			if(json.has(propertyName))
				deserializeProperty(property, json.get(propertyName));
		}
		return true;
	}
	// ==================================================
	/**
	 * Saves this {@link AutoConfig} to it's file.
	 * @param log Would you like to log this operation to the console?
	 * @throws IOException If an I/O error occurs writing to or creating the file.
	 * @throws SecurityException See {@link SecurityManager#checkWrite(String)}.
	 */
	public final void saveToFile(boolean log) throws IOException, SecurityException
	{
		JsonObject json = saveToJson();
		String jsonStr = GSON.toJson(json);
		this.filePath.toFile().delete();
		Files.writeString(filePath, jsonStr, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		if(log) LOGGER.info("Saved '" + getClass().getSimpleName() + "' config to '" + fileName + "'.");
	}
	
	/**
	 * Same as {@link #saveToFile(boolean)}, but any raised {@link IOException}s will not be handled.
	 * @param log Would you like to log this operation to the console?
	 * @see #saveToFile(boolean)
	 */
	public final void saveToFileOrCrash(boolean log)
	{
		try { saveToFile(log); }
		catch(Exception exc) { TUtils.throwCrash("Failed to save config file \"" + this.fileName + "\"", exc); }
	}
	
	/**
	 * Same as {@link #saveToFile(boolean)}, but returns false if an {@link IOException} is raised.
	 * @param log Would you like to log this operation to the console?
	 * @see #saveToFile(boolean)
	 */
	public final boolean trySaveToFile(boolean log)
	{
		try { saveToFile(log); } catch(IOException exc) { return false; }
		return true;
	}
	// --------------------------------------------------
	/**
	 * Loads this {@link AutoConfig} from it's file.
	 * @param log Would you like to log this operation to the console?
	 * @throws IOException If an I/O error occurs reading from the file.
	 */
	public final void loadFromFile(boolean log) throws IOException
	{
		if(!filePath.toFile().exists()) return;
		String jsonStr = Files.readString(filePath);
		
		JsonObject json = null;
		try { json = JsonParser.parseString(jsonStr).getAsJsonObject(); }
		catch(Exception exc)
		{
			if(exc instanceof JsonParseException)
				{ if(log) LOGGER.error("Unable to load '" + fileName + "' config. Could not parse the JSON."); }
			else if(exc instanceof JsonSyntaxException)
				{ if(log) LOGGER.error("Unable to load '" + fileName + "' config. Invalid JSON syntax."); }
			else if(exc instanceof IllegalStateException)
				{ if(log) LOGGER.error("Unable to load '" + fileName + "' config. Invalid JSON element type."); }
			else throw exc;
		}
		
		if(json != null)
		{
			loadFromJson(json);
			if(log) LOGGER.info("Loaded '" + getClass().getSimpleName() + "' config from '" + fileName + "'.");
		}
	}
	
	/**
	 * Same as {@link #loadFromFile(boolean)}, but any raised {@link IOException}s will not be handled.
	 * @param log Would you like to log this operation to the console?
	 * @see #loadFromFile(boolean)
	 */
	public final void loadFromFileOrCrash(boolean log)
	{
	    try { loadFromFile(log); } catch (IOException exc)
	    { TUtils.throwCrash("Failed to load config file \"" + this.fileName + "\".", exc); }
	}
	
	/**
	 * Same as {@link #loadFromFile(boolean)}, but returns false if an {@link IOException} is raised.
	 * @param log Would you like to log this operation to the console?
	 * @see #loadFromFile(boolean)
	 */
	public final boolean tryLoadFromFile(boolean log)
	{
	    try { loadFromFile(log); } catch (IOException exc) { return false; }
	    return true;
	}
	// ==================================================
}
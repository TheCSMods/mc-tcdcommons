package thecsdev.tcdcommons.api.config;

import static thecsdev.tcdcommons.TCDCommons.LOGGER;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;
import thecsdev.tcdcommons.api.config.annotation.NonSerialized;
import thecsdev.tcdcommons.api.config.annotation.SerializedAs;

public class AutoConfig implements ACJsonHandler<JsonObject>
{
	// ==================================================
	protected static final String FILE_EXTENSION = "json";
	/**
	 * The {@link Gson} used by {@link AutoConfig} to
	 * serialize and deserialize properties.
	 */
	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	// --------------------------------------------------
	/**
	 * The name of the {@link AutoConfig} file.
	 */
	@NonSerialized
	public final String fileName;
	
	/**
	 * The final path of the {@link AutoConfig} file.
	 * This is where {@link #saveToFile(boolean)} will save to,
	 * and where {@link #loadFromFile(boolean)} will load from.
	 */
	@NonSerialized
	public final Path filePath;
	// ==================================================
	/**
	 * Constructs an {@link AutoConfig} instance.
	 * @param name The name of the config file.
	 * @exception InvalidPathException If the fileName is invalid.
	 */
	public AutoConfig(String name)
	{
		this.fileName = name.strip().replaceAll("[\\\\/:*?\"<>|]", "") + "." + FILE_EXTENSION;
		this.filePath = FabricLoader.getInstance().getConfigDir().resolve(fileName);
	}
	// ==================================================
	/**
	 * Returns all valid property fields for this object.
	 * Both declared fields as well as inherited fields are included.
	 * Property fields must be public non-static primitive types or
	 * {@link ACJsonHandler}s.
	 */
	public List<Field> getPropertyFields()
	{
		return Arrays.asList(getClass().getFields()).stream()
			.filter(field ->
			{
				return  //perform checks
						field.canAccess(this) &&
						(field.getType().isPrimitive() || ACJsonHandler.class.isAssignableFrom(field.getType())) &&
						!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) &&
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
	@Nullable
	protected JsonElement serializeProperty(Field property)
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
	protected boolean deserializeProperty(Field property, JsonElement jEl)
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
	public String getPropertyName(Field property)
	{
		SerializedAs sa = property.getAnnotation(SerializedAs.class);
		if(sa != null) return sa.value();
		else return property.getName();
	}
	// ==================================================
	@Override
	public JsonObject saveToJson()
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
	@Override
	public boolean loadFromJson(JsonObject json)
	{
		for(Field property : getPropertyFields())
		{
			if(json.has(property.getName()))
				deserializeProperty(property, json.get(getPropertyName(property)));
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
	public void saveToFile(boolean log) throws IOException, SecurityException
	{
		JsonObject json = saveToJson();
		String jsonStr = GSON.toJson(json);
		Files.writeString(filePath, jsonStr,
				StandardOpenOption.CREATE_NEW,
				StandardOpenOption.TRUNCATE_EXISTING);
		if(log) LOGGER.info("Saved '" + getClass().getSimpleName() + "' config to '" + fileName + "'.");
	}
	// --------------------------------------------------
	/**
	 * Loads this {@link AutoConfig} from it's file.
	 * @param log Would you like to log this operation to the console?
	 * @throws IOException If an I/O error occurs reading from the file.
	 */
	public void loadFromFile(boolean log) throws IOException
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
		
		if(json != null) loadFromJson(json);
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.util.io.cache;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.CACHED_RESOURCE_SERIALIZER;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.registry.TRegistries;
import net.minecraft.util.Identifier;

/**
 * Responsible for serializing and deserializing cached resources to and from streams.
 * @see TRegistries#CACHED_RESOURCE_SERIALIZER
 */
public abstract class CachedResourceSerializer<T>
{
	// ==================================================
	private final Class<T> type;
	// ==================================================
	public CachedResourceSerializer(Class<T> type) throws NullPointerException
	{
		this.type = Objects.requireNonNull(type);
	}
	// ==================================================
	/**
	 * Returns the {@link Class} representing the type of cached
	 * resource this {@link CachedResourceSerializer} is for.
	 */
	public final Class<T> getResourceType() { return this.type; }
	// --------------------------------------------------
	/**
	 * Serializes a resource to an {@link OutputStream}.
	 * @param value The resource to serialize.
	 * @param stream The {@link OutputStream} to write/serialize the resource to.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalArgumentException If the given resource's type does not match {@link #getResourceType()}.
	 * @throws IOException If an {@link IOException} is raised during serialization.
	 */
	public final void serialize(T value, OutputStream stream)
			throws NullPointerException, IllegalArgumentException, IOException
	{
		//validate arguments
		Objects.requireNonNull(value);
		Objects.requireNonNull(stream);
		if(!Objects.equals(this.type, value.getClass()))
			throw new IllegalArgumentException(String.format(
					"Illegal value type; Expected '%s', got '%s'.",
					this.type.getName(),
					value.getClass().getName()));
		
		//serialize
		onSerialize(value, stream);
	}
	
	/**
	 * Deserializes a resource from an {@link InputStream}.
	 * @param stream The {@link InputStream} to read/deserialize the resource from.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IOException If an {@link IOException} is raised during deserialization.
	 * Additionally, if deserialization returns {@code null}, an {@link IOException} will be raised
	 * with {@link NullPointerException} as the cause. And if the deserialization returns
	 * a type that doesn't match {@link #getResourceType()}, an {@link IOException} will be raised with
	 * {@link ClassCastException} as the cause.
	 */
	public final T deserialize(InputStream stream) throws NullPointerException, IOException
	{
		Objects.requireNonNull(stream);
		T result = null;
		try { result = Objects.requireNonNull(onDeserialize(stream)); }
		catch(NullPointerException npe) { throw new IOException("Deserialization returned null.", npe); }
		
		if(!Objects.equals(this.type, result.getClass()))
			throw new IOException(String.format(
						"Deserialization returned an illegal type; Expected '%s', got '%s'.",
						this.type.getClass().getName(),
						result.getClass().getName()),
					new ClassCastException());
		
		return result;
	}
	// --------------------------------------------------
	protected abstract void onSerialize(T value, OutputStream stream) throws IOException;
	protected abstract T onDeserialize(InputStream stream) throws IOException;
	// ==================================================
	//register serializers for primitive types upon initialization
	public static final void init() { /*calls static*/ }
	static
	{
		//get mod id
		final String modId = getModID();
		
		//String
		CACHED_RESOURCE_SERIALIZER.register(
			new Identifier(modId, String.class.getName().toLowerCase().replace('.', '/')),
			new CachedResourceSerializer<String>(String.class)
			{
				protected final @Override void onSerialize(String value, OutputStream stream) throws IOException {
					if(value == null) value = "";
					stream.write(value.getBytes(StandardCharsets.UTF_16));
				}
				protected final @Override String onDeserialize(InputStream stream) throws IOException {
					return new String(stream.readAllBytes(), StandardCharsets.UTF_16);
				}
			});
		
		//byte[]
		CACHED_RESOURCE_SERIALIZER.register(
				new Identifier(modId, "byte_array"),
				new CachedResourceSerializer<byte[]>(byte[].class)
				{
					protected final @Override void onSerialize(byte[] value, OutputStream stream) throws IOException {
						if(value == null) value = new byte[] {};
						stream.write(value);
					}
					protected final @Override byte[] onDeserialize(InputStream stream) throws IOException {
						return stream.readAllBytes();
					}
				});
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.client.world.registry;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.annotations.Beta;
import com.mojang.datafixers.util.Either;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

@Beta
public final class DirectRegistryEntry<T> implements RegistryEntry<T>
{
	// ==================================================
	private static final Identifier ID_NULL = Identifier.of("null");
	// --------------------------------------------------
	protected final RegistryKey<T> key;
	protected final T value;
	// ==================================================
	@SuppressWarnings("unchecked")
	public DirectRegistryEntry(T value)
	{
		this.value = value;
		try
		{
			Class<?> rkClass = Class.forName(RegistryKey.class.getName());
			Constructor<?> rkInit = rkClass.getDeclaredConstructor(Identifier.class, Identifier.class);
			rkInit.setAccessible(true);
			this.key = (RegistryKey<T>) rkInit.newInstance(ID_NULL, ID_NULL);
		}
		catch(Exception exc) { throw new RuntimeException("Failed to create RegistryKey instance", exc); }
	}
	// ==================================================
	public T value() { return this.value; }
	public boolean hasKeyAndValue() { return true; }
	public boolean matchesKey(RegistryKey<T> key) { return false; }
	public boolean isIn(TagKey<T> tag) { return false; }
	public boolean matches(Predicate<RegistryKey<T>> predicate) { return false; }
	public Either<RegistryKey<T>, T> getKeyOrValue() { return Either.right(this.value); }
	public RegistryEntry.Type getType() { return RegistryEntry.Type.DIRECT; }
	public String toString() { return "Direct{" + this.value + "}"; }
	public boolean ownerEquals(RegistryEntryOwner<T> owner) { return true; }
	public @SuppressWarnings("unchecked") Stream<TagKey<T>> streamTags() { return Stream.of(new TagKey[0]); }
	// --------------------------------------------------
	public Optional<RegistryKey<T>> getKey() { return Optional.of(this.key); }
	public boolean matchesId(Identifier id) { return this.key.getRegistry().equals(id); }
	public boolean matches(RegistryEntry<T> arg0) { return arg0.matchesKey(this.key); }
	// ==================================================
}
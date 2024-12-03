package io.github.thecsdev.tcdcommons.api.util;

import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;

import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Provides utility methods for working with Minecraft's registry system.
 */
public final class RegistryUtils
{
	// ==================================================
	private RegistryUtils() {}
	// ==================================================
	/**
	 * Registers a command {@link ArgumentType}.
	 * @param id The unique {@link Identifier} of the {@link ArgumentType}.
	 * @param argTypeSupplier The {@link ArgumentType} {@link Supplier}.
	 * @throws NullPointerException When an argument is {@code null}, or the {@link Supplier} returns {@code null}.
	 * @apiNote Uses {@link ConstantArgumentSerializer}.
	 */
	public static final void registerCommandArgumentType(
			Identifier id,
			Supplier<ArgumentType<?>> argTypeSupplier) throws NullPointerException
	{
		//requirements
		Objects.requireNonNull(id);
		Objects.requireNonNull(argTypeSupplier);
		final var argType = Objects.requireNonNull(argTypeSupplier.get());
		final var argTypeSerializer = ConstantArgumentSerializer.of(argTypeSupplier);
		
		//handle the class map
		final var classMap = AccessorArgumentTypes.getClassMap();
		classMap.put(argType.getClass(), argTypeSerializer);
		
		//handle the registration to registry
		Registry.register(Registries.COMMAND_ARGUMENT_TYPE, id, argTypeSerializer);
	}
	// ==================================================
}
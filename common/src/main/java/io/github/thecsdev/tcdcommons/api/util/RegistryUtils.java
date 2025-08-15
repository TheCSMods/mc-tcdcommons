package io.github.thecsdev.tcdcommons.api.util;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorArgumentTypes;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

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
	 * @param id The unique {@link ResourceLocation} of the {@link ArgumentType}.
	 * @param argTypeSupplier The {@link ArgumentType} {@link Supplier}.
	 * @throws NullPointerException When an argument is {@code null}, or the {@link Supplier} returns {@code null}.
	 * @apiNote Uses {@link SingletonArgumentInfo}.
	 */
	public static final void registerCommandArgumentType(
			ResourceLocation id,
			Supplier<ArgumentType<?>> argTypeSupplier) throws NullPointerException
	{
		//requirements
		Objects.requireNonNull(id);
		Objects.requireNonNull(argTypeSupplier);
		final var argType = Objects.requireNonNull(argTypeSupplier.get());
		final var argTypeSerializer = SingletonArgumentInfo.contextFree(argTypeSupplier);
		
		//handle the class map
		final var classMap = AccessorArgumentTypes.getClassMap();
		classMap.put(argType.getClass(), argTypeSerializer);
		
		//handle the registration to registry
		Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, id, argTypeSerializer);
	}
	// ==================================================
}
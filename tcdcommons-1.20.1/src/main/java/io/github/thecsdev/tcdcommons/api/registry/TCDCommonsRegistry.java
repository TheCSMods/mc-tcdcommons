package io.github.thecsdev.tcdcommons.api.registry;

import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.arguments.ArgumentType;

import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.mixin.hooks.MixinArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TCDCommonsRegistry
{
	// ==================================================
	protected TCDCommonsRegistry() {}
	// ==================================================
	/**
	 * Maintains a record of all {@link PlayerBadge}s that have been registered during the current session.<br/>
	 * The term "session" has different meanings in different contexts:<br/>
	 * <ul>
	 * <li>In a client context, a "session" refers to a single runtime of the client, beginning
	 * from when the client starts and ending when it stops.</li>
	 * <li>In a server context, a "session" refers to a single runtime of the server, beginning
	 * from when the server starts and ending when it stops.</li>
	 * </ul>
	 */
	public static final BiMap<Identifier, PlayerBadge> PlayerBadges;
	// --------------------------------------------------
	/**
	 * Calls the static constructor for this class if it hasn't been called yet.
	 */
	public static void init() {}
	static
	{
		//define the registries
		PlayerBadges = HashBiMap.create();
	}
	// ==================================================
	/**
	 * A helper method for registering command {@link ArgumentType}s.
	 * @param catId The unique {@link Identifier} for the {@link ArgumentType}.
	 * @param clazz The {@link ArgumentType} generic type reference.
	 * @param catSupplier A {@link Supplier} that will supply instances of the {@link ArgumentType}.
	 */
	public static <T extends ArgumentType<?>> void registerCommandArgumentType(
			Identifier catId, Class<T> clazz, Supplier<T> catSupplier)
	{
		final var catSerializer = ConstantArgumentSerializer.of(catSupplier);
		MixinArgumentTypes.tcdcommons_getClassMap().put(clazz, catSerializer);
		Registry.register(Registries.COMMAND_ARGUMENT_TYPE, catId, catSerializer);
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.command.argument;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.registry.TRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

/**
 * A command {@link ArgumentType} for {@link TRegistry} {@link Identifier} keys.
 */
@Internal @Deprecated final class TRegistryKeyArgumentType implements ArgumentType<Identifier>
{
	// ==================================================
	/**
	 * The unique {@link Identifier} used to register this {@link ArgumentType} to
	 * the {@link Registries#COMMAND_ARGUMENT_TYPE} {@link Registry}.
	 */
	public static final Identifier ID = new Identifier(TCDCommons.getModID(), "t_registry_key");
	// --------------------------------------------------
	private final @Nullable TRegistry<?> registry;
	// ==================================================
	private TRegistryKeyArgumentType() { this.registry = null; }
	private TRegistryKeyArgumentType(TRegistry<?> registry) throws NullPointerException
	{
		this.registry = Objects.requireNonNull(registry);
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link TRegistry} that was assigned to this
	 * {@link TRegistryKeyArgumentType} upon the creation of this {@link Object}.
	 */
	public final @Nullable TRegistry<?> getRegistry() { return this.registry; }
	// ==================================================
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		//if no registry is defined, do not suggest anything
		if(this.registry == null)
			return ArgumentType.super.listSuggestions(context, builder);
		
		//list suggestions based on registry keys
		return CommandSource.suggestMatching(
				StreamSupport.stream(this.registry.spliterator(), false)
					.map(entry -> Objects.toString(entry.getKey())),
				builder);
	}
	// --------------------------------------------------
	public final @Override Identifier parse(StringReader reader) throws CommandSyntaxException
	{
		return Identifier.fromCommandInput(reader);
	}
	// ==================================================
	/**
	 * Creates and returns a new instance of {@link TRegistryKeyArgumentType},
	 * without a {@link TRegistry} defined. Not recommended to use this, as
	 * you may as well use {@link IdentifierArgumentType} at that point.
	 */
	public static @Internal TRegistryKeyArgumentType registryKey() { return new TRegistryKeyArgumentType(); }
	
	/**
	 * Creates and returns a new instance of {@link TRegistryKeyArgumentType}.
	 * @param registry The target {@link TRegistry}.
	 */
	public static TRegistryKeyArgumentType registryKey(TRegistry<?> registry) { return new TRegistryKeyArgumentType(registry); }
	// --------------------------------------------------
	/**
	 * Obtains the {@link TRegistry}'s {@link Identifier} key argument value from a given {@link CommandContext}.
	 * @param context The {@link CommandContext} in which a command is being executed.
	 * @param argumentName The name of the command argument.
	 */
	public static Identifier getRegistryKey(CommandContext<ServerCommandSource> context, String argumentName) { return IdentifierArgumentType.getIdentifier(context, argumentName); }
	// ==================================================
	
}
package io.github.thecsdev.tcdcommons.api.command.argument;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.thecsdev.tcdcommons.TCDCommons;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;

@Internal
@Experimental
public final class StatArgumentType implements ArgumentType<Identifier>
{
	// ==================================================
	/**
	 * The unique {@link Identifier} used to register this {@link ArgumentType} to
	 * the {@link Registries#COMMAND_ARGUMENT_TYPE} {@link Registry}.
	 */
	public static final Identifier ID = new Identifier(TCDCommons.getModID(), "stat");
	// --------------------------------------------------
	private StatType<?> statType;
	private String fallbackSTArgName = "stat_type"; //used when statType is null
	// ==================================================
	private StatArgumentType() {}
	private StatArgumentType(StatType<?> statType) { this.statType = Objects.requireNonNull(statType); }
	private StatArgumentType(String argName) { this.fallbackSTArgName = Objects.requireNonNull(argName); }
	// ==================================================
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		//obtain the stat type
		StatType<?> statType = this.statType;
		if(statType == null && this.fallbackSTArgName != null)
		try
		{
			@SuppressWarnings("unchecked")
			final var ctx = (CommandContext<ServerCommandSource>)context;
			statType = RegistryEntryArgumentType.getRegistryEntry(ctx, this.fallbackSTArgName, RegistryKeys.STAT_TYPE).value();
		}
		catch(CommandSyntaxException | IllegalStateException | ClassCastException e) {}
		if(statType == null) return ArgumentType.super.listSuggestions(context, builder);
		
		//obtain suggestions and null-check them
		@Nullable Iterable<Identifier> suggestions = statType.getRegistry().getKeys()
				.stream()
				.map(key -> key.getValue())
				.toList();
		
		//suggest
		return CommandSource.suggestMatching(
				StreamSupport.stream(suggestions.spliterator(), false)
					.map(entry -> Objects.toString(entry)),
				builder);
	}
	// --------------------------------------------------
	public final @Override Identifier parse(StringReader reader) throws CommandSyntaxException
	{
		return Identifier.fromCommandInput(reader);
	}
	// ==================================================
	public static StatArgumentType stat() { return new StatArgumentType(); }
	public static StatArgumentType stat(StatType<?> statType) { return new StatArgumentType(statType); }
	public static StatArgumentType fromArgument(String argName) { return new StatArgumentType(argName); }
	// ==================================================
}
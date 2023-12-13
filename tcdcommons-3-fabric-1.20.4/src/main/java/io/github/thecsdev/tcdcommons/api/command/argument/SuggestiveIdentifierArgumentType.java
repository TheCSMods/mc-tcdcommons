package io.github.thecsdev.tcdcommons.api.command.argument;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

/**
 * An {@link ArgumentType} for {@link Identifier}s that allows you to customize
 * the behavior of {@link ArgumentType#listSuggestions(CommandContext, SuggestionsBuilder)}.
 */
@Internal @Deprecated final class SuggestiveIdentifierArgumentType implements ArgumentType<Identifier>
{
	// ==================================================
	/**
	 * The unique {@link Identifier} used to register this {@link ArgumentType} to
	 * the {@link Registries#COMMAND_ARGUMENT_TYPE} {@link Registry}.
	 */
	public static final Identifier ID = new Identifier(TCDCommons.getModID(), "t_suggestive_identifier");
	// --------------------------------------------------
	private final @Internal Function<CommandContext<?>, Iterable<Identifier>> suggestionSupplier;
	// ==================================================
	private SuggestiveIdentifierArgumentType() { this.suggestionSupplier = (ctx -> Collections.emptyList()); }
	private SuggestiveIdentifierArgumentType(Function<CommandContext<?>, Iterable<Identifier>> suggestionSupplier)
	{
		this.suggestionSupplier = Objects.requireNonNull(suggestionSupplier);
	}
	// ==================================================
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		//if there is no supplier defined, return super
		if(this.suggestionSupplier == null)
			return ArgumentType.super.listSuggestions(context, builder);
		
		//obtain suggestions and null-check them
		@Nullable Iterable<Identifier> suggestions = this.suggestionSupplier.apply(context);
		if(suggestions == null) suggestions = Collections.emptyList();
		
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
	public static @Internal SuggestiveIdentifierArgumentType suggestiveId() { return new SuggestiveIdentifierArgumentType(); }
	public static SuggestiveIdentifierArgumentType suggestiveId(Function<CommandContext<?>, Iterable<Identifier>> suggestionSupplier)
	{
		return new SuggestiveIdentifierArgumentType(suggestionSupplier);
	}
	// --------------------------------------------------
	public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String argumentName) { return IdentifierArgumentType.getIdentifier(context, argumentName); }
	// ==================================================
}
package io.github.thecsdev.tcdcommons.command.argument;

import static io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry.PlayerBadges;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.thecsdev.tcdcommons.api.client.features.player.badges.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

/**
 * A command {@link ArgumentType} for {@link PlayerBadge} {@link Identifier}s.
 */
public final class PlayerBadgeIdentifierArgumentType implements ArgumentType<Identifier>
{
	// ==================================================
	protected PlayerBadgeIdentifierArgumentType() {}
	// ==================================================
	public static PlayerBadgeIdentifierArgumentType pbId() { return new PlayerBadgeIdentifierArgumentType(); }
	// ==================================================
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		//suggest Identifier-s based on registered non-client-side player badges
		return CommandSource.suggestMatching(
				PlayerBadges.entrySet().stream()
					.filter(entry -> !(entry.getValue() instanceof ClientPlayerBadge))
					.map(entry -> Objects.toString(entry.getKey())),
				builder);
	}
	// --------------------------------------------------
	public @Override Identifier parse(StringReader reader) throws CommandSyntaxException
	{
		return Identifier.fromCommandInput(reader);
	}
	// ==================================================
}
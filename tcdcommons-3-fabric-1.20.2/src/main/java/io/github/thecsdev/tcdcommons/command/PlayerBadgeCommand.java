package io.github.thecsdev.tcdcommons.command;

import static io.github.thecsdev.tcdcommons.api.registry.TRegistries.PLAYER_BADGE;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.github.thecsdev.tcdcommons.api.client.badge.ClientPlayerBadge;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public final class PlayerBadgeCommand
{
	// ==================================================
	//public static final Text TEXT_CLEAR_KICK = translatable("commands.badges.clear.kick"); -- unused
	public static final String TEXT_EDIT_OUTPUT = "commands.badges.edit.output";
	public static final String TEXT_CLEAR_OUTPUT = "commands.badges.clear.output";
	public static final String TEXT_QUERY_OUTPUT = "commands.badges.query.output";
	// ==================================================
	private PlayerBadgeCommand() {}
	// ==================================================
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		//prepare
		final var config = TCDCommons.getInstance().getConfig();
		final var command = literal("badges").requires(scs -> config.enablePlayerBadges && scs.hasPermissionLevel(2));
		
		//arguments
		command.then(badges_edit());
		command.then(badges_clear());
		command.then(badges_query());
		
		//finally, register the command
		dispatcher.register(command);
	}
	// --------------------------------------------------
	private static ArgumentBuilder<ServerCommandSource, ?> badges_edit()
	{
		return literal("edit")
				.then(argument("targets", EntityArgumentType.players())
						.then(argument("badge", IdentifierArgumentType.identifier()).suggests(SUGGEST_PB)
								.then(literal("set")
										.then(argument("value", IntegerArgumentType.integer(0))
												.executes(ctx -> execute_edit(ctx, true))
												)
										)
								.then(literal("increase")
										.then(argument("value", IntegerArgumentType.integer())
												.executes(ctx -> execute_edit(ctx, false))
												)
										)
								)
						);
	}
	private static ArgumentBuilder<ServerCommandSource, ?> badges_clear()
	{
		return literal("clear")
				.then(argument("targets", EntityArgumentType.players())
						.executes(ctx -> execute_clear(ctx)));
	}
	private static ArgumentBuilder<ServerCommandSource, ?> badges_query()
	{
		return literal("query")
				.then(argument("target", EntityArgumentType.player())
						.then(argument("badge", IdentifierArgumentType.identifier()).suggests(SUGGEST_PB)
								.executes(ctx -> execute_query(ctx))
								)
						);
	}
	// --------------------------------------------------
	private static SuggestionProvider<ServerCommandSource> SUGGEST_PB = (context, builder) ->
	{
		//suggest Identifier-s based on registered non-client-side player badges
		return CommandSource.suggestMatching(
				StreamSupport.stream(PLAYER_BADGE.spliterator(), false)
					.filter(entry -> !(entry.getValue() instanceof ClientPlayerBadge))
					.map(entry -> Objects.toString(entry.getKey())),
				builder);
	};// ==================================================
	private static int execute_edit(CommandContext<ServerCommandSource> context, boolean setOrIncrease)
	{
		try
		{
			//get parameter values
			final var arg_targets = EntityArgumentType.getPlayers(context, "targets");
			final var arg_badge = IdentifierArgumentType.getIdentifier(context, "badge");
			final int arg_value = IntegerArgumentType.getInteger(context, "value");
			
			//execute
			final AtomicInteger affected = new AtomicInteger();
			for(final var target : arg_targets)
			{
				//null check
				if(target == null) continue;
				
				//set stat value
				final var statHandler = ServerPlayerBadgeHandler.getServerBadgeHandler(target);
				if(setOrIncrease) statHandler.setValue(arg_badge, arg_value);
				else statHandler.increaseValue(arg_badge, arg_value);
				affected.incrementAndGet();
				
				//update the client
				statHandler.sendStats(target);
			}
			
			//send feedback
			context.getSource().sendFeedback(() -> TCDCT.cmd_pb_edit_out(
					TextUtils.literal(Objects.toString(arg_badge)),
					TextUtils.literal(Integer.toString(affected.get()))
				), false);
			
			//return the number of affected players, so command blocks and data-packs can read it
			return affected.get();
		}
		catch(CommandException | CommandSyntaxException | IllegalStateException | NullPointerException e)
		{
			handleError(context, e);
			return -1;
		}
	}
	private static int execute_clear(CommandContext<ServerCommandSource> context)
	{
		try
		{
			//get parameter values
			final var targets = EntityArgumentType.getPlayers(context, "targets");
			
			//execute
			final AtomicInteger affected = new AtomicInteger();
			for(final var target : targets)
			{
				//null check
				if(target == null) continue;
				
				//clear badges
				ServerPlayerBadgeHandler.getServerBadgeHandler(target).clearBadges();
				affected.incrementAndGet();
				
				//disconnect the player because that's the only way to update the client -- no longer an issue
				/*target.networkHandler.disconnect(TextUtils.literal("")
						.append(TEXT_CLEAR_KICK)
						.append("\n\n[EN]: Your player badge statistics were cleared, which requires you to disconnect and re-join."));*/
			}
			
			//send feedback
			context.getSource().sendFeedback(() -> TCDCT.cmd_pb_clear_out(TextUtils.literal(Integer.toString(affected.get()))), false);
			
			//return the number of affected players, so command blocks and data-packs can read it
			return affected.get();
		}
		catch(CommandException | CommandSyntaxException e)
		{
			handleError(context, e);
			return -1;
		}
	}
	private static int execute_query(CommandContext<ServerCommandSource> context)
	{
		try
		{
			//get parameter values
			final var arg_target = EntityArgumentType.getPlayer(context, "target");
			final var arg_badge = IdentifierArgumentType.getIdentifier(context, "badge");
			
			final var spbh = ServerPlayerBadgeHandler.getServerBadgeHandler(arg_target);
			final var value = spbh.getValue(arg_badge);
			
			//execute
			context.getSource().sendFeedback(() -> TCDCT.cmd_pb_query_out(
					arg_target.getDisplayName(),
					TextUtils.literal(Objects.toString(arg_badge)),
					TextUtils.literal(Integer.toString(value))
				), false);
			return value;
		}
		catch(CommandException | CommandSyntaxException e)
		{
			handleError(context, e);
			return -1;
		}
	}
	// ==================================================
	public static @Internal void handleError(CommandContext<ServerCommandSource> context, Throwable e)
	{
		//handle errors
		if(e instanceof Error)
			throw new Error("Unable to handle errors.", e);
		
		//handle command syntax errors
		context.getSource().sendError(
				translatable("command.failed")
				.append(":\n    " + e.getMessage()));
	}
	// ==================================================
}
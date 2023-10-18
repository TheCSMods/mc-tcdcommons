package io.github.thecsdev.tcdcommons.command;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.ServerPlayerBadgeHandler;
import io.github.thecsdev.tcdcommons.command.argument.PlayerBadgeIdentifierArgumentType;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public final class PlayerBadgeCommand
{
	// ==================================================
	//public static final Text TEXT_CLEAR_KICK = translatable("commands.badges.clear.kick"); -- unused
	public static final String TEXT_EDIT_OUTPUT = "commands.badges.edit.output";
	public static final String TEXT_CLEAR_OUTPUT = "commands.badges.clear.output";
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
		
		//finally, register the command
		dispatcher.register(command);
	}
	// --------------------------------------------------
	private static ArgumentBuilder<ServerCommandSource, ?> badges_edit()
	{
		return literal("edit")
				.then(argument("targets", EntityArgumentType.players())
						.then(argument("badge", PlayerBadgeIdentifierArgumentType.pbId())
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
	// ==================================================
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
			context.getSource().sendFeedback(() -> translatable(
					TEXT_EDIT_OUTPUT,
					Objects.toString(arg_badge),
					Integer.toString(affected.get())
				), false);
		}
		catch(CommandException | CommandSyntaxException | IllegalStateException | NullPointerException e) { handleError(context, e); }
		return 1;
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
			context.getSource().sendFeedback(() -> translatable(TEXT_CLEAR_OUTPUT, Integer.toString(affected.get())), false);
		}
		catch(CommandException | CommandSyntaxException e) { handleError(context, e); }
		return 1;
	}
	// ==================================================
	public static @Internal void handleError(CommandContext<ServerCommandSource> context, Throwable e)
	{
		//handle command syntax errors
		context.getSource().sendError(
				translatable("command.failed")
				.append(":\n    " + e.getMessage()));
	}
	// ==================================================
}
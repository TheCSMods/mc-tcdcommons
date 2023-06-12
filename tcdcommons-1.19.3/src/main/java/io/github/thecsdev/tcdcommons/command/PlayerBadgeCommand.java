package io.github.thecsdev.tcdcommons.command;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Objects;
import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.thecsdev.tcdcommons.api.features.player.badges.ServerPlayerBadgeHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public final class PlayerBadgeCommand
{
	// ==================================================
	protected PlayerBadgeCommand() {}
	// ==================================================
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		//# Permission levels:
		//Level 0 - Player
		//Level 1 - Moderator
		//Level 2 - Game-master
		//Level 3 - Administrator
		//Level 4 - Owner aka operator
		
		//register command with permission level 3
		dispatcher.register(literal("badge").requires(scs -> scs.hasPermissionLevel(3))
				.then(literal("grant")
						.then(argument("targets", EntityArgumentType.players())
								.then(argument("badge_id", IdentifierArgumentType.identifier())
										.executes(context -> execute_grantOrRevoke(context, true)))))
				.then(literal("revoke")
						.then(argument("targets", EntityArgumentType.players())
								.then(argument("badge_id", IdentifierArgumentType.identifier())
										.executes(context -> execute_grantOrRevoke(context, false)))))
				.then(literal("list")
						.then(argument("target", EntityArgumentType.player())
								.executes(context -> execute_list(context))))
		);
	}
	// ==================================================
	private static int execute_grantOrRevoke(CommandContext<ServerCommandSource> context, boolean grant)
	{
		try
		{
			//get parameter values
			final var targets = EntityArgumentType.getPlayers(context, "targets");
			final var badgeId = IdentifierArgumentType.getIdentifier(context, "badge_id");
			//execute
			for(var target : targets)
			{
				//null check
				if(target == null) continue;
				//grant or revoke
				if(grant) ServerPlayerBadgeHandler.getBadgeHandler(target).addBadge(badgeId);
				else ServerPlayerBadgeHandler.getBadgeHandler(target).removeBadge(badgeId);
			}
			//send feedback
			final var feedbackGoR = grant ?
					"commands.badge.grant.one.to_many.success" :
					"commands.badge.revoke.one.to_many.success";
			final var feedback = translatable(feedbackGoR,
					Objects.toString(badgeId),
					Objects.toString(targets.size()));
			context.getSource().sendFeedback(feedback, false);
		}
		catch (CommandException | CommandSyntaxException e) { handleError(context, e); }
		return 1;
	}
	// --------------------------------------------------
	private static int execute_list(CommandContext<ServerCommandSource> context)
	{
		try
		{
			//get parameter values
			final var target = EntityArgumentType.getPlayer(context, "target");
			//get badges
			final var badges = ServerPlayerBadgeHandler.getBadgeHandler(target).getBadges()
				    .stream()
				    .map(Identifier::toString) // convert each Identifier object to String
				    .collect(Collectors.joining(", ")); // join with a comma and space
			//send feedback
			final var feedback = translatable("commands.badge.list.of_one",
					target.getDisplayName().getString(),
					badges);
			context.getSource().sendFeedback(feedback, false);
		}
		catch(CommandException | CommandSyntaxException e) { handleError(context, e); }
		return 1;
	}
	// ==================================================
	private static void handleError(CommandContext<ServerCommandSource> context, Throwable e)
	{
		//handle command syntax errors
		if(e instanceof CommandSyntaxException)
			context.getSource().sendError(
					translatable("command.failed")
					.append(":\n    " + e.getMessage()));
		else if(e instanceof CommandException)
			context.getSource().sendError(
					translatable("command.failed")
					.append(":\n    ")
					.append(((CommandException)e).getTextMessage()));
	}
	// ==================================================
}
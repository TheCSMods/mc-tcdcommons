package io.github.thecsdev.tcdcommons.api.events.server.command;

import com.mojang.brigadier.CommandDispatcher;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandManagerEvent
{
	/**
	 * See {@link CommandRegistrationCallback#invoke}
	 */
	TEvent<CommandRegistrationCallback> COMMAND_REGISTRATION_CALLBACK = TEventFactory.createLoop();
	
	interface CommandRegistrationCallback
	{
		/**
		 * Invoked when a {@link CommandManager} is in the process of registering
		 * commands into it. Use this to register your own commands.
		 */
		void invoke(
				CommandDispatcher<ServerCommandSource> dispatcher,
				CommandRegistryAccess registryAccess,
				CommandManager.RegistrationEnvironment environment);
	}
}
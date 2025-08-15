package io.github.thecsdev.tcdcommons.api.events.server.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface CommandManagerEvent
{
	/**
	 * See {@link CommandRegistrationCallback#invoke}
	 */
	TEvent<CommandRegistrationCallback> COMMAND_REGISTRATION_CALLBACK = TEventFactory.createLoop();
	
	interface CommandRegistrationCallback
	{
		/**
		 * Invoked when a {@link Commands} is in the process of registering
		 * commands into it. Use this to register your own commands.
		 */
		void invoke(
				CommandDispatcher<CommandSourceStack> dispatcher,
				CommandBuildContext registryAccess,
				Commands.CommandSelection environment);
	}
}
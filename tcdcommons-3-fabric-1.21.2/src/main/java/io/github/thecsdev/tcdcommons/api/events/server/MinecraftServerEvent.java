package io.github.thecsdev.tcdcommons.api.events.server;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.server.MinecraftServer;

public interface MinecraftServerEvent
{
	/**
	 * See {@link TickedWorlds#invoke(MinecraftServer)}
	 */
	TEvent<TickedWorlds> TICKED_WORLDS = TEventFactory.createLoop();
	
	interface TickedWorlds
	{
		/**
		 * A {@link TEvent} that is invoked every time a {@link MinecraftServer} finishes ticking its worlds.
		 * @param server The {@link MinecraftServer}.
		 */
		public void invoke(MinecraftServer server);
	}
}
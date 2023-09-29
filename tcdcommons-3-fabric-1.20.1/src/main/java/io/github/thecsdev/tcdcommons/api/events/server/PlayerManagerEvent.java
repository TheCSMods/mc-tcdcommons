package io.github.thecsdev.tcdcommons.api.events.server;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerManagerEvent
{
	/**
	 * See {@link PlayerConnected#invoke(ServerPlayerEntity)}
	 */
	TEvent<PlayerConnected> PLAYER_CONNECTED = TEventFactory.createLoop();
	
	interface PlayerConnected
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayerEntity} joins a {@link MinecraftServer}.
		 * @param player The {@link ServerPlayerEntity} that was added to a {@link PlayerManager}'s player list.
		 */
		public void invoke(ServerPlayerEntity player);
	}
}
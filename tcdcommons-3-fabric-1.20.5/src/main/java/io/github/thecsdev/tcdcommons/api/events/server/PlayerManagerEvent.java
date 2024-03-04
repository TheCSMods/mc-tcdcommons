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
	
	/**
	 * See {@link PlayerChatted#invoke(ServerPlayerEntity, String)}
	 */
	TEvent<PlayerChatted> PLAYER_CHATTED = TEventFactory.createLoop();
	
	interface PlayerConnected
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayerEntity} joins a {@link MinecraftServer}.
		 * @param player The {@link ServerPlayerEntity} that was added to a {@link PlayerManager}'s player list.
		 */
		public void invoke(ServerPlayerEntity player);
	}
	
	interface PlayerChatted
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayerEntity} sends a chat message.
		 * @param player The {@link ServerPlayerEntity} that chatted.
		 * @param chatMessage The chat message they sent.
		 */
		public void invoke(ServerPlayerEntity player, String chatMessage);
	}
}
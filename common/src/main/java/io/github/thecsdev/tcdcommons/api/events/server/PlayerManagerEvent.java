package io.github.thecsdev.tcdcommons.api.events.server;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public interface PlayerManagerEvent
{
	/**
	 * See {@link PlayerConnected#invoke(ServerPlayer)}
	 */
	TEvent<PlayerConnected> PLAYER_CONNECTED = TEventFactory.createLoop();
	
	/**
	 * See {@link PlayerChatted#invoke(ServerPlayer, String)}
	 */
	TEvent<PlayerChatted> PLAYER_CHATTED = TEventFactory.createLoop();
	
	interface PlayerConnected
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayer} joins a {@link MinecraftServer}.
		 * @param player The {@link ServerPlayer} that was added to a {@link PlayerList}'s player list.
		 */
		public void invoke(ServerPlayer player);
	}
	
	interface PlayerChatted
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayer} sends a chat message.
		 * @param player The {@link ServerPlayer} that chatted.
		 * @param chatMessage The chat message they sent.
		 */
		public void invoke(ServerPlayer player, String chatMessage);
	}
}
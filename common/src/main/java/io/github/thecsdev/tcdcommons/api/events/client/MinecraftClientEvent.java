package io.github.thecsdev.tcdcommons.api.events.client;

import com.mojang.blaze3d.platform.Window;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;

public interface MinecraftClientEvent
{
	// ==================================================
	/**
	 * See {@link SetScreenPost#invoke(Screen)}
	 */
	TEvent<SetScreenPost> SET_SCREEN_POST = TEventFactory.createLoop();
	
	/**
	 * See {@link ResolutionChanged#invoke(int, int)}
	 */
	TEvent<ResolutionChanged> RESOLUTION_CHANGED = TEventFactory.createLoop();
	// --------------------------------------------------
	/**
	 * See {@link JoinClientWorld#invoke(Minecraft, ClientLevel)}
	 */
	TEvent<JoinClientWorld> JOINED_WORLD = TEventFactory.createLoop();
	
	/**
	 * See {@link ClientDisconnect#invoke(Minecraft)}
	 */
	TEvent<ClientDisconnect> DISCONNECTED = TEventFactory.createLoop();
	// ==================================================
	interface SetScreenPost
	{
		/**
		 * An event that is invoked AFTER a {@link Screen} has
		 * been set for the {@link Minecraft}.
		 * @param screen The new {@link Screen}.
		 */
		void invoke(Screen screen);
	}
	
	interface ResolutionChanged
	{
		/**
		 * An event that is invoked when the {@link Minecraft}'s
		 * {@link Window} resolution changes and when it is resized.
		 * @see Minecraft#getWindow()
		 * @see Window#getGuiScaledWidth()
		 * @see Window#getGuiScaledHeight()
		 */
		void invoke(int newScaledWidth, int newScaledHeight);
	}
	// --------------------------------------------------
	interface JoinClientWorld
	{
		/**
		 * A {@link TEvent} that is invoked when the
		 * {@link Minecraft} joins a {@link ClientLevel}.
		 * @param client The {@link Minecraft}.
		 * @param clientWorld The {@link ClientLevel} that was joined.
		 */
		public void invoke(Minecraft client, ClientLevel clientWorld);
	}
	
	interface ClientDisconnect
	{
		/**
		 * A {@link TEvent} that is invoked when the {@link Minecraft} disconnects.
		 * @param client The {@link Minecraft}.
		 */
		public void invoke(Minecraft client);
	}
	// ==================================================
}
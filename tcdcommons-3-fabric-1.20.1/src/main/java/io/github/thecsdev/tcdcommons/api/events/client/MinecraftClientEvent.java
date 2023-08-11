package io.github.thecsdev.tcdcommons.api.events.client;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

public interface MinecraftClientEvent
{
	/**
	 * See {@link SetScreenPost#invoke(Screen)}
	 */
	TEvent<SetScreenPost> SET_SCREEN_POST = TEventFactory.createLoop();
	
	/**
	 * See {@link ResolutionChanged#invoke(int, int)}.
	 */
	TEvent<ResolutionChanged> RESOLUTION_CHANGED = TEventFactory.createLoop();
	
	interface SetScreenPost
	{
		/**
		 * An event that is invoked AFTER a {@link Screen} has
		 * been set for the {@link MinecraftClient}.
		 * @param screen The new {@link Screen}.
		 */
		void invoke(Screen screen);
	}
	
	interface ResolutionChanged
	{
		/**
		 * An event that is invoked when the {@link MinecraftClient}'s
		 * {@link Window} resolution changes and when it is resized.
		 * @see MinecraftClient#getWindow()
		 * @see Window#getScaledWidth()
		 * @see Window#getScaledHeight()
		 */
		void invoke(int newScaledWidth, int newScaledHeight);
	}
}
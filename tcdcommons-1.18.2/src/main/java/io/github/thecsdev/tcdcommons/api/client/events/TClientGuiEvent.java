package io.github.thecsdev.tcdcommons.api.client.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public interface TClientGuiEvent
{
	// ==================================================
	/**
	 * See {@link PostSetScreen#postSetScreen(Screen)}
	 */
	Event<PostSetScreen> POST_SET_SCREEN = EventFactory.createLoop();
	// ==================================================
	interface PostSetScreen
	{
		/**
		 * An event that is invoked AFTER a {@link Screen} has
		 * been set for the {@link MinecraftClient}.
		 * @param screen The new {@link Screen}.
		 */
		void postSetScreen(Screen screen);
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public interface TClientEvent
{
	// ==================================================
	/**
	 * See {@link ResolutionChanged#resolutionChanged()}.
	 */
	Event<ResolutionChanged> RESOLUTION_CHANGED = EventFactory.createLoop();
	// ==================================================
	interface ResolutionChanged
	{
		/**
		 * An event that is invoked when the {@link MinecraftClient}'s
		 * {@link Window} resolution changes and when it is resized.
		 * @see MinecraftClient#getWindow()
		 * @see Window#getScaledWidth()
		 * @see Window#getScaledHeight()
		 */
		void resolutionChanged();
	}
	// ==================================================
}
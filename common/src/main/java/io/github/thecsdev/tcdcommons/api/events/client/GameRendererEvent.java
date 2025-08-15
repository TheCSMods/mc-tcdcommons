package io.github.thecsdev.tcdcommons.api.events.client;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Provides {@link TEvent}s for the {@link GameRenderer}.
 */
public interface GameRendererEvent
{
	/**
	 * See {@link RenderPost#invoke(float)}
	 */
	TEvent<RenderPost> RENDER_POST = TEventFactory.createLoop();
	
	interface RenderPost
	{
		/**
		 * An event that is invoked AFTER a {@link GameRenderer} renders.
		 * @param tickDelta The delta time in ticks.
		 */
		void invoke(float tickDelta);
	}
}
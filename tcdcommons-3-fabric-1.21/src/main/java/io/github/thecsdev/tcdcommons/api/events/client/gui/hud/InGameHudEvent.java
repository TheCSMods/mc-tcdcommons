package io.github.thecsdev.tcdcommons.api.events.client.gui.hud;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.event.TEventResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

public interface InGameHudEvent
{
	/**
	 * See {@link RenderPre#invoke(DrawContext, float)}
	 */
	TEvent<RenderPre> RENDER_PRE = TEventFactory.createEventResult();
	
	/**
	 * See {@link RenderPost#invoke(DrawContext, float)}
	 */
	TEvent<RenderPost> RENDER_POST = TEventFactory.createLoop();
	
	interface RenderPre
	{
		/**
		 * An event that is invoked BEFORE an {@link InGameHud} renders.
		 * @param pencil The rendering {@link DrawContext}.
		 * @param tickDelta The delta time in ticks.
		 */
		TEventResult invoke(DrawContext pencil, float tickDelta);
	}
	
	interface RenderPost
	{
		/**
		 * An event that is invoked AFTER an {@link InGameHud} renders.
		 * @param pencil The rendering {@link DrawContext}.
		 * @param tickDelta The delta time in ticks.
		 */
		void invoke(DrawContext pencil, float tickDelta);
	}
}
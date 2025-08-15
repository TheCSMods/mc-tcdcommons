package io.github.thecsdev.tcdcommons.api.events.client.gui.hud;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.event.TEventResult;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

public interface InGameHudEvent
{
	/**
	 * See {@link RenderPre#invoke(GuiGraphics, float)}
	 */
	TEvent<RenderPre> RENDER_PRE = TEventFactory.createEventResult();
	
	/**
	 * See {@link RenderPost#invoke(GuiGraphics, float)}
	 */
	TEvent<RenderPost> RENDER_POST = TEventFactory.createLoop();
	
	interface RenderPre
	{
		/**
		 * An event that is invoked BEFORE an {@link Gui} renders.
		 * @param pencil The rendering {@link GuiGraphics}.
		 * @param tickDelta The delta time in ticks.
		 */
		TEventResult invoke(GuiGraphics pencil, float tickDelta);
	}
	
	interface RenderPost
	{
		/**
		 * An event that is invoked AFTER an {@link Gui} renders.
		 * @param pencil The rendering {@link GuiGraphics}.
		 * @param tickDelta The delta time in ticks.
		 */
		void invoke(GuiGraphics pencil, float tickDelta);
	}
}
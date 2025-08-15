package io.github.thecsdev.tcdcommons.api.events.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.event.TEventResult;
import net.minecraft.client.gui.screens.PauseScreen;

public interface GameMenuScreenEvent
{
	/**
	 * See {@link InitWidgetsPre#invoke(PauseScreen)}
	 */
	TEvent<InitWidgetsPre> INIT_WIDGETS_PRE = TEventFactory.createEventResult();
	
	/**
	 * See {@link InitWidgetsPost#invoke(PauseScreen)}
	 */
	TEvent<InitWidgetsPost> INIT_WIDGETS_POST = TEventFactory.createLoop();
	
	interface InitWidgetsPre
	{
		/**
		 * An event that is invoked BEFORE a {@link PauseScreen}'s
		 * widgets are initialized.
		 * @param screen The {@link PauseScreen} in question.
		 */
		TEventResult invoke(PauseScreen screen);
	}
	
	interface InitWidgetsPost
	{
		/**
		 * An event that is invoked AFTER a {@link PauseScreen}'s
		 * widgets are initialized.
		 * @param screen The {@link PauseScreen} in question.
		 */
		void invoke(PauseScreen screen);
	}
}
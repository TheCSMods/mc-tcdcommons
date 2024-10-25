package io.github.thecsdev.tcdcommons.api.events.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.event.TEventResult;
import net.minecraft.client.gui.screen.GameMenuScreen;

public interface GameMenuScreenEvent
{
	/**
	 * See {@link InitWidgetsPre#invoke(GameMenuScreen)}
	 */
	TEvent<InitWidgetsPre> INIT_WIDGETS_PRE = TEventFactory.createEventResult();
	
	/**
	 * See {@link InitWidgetsPost#invoke(GameMenuScreen)}
	 */
	TEvent<InitWidgetsPost> INIT_WIDGETS_POST = TEventFactory.createLoop();
	
	interface InitWidgetsPre
	{
		/**
		 * An event that is invoked BEFORE a {@link GameMenuScreen}'s
		 * widgets are initialized.
		 * @param screen The {@link GameMenuScreen} in question.
		 */
		TEventResult invoke(GameMenuScreen screen);
	}
	
	interface InitWidgetsPost
	{
		/**
		 * An event that is invoked AFTER a {@link GameMenuScreen}'s
		 * widgets are initialized.
		 * @param screen The {@link GameMenuScreen} in question.
		 */
		void invoke(GameMenuScreen screen);
	}
}
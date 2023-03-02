package io.github.thecsdev.tcdcommons.api.client.events.screen;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.client.gui.screen.GameMenuScreen;

public interface TGameMenuScreenEvent
{
	// ==================================================
	/**
	 * See {@link InitWidgetsPre#initWidgetsPre(GameMenuScreen)}
	 */
	Event<InitWidgetsPre> INIT_WIDGETS_PRE = EventFactory.createEventResult();
	
	/**
	 * See {@link InitWidgetsPost#initWidgetsPost(GameMenuScreen)}
	 */
	Event<InitWidgetsPre> INIT_WIDGETS_POST = EventFactory.createLoop();
	// ==================================================
	interface InitWidgetsPre
	{
		/**
		 * An event that is invoked BEFORE a {@link GameMenuScreen}'s
		 * widgets are initialized.
		 * @param screen The {@link GameMenuScreen} in question.
		 */
		EventResult initWidgetsPre(GameMenuScreen screen);
	}
	
	interface InitWidgetsPost
	{
		/**
		 * An event that is invoked AFTER a {@link GameMenuScreen}'s
		 * widgets are initialized.
		 * @param screen The {@link GameMenuScreen} in question.
		 */
		void initWidgetsPost(GameMenuScreen screen);
	}
	// ==================================================
}
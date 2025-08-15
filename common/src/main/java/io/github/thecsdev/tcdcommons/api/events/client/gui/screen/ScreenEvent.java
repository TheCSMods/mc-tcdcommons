package io.github.thecsdev.tcdcommons.api.events.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

public interface ScreenEvent
{
	/**
	 * See {@link InitPost#invoke(Screen)}
	 */
	TEvent<InitPost> INIT_POST = TEventFactory.createLoop();
	
	interface InitPost
	{
		/**
		 * An event that is triggered after a {@link Screen} object has been initialized.
		 * This event provides an opportunity for listeners to perform actions or modify the
		 * state of the {@link Screen} after its initialization.
		 *
		 * <p>This event uses a {@link Consumer} interface for its listeners, which accept
		 * a {@link Screen} argument representing the {@link Screen} that was initialized.</p>
		 * 
		 * @param screen The {@link Screen} in question.
		 */
		void invoke(Screen screen);
	}
}
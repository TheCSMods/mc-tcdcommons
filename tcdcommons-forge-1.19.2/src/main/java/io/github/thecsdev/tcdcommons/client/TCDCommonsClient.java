package io.github.thecsdev.tcdcommons.client;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TCDCommonsClient extends TCDCommons
{
	// ==================================================
	private java.lang.reflect.Method TSCREEN_METHOD_ONOPENED;
	// ==================================================
	public TCDCommonsClient()
	{
		//init client registry
		TCDCommonsClientRegistry.init();
		//init reflection method for TScreen#onOpened
		try
		{
			TSCREEN_METHOD_ONOPENED = TScreen.class.getDeclaredMethod("onOpened");
			TSCREEN_METHOD_ONOPENED.setAccessible(true);
		}
		catch(NoSuchMethodException e)
		{
			String msg = "[" + TCDCommons.getModID() + "] " + "Failed to obtain TScreen#onOpened()";
			TCDCommons.crash(msg, e);
		}
	}
	// ==================================================
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onScreenOpened(ScreenEvent e)
	{
		//get TScreen
		TScreen ts = (e.getScreen() instanceof TScreen) ? (TScreen)e.getScreen() : null;
		if(ts == null) return;
		//invoke onOpened
		try { TSCREEN_METHOD_ONOPENED.invoke((ts)); }
		catch(Exception exc)
		{
			String msg = "[" + TCDCommons.getModID() + "] " + "Failed to invoke TScreen#onOpened()";
			TCDCommons.crash(msg, exc);
		}
	}
	// ==================================================
}
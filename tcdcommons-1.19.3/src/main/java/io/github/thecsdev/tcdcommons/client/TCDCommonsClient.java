package io.github.thecsdev.tcdcommons.client;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.events.TClientEvent;
import io.github.thecsdev.tcdcommons.api.client.events.TClientGuiEvent;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import io.github.thecsdev.tcdcommons.test.client.gui.screen.TestTScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.MutableText;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public final class TCDCommonsClient extends TCDCommons implements ClientModInitializer
{
	// ==================================================
	/**
	 * A reference to the {@link #onOpened()} method that used
	 * my mixins to invoke that method when this {@link TScreen}
	 * is opened. Please avoid using this yourself, as it may
	 * be subject to changes.
	 */
	private final java.lang.reflect.Method TSCREEN_METHOD_ONOPENED;
	// --------------------------------------------------
	/** The main instance of {@link MinecraftClient}. */
	private MinecraftClient client;
	// ==================================================
	public TCDCommonsClient()
	{
		//handle reflection for TScreen
		try
		{
			TSCREEN_METHOD_ONOPENED = TScreen.class.getDeclaredMethod("onOpened");
			TSCREEN_METHOD_ONOPENED.setAccessible(true);
		}
		catch(NoSuchMethodException e)
		{
			String msg = "[" + TCDCommons.getModID() + "] " + "Failed to obtain TScreen#onOpened()";
			throw new CrashException(new CrashReport(msg, e));
		}
	}
	// --------------------------------------------------
	@Override
	public void onInitializeClient()
	{
		this.client = MinecraftClient.getInstance();
		
		//init the client registry API
		TCDCommonsClientRegistry.init();
		
		//MixinMinecraftClient - Reflection handling for TScreen
		TClientGuiEvent.POST_SET_SCREEN.register(newScreen ->
		{
			//if a TScreen is about to be set...
			if(newScreen instanceof TScreen)
			{
				//...invoke onOpened()
				try { TSCREEN_METHOD_ONOPENED.invoke(((TScreen)newScreen)); }
				catch(Exception e)
				{
					String msg = "[" + TCDCommons.getModID() + "] " + "Failed to invoke TScreen#onOpened()";
					throw new CrashException(new CrashReport(msg, e));
				}
			}
		});
		
		//MixinMinecraftClient - Handling hud screen resizing
		TClientEvent.RESOLUTION_CHANGED.register(() ->
		{
			//basically update the sizes of hud screens
			TCDCommonsClientRegistry.reInitHudScreens();
		});
		
		//MixinTitleScreen - Testing button for the testing screen
		dev.architectury.event.events.client.ClientGuiEvent.INIT_POST.register((newScreen, nsAccess) ->
		{
			//check screen type
			if(!(newScreen instanceof TitleScreen)) return;
			
			//this will only be available in development
			if(!FabricLoader.getInstance().isDevelopmentEnvironment()) return; //TODO - ELEMINATE FABRIC APIs
			
			//add a testing button
			MutableText msg = TextUtils.fLiteral("§e" + TCDCommons.getModName());
			PressAction onPress = arg0 -> { client.setScreen(new TestTScreen(newScreen)); };
			ButtonWidget btn = ButtonWidget.builder(msg, onPress).dimensions(10, newScreen.height - 50, 125, 20).build();
			dev.architectury.hooks.client.screen.ScreenHooks.addRenderableWidget(newScreen, btn);
			
			//return
			return;
		});
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.client;

import dev.architectury.event.CompoundEventResult;
import io.github.thecsdev.tcdcommons.TCDCommons;
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
		dev.architectury.event.events.client.ClientGuiEvent.SET_SCREEN.register(newScreen ->
		{
			//if a TScreen is about to be set...
			if(newScreen instanceof TScreen/* && this.client.currentScreen == newScreen*/)
			{
				//...invoke onOpened()
				try { TSCREEN_METHOD_ONOPENED.invoke(((TScreen)newScreen)); }
				catch(Exception e)
				{
					String msg = "[" + TCDCommons.getModID() + "] " + "Failed to invoke TScreen#onOpened()";
					throw new CrashException(new CrashReport(msg, e));
				}
			}
			//return
			return CompoundEventResult.pass();
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
		
		//MixinInGameHud - Handling hud rendering
		/*float tcdcommons_tickDeltaTime = 0;
		dev.architectury.event.events.client.ClientGuiEvent.RENDER_HUD.register((matrices, tickDelta) ->
		{
			//check if the current screen is a TScreen
			if(this.client == null || !(this.client.currentScreen instanceof TScreen))
				return;
			
			//ask the currently opened TScreen if this hud should be rendered
			if(!((TScreen)this.client.currentScreen).shouldRenderInGameHud())
				return;
			
			//keep track of tick delta time
			tcdcommons_tickDeltaTime += tickDelta;
			
			//get mouse XY
			//int mX = scaledWidth / 2, mY = scaledHeight / 2;
			int mX = (int)(this.client.mouse.getX() * scaledWidth / this.client.getWindow().getWidth());
		    int mY = (int)(this.client.mouse.getY() * scaledHeight / this.client.getWindow().getHeight());
		    
			//iterate and render all hud screens
		    boolean tick = tcdcommons_tickDeltaTime > 1;
			for(var hScreen : TCDCommonsClientRegistry.InGameHud_Screens.entrySet())
			{
				//do not handle current screen
				if(client.currentScreen == hScreen.getValue()) continue;
				//render and tick if needed
				hScreen.getValue().render(matrices, mX, mY, tickDelta);
				if(tick) hScreen.getValue().tick();
			}
			
			//clear delta time if ticked
			if(tick) tcdcommons_tickDeltaTime = 0;
		});*/
	}
	// ==================================================
}
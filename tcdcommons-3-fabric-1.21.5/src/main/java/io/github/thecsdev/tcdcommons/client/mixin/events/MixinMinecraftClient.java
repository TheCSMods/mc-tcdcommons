package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.mixin.TCMInternal.CURRENT_T_SCREEN;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenWrapper;
import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.events.client.MinecraftClientEvent;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorTScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
{
	// ==================================================
	public @Shadow Window window;
	public @Shadow Screen currentScreen;
	// ==================================================
	@Inject(method = "onResolutionChanged", at = @At("RETURN"))
	public void onResolutionChanged(CallbackInfo callback)
	{
		//initialize hud-screens
		TClientRegistries.reInitHudScreens();
		
		//invoke the resolution change event
		final int sW = window.getScaledWidth(), sH = window.getScaledHeight();
		MinecraftClientEvent.RESOLUTION_CHANGED.invoker().invoke(sW, sH);
	}
	// --------------------------------------------------
	@Inject(method = "setScreen", at = @At("RETURN"))
	public void onSetScreen(Screen screen, CallbackInfo callback)
	{
		//first make sure the screen got properly assigned and set
		if(this.currentScreen != screen) return;
		
		//track the currently opened TScreen, if any;
		//use try-catch to prevent other mods from breaking the TScreen-tracking
		try
		{
			//call `onClosed` on any currently opened TScreen-s
			if(CURRENT_T_SCREEN != null && screen != CURRENT_T_SCREEN.getAsScreen())
			{
				//invoke `onClosed` for any opened TScreen-s
				final var i = ((AccessorTScreen)CURRENT_T_SCREEN);
				CURRENT_T_SCREEN.setFocusedElement(null); //clear any focus when the screen closes
				CURRENT_T_SCREEN = null; //prevent StackOverflowError issues
				i.tcdcommons_onClosed();
				//prevent further execution if `onClosed` changed the screen
				if(this.currentScreen != screen)
					return;
			}
			//call `onOpened` on any TScreen-s that just got opened
			if(screen instanceof TScreenWrapper)
			{
				CURRENT_T_SCREEN = ((TScreenWrapper<?>)screen).getTargetTScreen();
				CURRENT_T_SCREEN.setFocusedElement(null); //clear any focus when the screen opens
				((AccessorTScreen)CURRENT_T_SCREEN).tcdcommons_onOpened();
			}
			else CURRENT_T_SCREEN = null;
		}
		catch(Exception exc)
		{
			//if an exception is raised, track the opened TScreen;
			//this is a fail-safe, to make sure nothing breaks in the event the exception ends up being handled
			if(screen instanceof TScreenWrapper) CURRENT_T_SCREEN = ((TScreenWrapper<?>)screen).getTargetTScreen();
			else CURRENT_T_SCREEN = null;
			
			//finally throw the raised exception, but wrap it as an Error
			//^ not recommended to try and catch the Error tho, as onOpened/onClosed hasn't been called properly,
			//  meaning that catching the Error will very likely break other mods
			final String msg = "An '%s' was raised where it shouldn't have been, and '%s' is unable to "
					+ "continue keeping track of '%s's.";
			throw new Error(String.format(msg,
					Exception.class.getSimpleName(),
					TCDCommons.getModName(),
					TScreen.class.getSimpleName()
				), exc);
		}
		
		//invoke the screen change event
		MinecraftClientEvent.SET_SCREEN_POST.invoker().invoke(screen);
	}
	// --------------------------------------------------
	/*@Inject(method = "joinWorld", at = @At("RETURN")) -- MOVED TO ClientPlayNetworkHandler
	public void onJoinWorld(ClientWorld clientWorld, CallbackInfo callback)
	{
		final var MC_CLIENT = (MinecraftClient)(Object)this;
		
		//re-initialize hud screens
		TaskScheduler.executeOnce(
				MC_CLIENT,
				() -> MC_CLIENT.player != null, //player cannot be null here
				() -> TClientRegistries.reInitHudScreens());
		
		//invoke event
		MinecraftClientEvent.JOINED_WORLD.invoker().invoke(MC_CLIENT, clientWorld);
	}*/
	// --------------------------------------------------
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
	public void onDisconnect(Screen screen, CallbackInfo callback)
	{
		//invoke event
		MinecraftClientEvent.DISCONNECTED.invoker().invoke((MinecraftClient)(Object)this);
	}
	// ==================================================
}
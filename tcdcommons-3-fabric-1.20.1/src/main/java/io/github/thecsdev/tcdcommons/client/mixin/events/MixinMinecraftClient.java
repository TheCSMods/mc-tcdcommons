package io.github.thecsdev.tcdcommons.client.mixin.events;

import static io.github.thecsdev.tcdcommons.client.mixin.TCMInternal.CURRENT_T_SCREEN;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenWrapper;
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
			//finally throw the raised exception
			throw exc;
		}
		
		//invoke the screen change event
		MinecraftClientEvent.SET_SCREEN_POST.invoker().invoke(screen);
	}
	// ==================================================
}
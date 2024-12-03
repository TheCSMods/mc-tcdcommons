package io.github.thecsdev.tcdcommons.client.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.client.gui.screen.GameMenuScreenEvent;
import net.minecraft.client.gui.screen.GameMenuScreen;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen
{
	@Inject(method = "initWidgets", at = @At("HEAD"), cancellable = true)
	public void onInitWidgetsPre(CallbackInfo callback)
	{
		//invoke the pre-init event
		if(GameMenuScreenEvent.INIT_WIDGETS_PRE.invoker().invoke((GameMenuScreen)(Object)this).isEventCancelled())
		{
			//if it got cancelled, then cancel further execution of this method
			callback.cancel();
			return;
		}
	}
	
	@Inject(method = "initWidgets", at = @At("RETURN"))
	public void onInitWidgetsPost(CallbackInfo callback)
	{
		//invoke the post-init event
		GameMenuScreenEvent.INIT_WIDGETS_POST.invoker().invoke((GameMenuScreen)(Object)this);
	}
}
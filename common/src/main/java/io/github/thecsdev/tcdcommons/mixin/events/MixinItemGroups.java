package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.item.ItemGroupEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeModeTabs.class, priority = 9001)
public abstract class MixinItemGroups
{
	@Inject(method = "tryRebuildTabContents", at = @At("RETURN"))
	private static void onUpdateDisplayContext(
			FeatureFlagSet enabledFeatures,
			boolean operatorEnabled,
			HolderLookup.Provider lookup,
			CallbackInfoReturnable<Boolean> callback)
	{
		//do nothing if the update does not take place
		if(!callback.getReturnValueZ()) return;
		
		//invoke the event
		ItemGroupEvent.UPDATE_DISPLAY_CONTEXT.invoker().invoke(enabledFeatures, operatorEnabled, lookup);
	}
}
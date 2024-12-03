package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.thecsdev.tcdcommons.api.events.item.ItemGroupEvent;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

@Mixin(value = ItemGroups.class, priority = 9001)
public abstract class MixinItemGroups
{
	@Inject(method = "updateDisplayContext", at = @At("RETURN"))
	private static void onUpdateDisplayContext(
			FeatureSet enabledFeatures,
			boolean operatorEnabled,
			RegistryWrapper.WrapperLookup lookup,
			CallbackInfoReturnable<Boolean> callback)
	{
		//do nothing if the update does not take place
		if(!callback.getReturnValueZ()) return;
		
		//invoke the event
		ItemGroupEvent.UPDATE_DISPLAY_CONTEXT.invoker().invoke(enabledFeatures, operatorEnabled, lookup);
	}
}
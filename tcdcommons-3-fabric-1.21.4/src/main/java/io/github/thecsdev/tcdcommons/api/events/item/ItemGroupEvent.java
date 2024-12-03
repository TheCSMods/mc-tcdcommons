package io.github.thecsdev.tcdcommons.api.events.item;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

/**
 * {@link TEvent}s related to {@link ItemGroup}s.
 */
public interface ItemGroupEvent
{
	/**
	 * See {@link ItemGroupUdcCallback#invoke(FeatureSet, boolean, net.minecraft.registry.RegistryWrapper.WrapperLookup)}
	 */
	TEvent<ItemGroupUdcCallback> UPDATE_DISPLAY_CONTEXT = TEventFactory.createLoop();
	
	interface ItemGroupUdcCallback
	{
		/**
		 * Invoked when the {@link ItemGroup}'s display context is updated.
		 */
		public void invoke(FeatureSet enabledFeatures, boolean operatorEnabled, RegistryWrapper.WrapperLookup lookup);
	}
}
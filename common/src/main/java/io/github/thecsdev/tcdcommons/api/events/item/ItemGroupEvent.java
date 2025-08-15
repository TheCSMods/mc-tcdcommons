package io.github.thecsdev.tcdcommons.api.events.item;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;

/**
 * {@link TEvent}s related to {@link CreativeModeTab}s.
 */
public interface ItemGroupEvent
{
	/**
	 * See {@link ItemGroupUdcCallback#invoke(FeatureFlagSet, boolean, net.minecraft.core.HolderLookup.Provider)}
	 */
	TEvent<ItemGroupUdcCallback> UPDATE_DISPLAY_CONTEXT = TEventFactory.createLoop();
	
	interface ItemGroupUdcCallback
	{
		/**
		 * Invoked when the {@link CreativeModeTab}'s display context is updated.
		 */
		public void invoke(FeatureFlagSet enabledFeatures, boolean operatorEnabled, HolderLookup.Provider lookup);
	}
}
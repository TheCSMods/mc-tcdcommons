package io.github.thecsdev.tcdcommons.api.events.entity.player;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerEntityEvent
{
	/**
	 * See {@link ItemPickedUp#invoke(ServerPlayerEntity, ItemEntity, int)}
	 */
	TEvent<ItemPickedUp> ITEM_PICKED_UP = TEventFactory.createLoop();
	
	TEvent<LootContainerOpened> LOOT_CONTAINER_OPENED = TEventFactory.createLoop();
	
	interface ItemPickedUp
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayerEntity} picks up an {@link ItemEntity}.
		 * @param playerEntity The {@link ServerPlayerEntity}.
		 * @param itemEntity The {@link ItemEntity} being picked up.
		 * @param pickedUpCount The amount that could fit in the {@link ServerPlayerEntity}'s
		 * inventory, and was therefore picked-up.
		 */
		public void invoke(ServerPlayerEntity playerEntity, ItemEntity itemEntity, int pickedUpCount);
	}
	
	interface LootContainerOpened
	{
		/**
		 * A {@link TEvent} that is invoked whenever a {@link ServerPlayerEntity}
		 * opens a container that has a loot-table aka randomly generated loot inside of it.
		 * This {@link TEvent} will not invoke for any subsequent openings of the same container.
		 * @param player The {@link ServerPlayerEntity} that opened the container.
		 * @param container The opened container.
		 */
		public void invoke(ServerPlayerEntity player, LootableContainerBlockEntity container);
	}
}
package io.github.thecsdev.tcdcommons.api.events.entity.player;

import org.jetbrains.annotations.ApiStatus.Experimental;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerEntityEvent
{
	/**
	 * See {@link ItemPickedUp#invoke(ServerPlayerEntity, ItemEntity, int)}
	 */
	TEvent<ItemPickedUp> ITEM_PICKED_UP = TEventFactory.createLoop();

	/**
	 * See {@link LootContainerOpened#invoke(ServerPlayerEntity, LootableContainerBlockEntity)}
	 */
	TEvent<LootContainerOpened> LOOT_CONTAINER_OPENED = TEventFactory.createLoop();
	
	/**
	 * See {@link ExperienceAdded#invoke(ServerPlayerEntity, int)}
	 */
	TEvent<ExperienceAdded> EXPERIENCE_ADDED = TEventFactory.createLoop();
	
	/**
	 * See {@link BlockPlaced#invoke(ItemPlacementContext)}
	 */
	@Experimental //may change the method signature!
	TEvent<BlockPlaced> BLOCK_PLACED = TEventFactory.createLoop();
	
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
	
	interface ExperienceAdded
	{
		/**
		 * A {@link TEvent} that is invoked whenever a {@link ServerPlayerEntity} gains some experience.
		 * @param player The {@link ServerPlayerEntity}.
		 * @param experience The amount of experience they gained.
		 * @apiNote Does not get invoked when the experience is "set", or changed some other way.
		 */
		public void invoke(ServerPlayerEntity player, int experience);
	}
	
	interface BlockPlaced
	{
		/**
		 * A {@link TEvent} that is invoked whenever a {@link ServerPlayerEntity} places a block.
		 * @param context The {@link ItemPlacementContext}.
		 * @apiNote Only executes on the "server-side".
		 */
		@Experimental //may change the method signature!
		public void invoke(ItemPlacementContext context);
	}
}
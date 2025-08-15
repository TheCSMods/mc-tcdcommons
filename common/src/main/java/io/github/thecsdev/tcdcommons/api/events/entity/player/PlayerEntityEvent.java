package io.github.thecsdev.tcdcommons.api.events.entity.player;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface PlayerEntityEvent
{
	/**
	 * See {@link ItemPickedUp#invoke(ServerPlayer, ItemEntity, int)}
	 */
	TEvent<ItemPickedUp> ITEM_PICKED_UP = TEventFactory.createLoop();

	/**
	 * See {@link LootContainerOpened#invoke(ServerPlayer, RandomizableContainerBlockEntity)}
	 */
	TEvent<LootContainerOpened> LOOT_CONTAINER_OPENED = TEventFactory.createLoop();
	
	/**
	 * See {@link ExperienceAdded#invoke(ServerPlayer, int)}
	 */
	TEvent<ExperienceAdded> EXPERIENCE_ADDED = TEventFactory.createLoop();
	
	/**
	 * See {@link BlockPlaced#invoke(BlockPlaceContext)}
	 */
	@Experimental //may change the method signature!
	TEvent<BlockPlaced> BLOCK_PLACED = TEventFactory.createLoop();
	
	interface ItemPickedUp
	{
		/**
		 * A {@link TEvent} that is invoked when a {@link ServerPlayer} picks up an {@link ItemEntity}.
		 * @param playerEntity The {@link ServerPlayer}.
		 * @param itemEntity The {@link ItemEntity} being picked up.
		 * @param pickedUpCount The amount that could fit in the {@link ServerPlayer}'s
		 * inventory, and was therefore picked-up.
		 */
		public void invoke(ServerPlayer playerEntity, ItemEntity itemEntity, int pickedUpCount);
	}
	
	interface LootContainerOpened
	{
		/**
		 * A {@link TEvent} that is invoked whenever a {@link ServerPlayer}
		 * opens a container that has a loot-table aka randomly generated loot inside of it.
		 * This {@link TEvent} will not invoke for any subsequent openings of the same container.
		 * @param player The {@link ServerPlayer} that opened the container.
		 * @param container The opened container.
		 */
		public void invoke(ServerPlayer player, RandomizableContainerBlockEntity container);
	}
	
	interface ExperienceAdded
	{
		/**
		 * A {@link TEvent} that is invoked whenever a {@link ServerPlayer} gains some experience.
		 * @param player The {@link ServerPlayer}.
		 * @param experience The amount of experience they gained.
		 * @apiNote Does not get invoked when the experience is "set", or changed some other way.
		 */
		public void invoke(ServerPlayer player, int experience);
	}
	
	interface BlockPlaced
	{
		/**
		 * A {@link TEvent} that is invoked whenever a {@link ServerPlayer} places a block.
		 * @param context The {@link BlockPlaceContext}.
		 * @apiNote Only executes on the "server-side".
		 */
		@Experimental //may change the method signature!
		public void invoke(BlockPlaceContext context);
	}
}
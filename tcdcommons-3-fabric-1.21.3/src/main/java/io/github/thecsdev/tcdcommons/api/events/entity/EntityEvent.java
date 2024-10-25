package io.github.thecsdev.tcdcommons.api.events.entity;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public interface EntityEvent
{
	/**
	 * Invoked when a {@link ServerPlayerEntity}'s NBT data is being read from an {@link NbtCompound}.
	 * @see ServerPlayerEntityNBTCallback#invoke(ServerPlayerEntity, NbtCompound)
	 */
	TEvent<ServerPlayerEntityNBTCallback> SERVER_PLAYER_READ_NBT = TEventFactory.createLoop();
	
	/**
	 * Invoked when a {@link ServerPlayerEntity}'s NBT data is being written to an {@link NbtCompound}.
	 * @see ServerPlayerEntityNBTCallback#invoke(ServerPlayerEntity, NbtCompound)
	 */
	TEvent<ServerPlayerEntityNBTCallback> SERVER_PLAYER_WRITE_NBT = TEventFactory.createLoop();
	
	interface ServerPlayerEntityNBTCallback
	{
		/**
		 * An event that is invoked when a {@link ServerPlayerEntity}'s
		 * NBT data is being read or written (aka saved or loaded).
		 * @param player The {@link ServerPlayerEntity} in question.
		 * @param nbt The {@link ServerPlayerEntity}'s NBT data ({@link NbtCompound}).
		 */
		void invoke(ServerPlayerEntity player, NbtCompound nbt);
	}
}
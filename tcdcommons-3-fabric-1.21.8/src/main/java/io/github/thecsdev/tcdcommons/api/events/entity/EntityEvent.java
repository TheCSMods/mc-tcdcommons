package io.github.thecsdev.tcdcommons.api.events.entity;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public interface EntityEvent
{
	/**
	 * Invoked when a {@link ServerPlayerEntity}'s NBT data is being read from an {@link NbtCompound}.
	 * @see ServerPlayerCustomDataReadCallback#invoke(ServerPlayerEntity, ReadView)
	 */
	TEvent<ServerPlayerCustomDataReadCallback> SERVER_PLAYER_READ_NBT = TEventFactory.createLoop();
	
	/**
	 * Invoked when a {@link ServerPlayerEntity}'s NBT data is being written to an {@link NbtCompound}.
	 * @see ServerPlayerCustomDataWriteCallback#invoke(ServerPlayerEntity, WriteView)
	 */
	TEvent<ServerPlayerCustomDataWriteCallback> SERVER_PLAYER_WRITE_NBT = TEventFactory.createLoop();
	
	interface ServerPlayerCustomDataReadCallback { void invoke(ServerPlayerEntity player, ReadView view); }
	interface ServerPlayerCustomDataWriteCallback { void invoke(ServerPlayerEntity player, WriteView view); }
}
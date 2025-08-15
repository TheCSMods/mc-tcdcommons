package io.github.thecsdev.tcdcommons.api.events.entity;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface EntityEvent
{
	/**
	 * Invoked when a {@link ServerPlayer}'s NBT data is being read from an {@link CompoundTag}.
	 * @see ServerPlayerCustomDataReadCallback#invoke(ServerPlayer, ValueInput)
	 */
	TEvent<ServerPlayerCustomDataReadCallback> SERVER_PLAYER_READ_NBT = TEventFactory.createLoop();
	
	/**
	 * Invoked when a {@link ServerPlayer}'s NBT data is being written to an {@link CompoundTag}.
	 * @see ServerPlayerCustomDataWriteCallback#invoke(ServerPlayer, ValueOutput)
	 */
	TEvent<ServerPlayerCustomDataWriteCallback> SERVER_PLAYER_WRITE_NBT = TEventFactory.createLoop();
	
	interface ServerPlayerCustomDataReadCallback { void invoke(ServerPlayer player, ValueInput view); }
	interface ServerPlayerCustomDataWriteCallback { void invoke(ServerPlayer player, ValueOutput view); }
}
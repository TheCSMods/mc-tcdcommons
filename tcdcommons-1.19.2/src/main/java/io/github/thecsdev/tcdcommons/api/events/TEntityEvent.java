package io.github.thecsdev.tcdcommons.api.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Some extra {@link Event}s related to {@link Entity}-ies.
 * @see EntityEvent
 */
public interface TEntityEvent
{
	// ==================================================
	/*
	 * Invoked when an {@link Entity}'s NBT data is being read from an {@link NbtCompound}.<br/>
	 * See {@link EntityNBTCallback#entityWriteNBTCallback(Entity, NbtCompound)}
	 *
	//performance concern - a mod could perform `casting` and `instanceof`, which is too expensive
	@Deprecated(forRemoval = true)
	Event<EntityNBTCallback> READ_NBT = EventFactory.createLoop();
	
	/*
	 * Invoked when an {@link Entity}'s NBT data is being written to an {@link NbtCompound}.<br/>
	 * See {@link EntityNBTCallback#entityWriteNBTCallback(Entity, NbtCompound)}
	 *
	//performance concern - a mod could perform `casting` and `instanceof`, which is too expensive
	@Deprecated(forRemoval = true)
	Event<EntityNBTCallback> WRITE_NBT = EventFactory.createLoop();*/
	// --------------------------------------------------
	/**
	 * Invoked when a {@link ServerPlayerEntity}'s NBT data is being read from an {@link NbtCompound}.
	 * @see ServerPlayerEntityNBTCallback#serverPlayerEntityNBTCallback(ServerPlayerEntity, NbtCompound)
	 */
	Event<ServerPlayerEntityNBTCallback> SERVER_PLAYER_READ_NBT = EventFactory.createLoop();
	
	/**
	 * Invoked when a {@link ServerPlayerEntity}'s NBT data is being written to an {@link NbtCompound}.
	 * @see ServerPlayerEntityNBTCallback#serverPlayerEntityNBTCallback(ServerPlayerEntity, NbtCompound)
	 */
	Event<ServerPlayerEntityNBTCallback> SERVER_PLAYER_WRITE_NBT = EventFactory.createLoop();
	// ==================================================
	/*@Deprecated(forRemoval = true)
	interface EntityNBTCallback
	{
		/*
		 * An event that is invoked when an {@link Entity}'s
		 * NBT data is being read or written (aka saved or loaded).
		 * @param entity The {@link Entity} in question.
		 * @param nbt The {@link Entity}'s NBT data ({@link NbtCompound}).
		 *
		void entityNBTCallback(Entity entity, NbtCompound nbt);
	}*/
	// --------------------------------------------------
	interface ServerPlayerEntityNBTCallback
	{
		/**
		 * An event that is invoked when a {@link ServerPlayerEntity}'s
		 * NBT data is being read or written (aka saved or loaded).
		 * @param player The {@link ServerPlayerEntity} in question.
		 * @param nbt The {@link ServerPlayerEntity}'s NBT data ({@link NbtCompound}).
		 */
		void serverPlayerEntityNBTCallback(ServerPlayerEntity player, NbtCompound nbt);
	}
	// ==================================================
}
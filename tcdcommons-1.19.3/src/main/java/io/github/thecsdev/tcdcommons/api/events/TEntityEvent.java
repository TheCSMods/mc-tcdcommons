package io.github.thecsdev.tcdcommons.api.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

/**
 * Some extra {@link Event}s related to {@link Entity}-ies.
 * @see EntityEvent
 */
public interface TEntityEvent
{
	// ==================================================
	/**
	 * Invoked when an {@link Entity}'s NBT data is being read from an {@link NbtCompound}.<br/>
	 * See {@link EntityNBTCallback#entityWriteNBTCallback(Entity, NbtCompound)}
	 */
	Event<EntityNBTCallback> READ_NBT = EventFactory.createLoop();
	
	/**
	 * Invoked when an {@link Entity}'s NBT data is being written to an {@link NbtCompound}.<br/>
	 * See {@link EntityNBTCallback#entityWriteNBTCallback(Entity, NbtCompound)}
	 */
	Event<EntityNBTCallback> WRITE_NBT = EventFactory.createLoop();
	// ==================================================
	interface EntityNBTCallback
	{
		/**
		 * An event that is invoked when an {@link Entity}'s
		 * NBT data is being read or written (aka saved or loaded).
		 * @param entity The {@link Entity} in question.
		 * @param nbt The {@link Entity}'s NBT data ({@link NbtCompound}).
		 */
		void entityNBTCallback(Entity entity, NbtCompound nbt);
	}
	// ==================================================
}
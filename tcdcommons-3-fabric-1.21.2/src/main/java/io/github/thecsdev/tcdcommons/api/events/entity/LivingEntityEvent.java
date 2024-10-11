package io.github.thecsdev.tcdcommons.api.events.entity;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public interface LivingEntityEvent
{
	/**
	 * See {@link StatusEffectAdded#invoke(LivingEntity, StatusEffectInstance, Entity)}
	 */
	TEvent<StatusEffectAdded> STATUS_EFFECT_ADDED = TEventFactory.createLoop();
	
	interface StatusEffectAdded
	{
		/**
		 * A {@link TEvent} that is invoked after a {@link StatusEffectInstance}
		 * has been added to a {@link LivingEntity}.
		 * @param entity The {@link LivingEntity} that received the effect.
		 * @param effect The {@link StatusEffectInstance}.
		 * @param source An optional {@link Entity} that was the cause of the effect.
		 */
		public void invoke(LivingEntity entity, StatusEffectInstance effect, @Nullable Entity source);
	}
}
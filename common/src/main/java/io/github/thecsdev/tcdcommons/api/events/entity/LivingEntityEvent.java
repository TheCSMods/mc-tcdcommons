package io.github.thecsdev.tcdcommons.api.events.entity;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface LivingEntityEvent
{
	/**
	 * See {@link StatusEffectAdded#invoke(LivingEntity, MobEffectInstance, Entity)}
	 */
	TEvent<StatusEffectAdded> STATUS_EFFECT_ADDED = TEventFactory.createLoop();
	
	interface StatusEffectAdded
	{
		/**
		 * A {@link TEvent} that is invoked after a {@link MobEffectInstance}
		 * has been added to a {@link LivingEntity}.
		 * @param entity The {@link LivingEntity} that received the effect.
		 * @param effect The {@link MobEffectInstance}.
		 * @param source An optional {@link Entity} that was the cause of the effect.
		 */
		public void invoke(LivingEntity entity, MobEffectInstance effect, @Nullable Entity source);
	}
}
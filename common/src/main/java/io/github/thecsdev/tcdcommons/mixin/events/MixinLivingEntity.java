package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.entity.LivingEntityEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 2000) //higher priority so other mods can cancel
public abstract class MixinLivingEntity
{
	@Inject(
			method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
			at = @At("RETURN"))
	public void onAddStatusEffect(
			MobEffectInstance effect,
			@Nullable Entity source,
			CallbackInfoReturnable<Boolean> ci)
	{
		//if the effect didn't get applied, return
		if(!ci.getReturnValueZ()) return;
		
		//invoke the event
		LivingEntityEvent.STATUS_EFFECT_ADDED.invoker().invoke((LivingEntity)(Object)this, effect, source);
	}
}
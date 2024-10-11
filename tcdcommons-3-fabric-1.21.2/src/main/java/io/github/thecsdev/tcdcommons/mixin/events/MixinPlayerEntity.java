package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity
{
	public @Shadow int experienceLevel;
	public @Shadow int totalExperience;
	public @Shadow float experienceProgress;
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }
	
	@Inject(method = "addExperience", at = @At("RETURN"))
	public void onAddExperience(int experience, CallbackInfo ci)
	{
		//make sure this is a server-side player entity
		if(!(((Object)this) instanceof ServerPlayerEntity))
			return;
		
		//invoke the event
		PlayerEntityEvent.EXPERIENCE_ADDED.invoker().invoke((ServerPlayerEntity)(Object)this, experience);
	}
}
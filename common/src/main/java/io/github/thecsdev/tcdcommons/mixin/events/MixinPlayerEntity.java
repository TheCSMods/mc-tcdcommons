package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity
{
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, Level world) { super(entityType, world); }
	
	@Inject(method = "giveExperiencePoints", at = @At("RETURN"))
	public void onAddExperience(int experience, CallbackInfo ci)
	{
		//make sure this is a server-side player entity
		if(!(((Object)this) instanceof ServerPlayer))
			return;
		
		//invoke the event
		PlayerEntityEvent.EXPERIENCE_ADDED.invoker().invoke((ServerPlayer)(Object)this, experience);
	}
}
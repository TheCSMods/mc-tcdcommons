package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ItemEntity.class)
public class MixinItemEntity
{
	private @Unique int opc_itemCount;
	@ModifyVariable(
			method = "onPlayerCollision",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/entity/player/PlayerEntity;triggerItemPickedUpByEntityCriteria(Lnet/minecraft/entity/ItemEntity;)V",
				shift = At.Shift.BEFORE //Important: Has to be done before the onPlayerCollection injection is invoked
			),
			ordinal = 0
		 )
		 private int captureItemCount(int i) {
			opc_itemCount = i;
			return i;
		}

	@Inject(
			method = "onPlayerCollision",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;triggerItemPickedUpByEntityCriteria(Lnet/minecraft/entity/ItemEntity;)V",
					shift = Shift.AFTER //Important: Has to be done after the modify-variable injection is invoked
				),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void onPlayerCollision(PlayerEntity player, CallbackInfo ci)
	{
		//requirements
		if(!(player instanceof ServerPlayerEntity))
			return;
		
		//invoke the event
		PlayerEntityEvent.ITEM_PICKED_UP.invoker().invoke((ServerPlayerEntity)player, (ItemEntity)(Object)this, opc_itemCount);
	}
}
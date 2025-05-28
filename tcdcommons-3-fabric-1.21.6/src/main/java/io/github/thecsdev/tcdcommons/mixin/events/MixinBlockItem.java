package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

@Mixin(value = BlockItem.class, priority = 18001) //higher priority to allow other mods to handle/cancel it first
public abstract class MixinBlockItem
{
	@SuppressWarnings("resource")
	@Inject(
			method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			at = @At("RETURN"))
	public void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> ci)
	{
		//ignore if not placed or not proper environment
		final var r = ci.getReturnValue();
		if(r == null || !r.isAccepted() ||
				context == null || context.getWorld().isClient || !(context.getPlayer() instanceof ServerPlayerEntity))
			return;
		
		//handle if placed
		PlayerEntityEvent.BLOCK_PLACED.invoker().invoke(context);
	}
}
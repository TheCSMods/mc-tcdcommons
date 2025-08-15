package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockItem.class, priority = 18001) //higher priority to allow other mods to handle/cancel it first
public abstract class MixinBlockItem
{
	@SuppressWarnings("resource")
	@Inject(
			method = "place",
			at = @At("RETURN"))
	public void onPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> ci)
	{
		//ignore if not placed or not proper environment
		final var r = ci.getReturnValue();
		if(r == null || !r.consumesAction() ||
				context == null || context.getLevel().isClientSide || !(context.getPlayer() instanceof ServerPlayer))
			return;
		
		//handle if placed
		PlayerEntityEvent.BLOCK_PLACED.invoker().invoke(context);
	}
}
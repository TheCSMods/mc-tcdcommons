package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainer.class)
public interface MixinLootableContainerBlockEntity
{
	@Inject(
			method = "unpackLootTable",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V",
				shift = At.Shift.AFTER
			)
		)
	private void afterCheckLootInteraction(@Nullable Player player, CallbackInfo ci)
	{
		//requirements
		if(!(player instanceof ServerPlayer))
			return;
		
		//invoke the event
		PlayerEntityEvent.LOOT_CONTAINER_OPENED.invoker()
			.invoke((ServerPlayer)player, (RandomizableContainerBlockEntity)(Object)this);
	}
}
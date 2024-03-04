package io.github.thecsdev.tcdcommons.mixin.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.entity.player.PlayerEntityEvent;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(LootableInventory.class)
public interface MixinLootableContainerBlockEntity
{
	@Inject(
			method = "generateLoot(Lnet/minecraft/entity/player/PlayerEntity;)V",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/loot/LootTable;supplyInventory("
						+ "Lnet/minecraft/inventory/Inventory;"
						+ "Lnet/minecraft/loot/context/LootContextParameterSet;"
						+ "J)V",
				shift = At.Shift.AFTER
			)
		)
	private void afterCheckLootInteraction(@Nullable PlayerEntity player, CallbackInfo ci)
	{
		//requirements
		if(!(player instanceof ServerPlayerEntity))
			return;
		
		//invoke the event
		PlayerEntityEvent.LOOT_CONTAINER_OPENED.invoker()
			.invoke((ServerPlayerEntity)player, (LootableContainerBlockEntity)(Object)this);
	}
}
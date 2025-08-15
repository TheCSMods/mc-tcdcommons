package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(EntityRenderDispatcher.class)
public interface AccessorEntityRenderDispatcher
{
	public @Accessor("entityModels") Supplier<EntityModelSet> getEntityModelsGetter();
	public @Accessor("equipmentAssets") EquipmentAssetManager getEquipmentModelLoader();
	public @Accessor("renderers") Map<EntityType<?>, EntityRenderer<?, ?>> getRenderers();
	public @Accessor("playerRenderers") Map<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> getModelRenderers();
}
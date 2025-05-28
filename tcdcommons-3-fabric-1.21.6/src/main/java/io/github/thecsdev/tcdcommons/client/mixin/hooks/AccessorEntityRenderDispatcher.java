package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(EntityRenderDispatcher.class)
public interface AccessorEntityRenderDispatcher
{
	public @Accessor("entityModelsGetter") Supplier<LoadedEntityModels> getEntityModelsGetter();
	public @Accessor("equipmentModelLoader") EquipmentModelLoader getEquipmentModelLoader();
	public @Accessor("renderers") Map<EntityType<?>, EntityRenderer<?, ?>> getRenderers();
	public @Accessor("modelRenderers") Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> getModelRenderers();
}
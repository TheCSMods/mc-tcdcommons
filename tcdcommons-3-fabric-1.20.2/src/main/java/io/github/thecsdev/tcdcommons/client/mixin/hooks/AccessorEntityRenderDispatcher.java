package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(EntityRenderDispatcher.class)
public interface AccessorEntityRenderDispatcher
{
	public @Accessor("modelLoader") EntityModelLoader getModelLoader();
	public @Accessor("renderers") Map<EntityType<?>, EntityRenderer<?>> getRenderers();
	public @Accessor("modelRenderers") Map<EntityType<?>, EntityRenderer<? extends PlayerEntity>> getModelRenderers();
}
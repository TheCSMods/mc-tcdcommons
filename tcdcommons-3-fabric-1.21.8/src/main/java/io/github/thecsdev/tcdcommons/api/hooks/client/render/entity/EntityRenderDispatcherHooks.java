package io.github.thecsdev.tcdcommons.api.hooks.client.render.entity;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public final class EntityRenderDispatcherHooks
{
	private EntityRenderDispatcherHooks() {}
	
	/**
	 * Returns an {@link EntityRenderer} for a given {@link EntityType}.
	 * @param erd The target {@link EntityRenderDispatcher}
	 * @param entityType Any {@link EntityType} that isn't related to {@link PlayerEntity}.
	 * @see #getPlayerRenderer(EntityRenderDispatcher, PlayerEntity)
	 * @apiNote <b>Does not work for {@link PlayerEntity} types.</b>
	 */
	public static @Nullable EntityRenderer<?, ?> getEntityRenderer(EntityRenderDispatcher erd, EntityType<?> entityType)
	{
		return ((AccessorEntityRenderDispatcher)erd).getRenderers().get(entityType);
	}
	
	/**
	 * Returns an {@link EntityRenderer} for a given {@link PlayerEntity}.
	 * @param erd The target {@link EntityRenderDispatcher}
	 */
	public static <T extends PlayerEntity> EntityRenderer<? super T, ?> getPlayerRenderer(EntityRenderDispatcher erd, T playerEntity)
	{
		return erd.getRenderer(playerEntity);
	}
}
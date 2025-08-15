package io.github.thecsdev.tcdcommons.api.hooks.client.render.entity;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class EntityRenderDispatcherHooks
{
	private EntityRenderDispatcherHooks() {}
	
	/**
	 * Returns an {@link EntityRenderer} for a given {@link EntityType}.
	 * @param erd The target {@link EntityRenderDispatcher}
	 * @param entityType Any {@link EntityType} that isn't related to {@link Player}.
	 * @see #getPlayerRenderer(EntityRenderDispatcher, Player)
	 * @apiNote <b>Does not work for {@link Player} types.</b>
	 */
	public static @Nullable EntityRenderer<?, ?> getEntityRenderer(EntityRenderDispatcher erd, EntityType<?> entityType)
	{
		return ((AccessorEntityRenderDispatcher)erd).getRenderers().get(entityType);
	}
	
	/**
	 * Returns an {@link EntityRenderer} for a given {@link Player}.
	 * @param erd The target {@link EntityRenderDispatcher}
	 */
	public static <T extends Player> EntityRenderer<? super T, ?> getPlayerRenderer(EntityRenderDispatcher erd, T playerEntity)
	{
		return erd.getRenderer(playerEntity);
	}
}
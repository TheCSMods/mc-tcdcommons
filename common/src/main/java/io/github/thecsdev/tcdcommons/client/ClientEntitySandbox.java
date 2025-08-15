package io.github.thecsdev.tcdcommons.client;

import com.google.common.annotations.Beta;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TEntityRendererElement;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

/**
 * Used by {@link TEntityRendererElement} to render {@link Entity}s.
 */
@Beta
public final @Internal class ClientEntitySandbox
{
	// ==================================================
	private ClientEntitySandbox() {}
	// ==================================================
	/*private static final @Nullable Level SANDBOX_WORLD = new ClientSandboxWorld();
	private static final Cache<EntityType<?>, Entity> ENTITY_CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.HOURS)
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.removalListener((RemovalNotification<EntityType<?>, Entity> remNotif) ->
			{
				final Entity entity = remNotif.getValue();
				if(entity != null)
					try { entity.discard(); } catch(Exception e) {}
			})
			.build();
	static { TaskScheduler.schedulePeriodicCacheCleanup(ENTITY_CACHE); }*/
	// ==================================================
	public static @Nullable Entity getCachedEntityFromType(EntityType<?> entityType)
	{
		if(entityType == EntityType.PLAYER)
			return Minecraft.getInstance().player;
		else return null;
		//check arguments
		/*if(entityType == EntityType.PLAYER) return MC_CLIENT.player;
		else if(entityType == null || !entityType.canSummon())
			return null;
		
		//check if an entry already exists
		final Entity existing = ENTITY_CACHE.getIfPresent(entityType);
		if(existing != null) return existing;
		
		//create a new entity and put it in the cache
		Entity newEntity = null;
		try { newEntity = entityType.create(SANDBOX_WORLD, EntitySpawnReason.MOB_SUMMONED); }
		catch(Exception e)
		{
			//some entities might not behave as expected, and
			//may throw exceptions upon their creation. deal with this
			newEntity = EntityType.MARKER.create(SANDBOX_WORLD, EntitySpawnReason.MOB_SUMMONED);
		}
		try { newEntity.discard(); }
		catch(Exception e) {} //<- again, deal with unexpected behavior from modded entities
		
		//assign cache and return the entity
		if(newEntity != null) ENTITY_CACHE.put(entityType, newEntity);
		return newEntity;*/
	}
	// ==================================================
}
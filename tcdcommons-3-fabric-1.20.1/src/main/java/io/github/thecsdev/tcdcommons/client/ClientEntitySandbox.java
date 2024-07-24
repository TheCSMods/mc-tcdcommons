package io.github.thecsdev.tcdcommons.client;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TEntityRendererElement;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import io.github.thecsdev.tcdcommons.client.world.ClientSandboxWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Used by {@link TEntityRendererElement} to render {@link Entity}s.
 */
@Beta
public final @Internal class ClientEntitySandbox
{
	// ==================================================
	private ClientEntitySandbox() {}
	// ==================================================
	private static final @Nullable World SANDBOX_WORLD = new ClientSandboxWorld();
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
	static { TaskScheduler.schedulePeriodicCacheCleanup(ENTITY_CACHE); }
	// ==================================================
	public static @Nullable Entity getCachedEntityFromType(EntityType<?> entityType)
	{
		//check arguments
		if(entityType == EntityType.PLAYER) return MC_CLIENT.player;
		else if(entityType == null || !entityType.isSummonable())
			return null;
		
		//check if an entry already exists
		final Entity existing = ENTITY_CACHE.getIfPresent(entityType);
		if(existing != null) return existing;
		
		//create a new entity and put it in the cache
		Entity newEntity = null;
		try { newEntity = entityType.create(SANDBOX_WORLD); }
		catch(Exception e)
		{
			//some entities might not behave as expected, and
			//may throw exceptions upon their creation. deal with this
			newEntity = EntityType.MARKER.create(SANDBOX_WORLD);
		}
		try { newEntity.discard(); }
		catch(Exception e) { /*again, deal with unexpected behavior from modded entities*/ }
		
		//assign cache and return the entity
		if(newEntity != null) ENTITY_CACHE.put(entityType, newEntity);
		return newEntity;
	}
	// ==================================================
}
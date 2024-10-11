package io.github.thecsdev.tcdcommons.api.registry;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceSerializer;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider;

/**
 * {@link TCDCommons}'s registries.
 */
@SuppressWarnings("removal")
public final class TRegistries
{
	private TRegistries() {}
	
	/**
	 * Contains {@link PlayerBadge}s that were registered for this session.
	 * @apiNote Don't forget to register their corresponding renderers.
	 * @apiNote To self: Has to be a {@link TSimpleRegistry}, otherwise {@link PlayerBadge#getId()} would break.
	 */
	public static final TSimpleRegistry<PlayerBadge> PLAYER_BADGE = new TSimpleRegistry<>();
	
	/**
	 * Contains {@link RepositoryInfoProvider}s that were registered for this session.
	 */
	public static final TMutableRegistry<RepositoryInfoProvider> REPO_INFO_PROVIDER = new TMutableRegistry<>();
	
	/**
	 * Contains {@link CachedResourceSerializer}s that tell the {@link CachedResourceManager}
	 * how to serialize and deserialize {@link CachedResource}s.
	 */
	public static final TSimpleRegistry<CachedResourceSerializer<?>> CACHED_RESOURCE_SERIALIZER = new TSimpleRegistry<>();
}
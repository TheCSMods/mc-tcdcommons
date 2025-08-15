package io.github.thecsdev.tcdcommons.api.hooks.world.biome.source;

import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorBiomeAccess;
import net.minecraft.world.level.biome.BiomeManager;

public final class BiomeAccessHooks
{
	private BiomeAccessHooks() {}
	
	/**
	 * Returns the SHA256 seed value that corresponds to a given {@link BiomeManager}.
	 * @param biomeAccess The given {@link BiomeManager}.
	 */
	public static long getBiomeAccessSeed(BiomeManager biomeAccess) { return ((AccessorBiomeAccess)biomeAccess).getSeed(); }
}
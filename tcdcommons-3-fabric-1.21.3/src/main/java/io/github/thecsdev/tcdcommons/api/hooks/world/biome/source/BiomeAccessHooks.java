package io.github.thecsdev.tcdcommons.api.hooks.world.biome.source;

import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorBiomeAccess;
import net.minecraft.world.biome.source.BiomeAccess;

public final class BiomeAccessHooks
{
	private BiomeAccessHooks() {}
	
	/**
	 * Returns the SHA256 seed value that corresponds to a given {@link BiomeAccess}.
	 * @param biomeAccess The given {@link BiomeAccess}.
	 */
	public static long getBiomeAccessSeed(BiomeAccess biomeAccess) { return ((AccessorBiomeAccess)biomeAccess).getSeed(); }
}
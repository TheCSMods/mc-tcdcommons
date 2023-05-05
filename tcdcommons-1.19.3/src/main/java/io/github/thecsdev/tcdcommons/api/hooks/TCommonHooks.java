package io.github.thecsdev.tcdcommons.api.hooks;

import io.github.thecsdev.tcdcommons.mixin.hooks.MixinBiomeAccess;
import net.minecraft.world.biome.source.BiomeAccess;

public final class TCommonHooks
{
	// ==================================================
	protected TCommonHooks() {}
	// ==================================================
	/**
	 * Returns the SHA256 seed value that corresponds to a given {@link BiomeAccess}.
	 * @param biomeAccess The given {@link BiomeAccess}.
	 */
	public static long getBiomeAccessSeed(BiomeAccess biomeAccess) { return ((MixinBiomeAccess)biomeAccess).getSeed(); }
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.hooks;

import io.github.thecsdev.tcdcommons.mixin.hooks.MixinBiomeAccess;
import io.github.thecsdev.tcdcommons.mixin.hooks.MixinStatHandler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
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
	
	/**
	 * Returns the stat map for a given {@link StatHandler}.
	 */
	public static Object2IntMap<Stat<?>> getStatHandlerStatMap(StatHandler statHandler) { return ((MixinStatHandler)statHandler).getStatMap(); }
	// ==================================================
}
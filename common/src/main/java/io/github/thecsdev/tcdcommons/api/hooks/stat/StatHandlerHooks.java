package io.github.thecsdev.tcdcommons.api.hooks.stat;

import io.github.thecsdev.tcdcommons.mixin.hooks.AccessorStatHandler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;

public final class StatHandlerHooks
{
	private StatHandlerHooks() {}
	
	/**
	 * Returns the stat map for a given {@link StatsCounter}.
	 */
	public static Object2IntMap<Stat<?>> getStatHandlerStatMap(StatsCounter statHandler) { return ((AccessorStatHandler)statHandler).getStatMap(); }
}
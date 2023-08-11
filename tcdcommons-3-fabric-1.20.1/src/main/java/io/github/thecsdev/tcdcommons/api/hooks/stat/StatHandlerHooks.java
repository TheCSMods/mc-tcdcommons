package io.github.thecsdev.tcdcommons.api.hooks.stat;

import io.github.thecsdev.tcdcommons.mixin.hooks.MixinStatHandler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;

public final class StatHandlerHooks
{
	private StatHandlerHooks() {}
	
	/**
	 * Returns the stat map for a given {@link StatHandler}.
	 */
	public static Object2IntMap<Stat<?>> getStatHandlerStatMap(StatHandler statHandler) { return ((MixinStatHandler)statHandler).getStatMap(); }
}
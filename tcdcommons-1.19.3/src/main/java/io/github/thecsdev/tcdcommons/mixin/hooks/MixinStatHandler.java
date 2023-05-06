package io.github.thecsdev.tcdcommons.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;

@Mixin(StatHandler.class)
public interface MixinStatHandler
{
	@Accessor("statMap")
	public abstract Object2IntMap<Stat<?>> getStatMap();
}
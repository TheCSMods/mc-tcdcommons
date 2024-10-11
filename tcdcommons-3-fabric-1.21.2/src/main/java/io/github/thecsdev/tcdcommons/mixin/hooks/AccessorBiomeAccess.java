package io.github.thecsdev.tcdcommons.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(BiomeAccess.class)
public interface AccessorBiomeAccess
{
	@Accessor("seed")
	public abstract long getSeed();
}
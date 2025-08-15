package io.github.thecsdev.tcdcommons.mixin.hooks;

import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeManager.class)
public interface AccessorBiomeAccess
{
	@Accessor("biomeZoomSeed")
	public abstract long getSeed();
}
package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.texture.NativeImage;

@Mixin(NativeImage.class)
public interface AccessorNativeImage
{
	public @Invoker("getColor") int tcdcommons_getColor(int x, int y);
	public @Invoker("setColor") void tcdcommons_setColor(int x, int y, int color);
}
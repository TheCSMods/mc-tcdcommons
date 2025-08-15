package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NativeImage.class)
public interface AccessorNativeImage
{
	public @Invoker("getPixel") int tcdcommons_getColor(int x, int y);
	public @Invoker("setPixel") void tcdcommons_setColor(int x, int y, int color);
	//TODO - ^ Come back to this if colors look off
}
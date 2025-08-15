package io.github.thecsdev.tcdcommons.client.mixin.events;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

/**
 * This {@link Mixin}'s sole purpose is to put image byte data loading up to
 * LWJGL's digression, rather than having the game enforce PNG. And on another note,
 * the game even fails to validate PNG properly most the time anyways, so this
 * is kind of more of a bug fix in a way.
 */
@Mixin(value = NativeImage.class)
public class MixinNativeImage
{
	@Redirect(
		method = "read(Lcom/mojang/blaze3d/platform/NativeImage$Format;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/platform/NativeImage;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/PngInfo;validateHeader(Ljava/nio/ByteBuffer;)V"
		),
		require = 0
	)
	private static void skipPngValidation(ByteBuffer buffer) {}
}
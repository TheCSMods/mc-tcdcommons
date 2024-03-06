package io.github.thecsdev.tcdcommons.client.mixin.events;

import java.nio.ByteBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.texture.NativeImage;

/**
 * This {@link Mixin}'s sole purpose is to put image byte data loading up to
 * LWJGL's digression, rather than having the game enforce PNG. And on another note,
 * the game even fails to validate PNG properly most the time anyways, so this
 * is kind of more of a bug fix in a way.
 */
@Mixin(value = NativeImage.class, priority = 9001)
public class MixinNativeImage
{
	@Redirect(
		method = "read(Lnet/minecraft/client/texture/NativeImage$Format;Ljava/nio/ByteBuffer;)Lnet/minecraft/client/texture/NativeImage;", 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/util/PngMetadata;validate(Ljava/nio/ByteBuffer;)V"
		),
		require = 0
	)
	private static void skipPngValidation(ByteBuffer buffer) {}
}
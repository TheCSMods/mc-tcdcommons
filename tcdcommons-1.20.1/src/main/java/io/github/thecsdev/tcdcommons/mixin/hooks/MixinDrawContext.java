package io.github.thecsdev.tcdcommons.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(DrawContext.class)
public interface MixinDrawContext
{
	@Accessor("client")
	public MinecraftClient getClient();
	
	@Accessor("matrices")
	public MatrixStack getMatrices();
	
	@Accessor("vertexConsumers")
	public VertexConsumerProvider.Immediate getVertexConsumers();
}
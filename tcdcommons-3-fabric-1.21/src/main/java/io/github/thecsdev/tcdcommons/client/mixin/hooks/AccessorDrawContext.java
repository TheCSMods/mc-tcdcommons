package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(DrawContext.class)
public interface AccessorDrawContext
{
	public @Accessor("client") MinecraftClient getClient();
	public @Mutable @Accessor("client") void setClient(MinecraftClient client);
	
	public @Accessor("matrices") MatrixStack getMatrices();
	public @Mutable @Accessor("matrices") void setMatrices(MatrixStack matrices);
	
	public @Accessor("vertexConsumers") VertexConsumerProvider.Immediate getVertexConsumers();
	public @Mutable @Accessor("vertexConsumers") void setVertexConsumers(VertexConsumerProvider.Immediate vertexConsumers);
}
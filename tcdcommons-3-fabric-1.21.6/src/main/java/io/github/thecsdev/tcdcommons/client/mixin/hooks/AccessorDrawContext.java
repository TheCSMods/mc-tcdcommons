package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;

@Mixin(DrawContext.class)
public interface AccessorDrawContext
{
	public @Accessor("client") MinecraftClient getClient();
	public @Mutable @Accessor("client") void setClient(MinecraftClient client);
	
	public @Accessor("matrices") Matrix3x2fStack getMatrices();
	public @Mutable @Accessor("matrices") void setMatrices(Matrix3x2fStack matrices);
	
	public @Accessor("state") GuiRenderState getState();
	public @Mutable @Accessor("state") void setState(GuiRenderState state);
}
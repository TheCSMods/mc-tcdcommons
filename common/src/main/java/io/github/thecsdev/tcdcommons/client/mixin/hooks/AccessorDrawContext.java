package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface AccessorDrawContext
{
	public @Accessor("minecraft") Minecraft getClient();
	public @Mutable @Accessor("minecraft") void setClient(Minecraft client);
	
	public @Accessor("pose") Matrix3x2fStack getMatrices();
	public @Mutable @Accessor("pose") void setMatrices(Matrix3x2fStack matrices);
	
	public @Accessor("guiRenderState") GuiRenderState getState();
	public @Mutable @Accessor("guiRenderState") void setState(GuiRenderState state);
}
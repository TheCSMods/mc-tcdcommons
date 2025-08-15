package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GridLayout.class)
public interface AccessorGridWidget
{
	public abstract @Accessor("children") List<LayoutElement> getChildren();
	public abstract @Mutable @Accessor("children") void setChildren(List<LayoutElement> children);
}
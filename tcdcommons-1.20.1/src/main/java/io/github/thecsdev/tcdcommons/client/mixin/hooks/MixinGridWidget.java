package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;

@Mixin(GridWidget.class)
public interface MixinGridWidget
{
	@Mutable
	@Accessor("children")
	public abstract List<Widget> getChildren();
	
	@Mutable
	@Accessor("children")
	public abstract void setChildren(List<Widget> children);
}
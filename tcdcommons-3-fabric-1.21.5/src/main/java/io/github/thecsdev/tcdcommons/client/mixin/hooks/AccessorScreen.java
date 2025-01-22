package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface AccessorScreen
{
	@Invoker("children")
	public List<? extends Element> tcdcommons_children();
	
	@Invoker("addDrawableChild")
	public <T extends Element & Drawable & Selectable> T tcdcommons_addDrawableChild(T drawableElement);
	
	@Invoker("addDrawable")
	public <T extends Drawable> T tcdcommons_addDrawable(T drawable);
    
	@Invoker("addSelectableChild")
	public <T extends Element & Selectable> T tcdcommons_addSelectableChild(T child);
    
	@Invoker("remove")
	public void tcdcommons_remove(Element child);
    
	@Invoker("clearChildren")
	public void tcdcommons_clearChildren();
}
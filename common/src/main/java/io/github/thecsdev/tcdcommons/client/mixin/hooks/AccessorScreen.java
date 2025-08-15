package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface AccessorScreen
{
	@Invoker("children")
	public List<? extends GuiEventListener> tcdcommons_children();
	
	@Invoker("addRenderableWidget")
	public <T extends GuiEventListener & Renderable & NarratableEntry> T tcdcommons_addDrawableChild(T drawableElement);
	
	@Invoker("addRenderableOnly")
	public <T extends Renderable> T tcdcommons_addDrawable(T drawable);
    
	@Invoker("addWidget")
	public <T extends GuiEventListener & NarratableEntry> T tcdcommons_addSelectableChild(T child);
    
	@Invoker("removeWidget")
	public void tcdcommons_remove(GuiEventListener child);
    
	@Invoker("clearWidgets")
	public void tcdcommons_clearChildren();
}
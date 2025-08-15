package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Checkbox.class)
public interface AccessorCheckboxWidget
{
	static @Accessor("CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE") ResourceLocation getSelectedHighlightedTexture() { throw new AssertionError(); }
	static @Accessor("CHECKBOX_SELECTED_SPRITE") ResourceLocation getSelectedTexture() { throw new AssertionError(); }
	static @Accessor("CHECKBOX_HIGHLIGHTED_SPRITE") ResourceLocation getHighlightedTexture() { throw new AssertionError(); }
	static @Accessor("CHECKBOX_SPRITE") ResourceLocation getTexture() { throw new AssertionError(); }
}
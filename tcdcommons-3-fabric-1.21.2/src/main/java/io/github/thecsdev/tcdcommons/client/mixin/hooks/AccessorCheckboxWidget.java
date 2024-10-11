package io.github.thecsdev.tcdcommons.client.mixin.hooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.util.Identifier;

@Mixin(CheckboxWidget.class)
public interface AccessorCheckboxWidget
{
	static @Accessor("SELECTED_HIGHLIGHTED_TEXTURE") Identifier getSelectedHighlightedTexture() { throw new AssertionError(); }
	static @Accessor("SELECTED_TEXTURE") Identifier getSelectedTexture() { throw new AssertionError(); }
	static @Accessor("HIGHLIGHTED_TEXTURE") Identifier getHighlightedTexture() { throw new AssertionError(); }
	static @Accessor("TEXTURE") Identifier getTexture() { throw new AssertionError(); }
}
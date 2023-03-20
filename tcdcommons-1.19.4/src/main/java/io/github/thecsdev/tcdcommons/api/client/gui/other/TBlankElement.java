package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A blank {@link TElement} that does nothing.
 */
public class TBlankElement extends TElement
{
	public TBlankElement() { this(0,0,0,0); }
	public TBlankElement(int x, int y, int width, int height) { super(x, y, width, height); }
	public @Override boolean isClickThrough() { return true; }
	public @Override boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return !gainingFocus; }
	public @Override void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime) {}
}
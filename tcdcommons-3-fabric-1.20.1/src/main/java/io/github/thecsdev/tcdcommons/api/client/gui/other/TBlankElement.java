package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class TBlankElement extends TElement
{
	public TBlankElement(int x, int y, int width, int height) { super(x, y, width, height); }
	public @Virtual @Override boolean isFocusable() { return super.isFocusable(); }
	public @Virtual @Override void render(TDrawContext pencil) {}
}
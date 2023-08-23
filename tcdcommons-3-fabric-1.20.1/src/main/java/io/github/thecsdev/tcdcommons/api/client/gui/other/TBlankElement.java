package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputDiscoveryPhase;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class TBlankElement extends TElement
{
	public TBlankElement(int x, int y, int width, int height) { super(x, y, width, height); }
	public @Virtual @Override boolean isFocusable() { return false; }
	public @Virtual @Override void render(TDrawContext pencil) {}
	public @Virtual @Override boolean input(TInputContext inputContext, InputDiscoveryPhase inputPhase) { return false; }
	public @Virtual @Override boolean input(TInputContext inputContext) { return false; }
}
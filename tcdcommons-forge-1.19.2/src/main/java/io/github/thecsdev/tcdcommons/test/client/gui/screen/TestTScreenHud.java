package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;

public class TestTScreenHud extends TScreen
{
	// ==================================================
	public TestTScreenHud() { super(TextUtils.literal("Testing HUD TScreen")); }
	// ==================================================
	protected @Override void init()
	{
		var btn = new TButtonWidget(getTpeWidth() / 2 - 50, 40, 100, 20, TextUtils.literal("Hello, Test."), null);
		addTChild(btn);
	}
	// ==================================================
	public @Override void renderBackground(PoseStack matrices) {}
	// ==================================================
}
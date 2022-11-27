package thecsdev.tcdcommons.test.client.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import thecsdev.tcdcommons.api.util.TextUtils;

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
	public @Override void renderBackground(MatrixStack matrices) {}
	// ==================================================
}
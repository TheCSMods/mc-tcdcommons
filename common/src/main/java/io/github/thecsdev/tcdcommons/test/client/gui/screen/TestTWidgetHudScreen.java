package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TWidgetHudScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import net.minecraft.resources.ResourceLocation;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries.HUD_SCREEN;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

public final class TestTWidgetHudScreen extends TWidgetHudScreen
{
	// ==================================================
	public static final ResourceLocation HUD_SCREEN_ID = ResourceLocation.fromNamespaceAndPath(getModID(), "test_hud_widget_screen");
	// --------------------------------------------------
	protected float tickTime = 0;
	// ==================================================
	public TestTWidgetHudScreen() { super(literal(TestTWidgetHudScreen.class.getSimpleName()), HUD_SCREEN_ID); }
	// --------------------------------------------------
	public @Override void render(TDrawContext pencil)
	{
		//render
		super.render(pencil);
		
		//automatic unregistering process
		this.tickTime += pencil.deltaTime;
		if(this.tickTime > 200)
		{
			this.tickTime = 0;
			HUD_SCREEN.unregister(HUD_SCREEN_ID);
		}
	}
	// ==================================================
	public static boolean isShown() { return HUD_SCREEN.containsKey(HUD_SCREEN_ID); }
	public static void show()
	{
		//if is already shown, do nothing
		if(isShown()) return;
		
		//create screen, and put it on the hud
		final var sc = new TestTWidgetHudScreen();
		HUD_SCREEN.register(HUD_SCREEN_ID, sc.getAsScreen());
	}
	// ==================================================
}
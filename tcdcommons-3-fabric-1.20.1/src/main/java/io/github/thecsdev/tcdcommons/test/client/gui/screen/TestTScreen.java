package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel.HEIGHT;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.net.URL;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.layout.UITableLayout;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TTextureElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenPlus;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UIExternalTexture;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class TestTScreen extends TScreenPlus
{
	// ==================================================
	private static @Nullable TTextureElement tex;
	private static @Nullable UIExternalTexture cacheTex; //always respect the bandwidth, even when testing
	static
	{
		try
		{
			UIExternalTexture.loadTextureAsync(
					new URL("https://avatars.githubusercontent.com/u/120978613?v=4"),
					MC_CLIENT,
					res -> { cacheTex = res; if(tex != null) tex.setTexture(res); },
					exc -> exc.printStackTrace());
		}
		catch(Exception e) {}
	}
	// --------------------------------------------------
	public final Screen parent;
	// ==================================================
	public TestTScreen(Screen parent)
	{
		super(literal(TestTScreen.class.getSimpleName())); this.parent = parent;
	}
	// --------------------------------------------------
	public final void open() { MC_CLIENT.setScreen(getAsScreen()); }
	public final @Override void close() { MC_CLIENT.setScreen(this.parent); }
	// ==================================================
	public final @Override void init()
	{
		//calculate dimensions
		//final int x = getWidth() / 4, w = getWidth() / 2;
		final int x = getWidth() / 8, w = getWidth() - x*2;
		
		//init stuff
		init_menuBar(x, w);
		init_panel(x, HEIGHT + 5, w, getHeight() - (HEIGHT*2 - 10));
	}
	// --------------------------------------------------
	protected final TMenuBarPanel init_menuBar(int x, int width)
	{
		//create and add the menu
		final var menu = new TMenuBarPanel(x, getY(), width);
		addChild(menu);
		
		//add items to the menu
		menu.addButton(literal("File"), btn ->
		{
			final var menu_file = new TContextMenuPanel(btn);
			menu_file.addButton(literal("New"), btn2 -> {});
			menu_file.addButton(literal("Open file"), btn2 ->
			{
				TFileChooserScreen.builder().showOpenFileDialog(result ->
				{
					System.out.println("File chooser results: " +
							result.getReturnValue() + " | " +
							result.getSelectedFile());
				});
			});
			menu_file.addButton(literal("Open Projects from File System"), btn2 -> {});
			menu_file.addButton(literal("Recent files >"), btn2 -> {});
			menu_file.addSeparator();
			menu_file.addButton(literal("Close Editor"), btn2 -> {});
			menu_file.addButton(literal("Close All Editors"), btn2 -> {});
			menu_file.addSeparator();
			menu_file.addButton(literal("Save"), btn2 -> {});
			menu_file.addButton(literal("Save As"), btn2 ->
			{
				TFileChooserScreen.builder().showSaveFileDialog(result ->
				{
					System.out.println("File chooser results: " +
							result.getReturnValue() + " | " +
							result.getSelectedFile());
				});
			});
			menu_file.addButton(literal("Save All"), btn2 -> {});
			menu_file.addButton(literal("Revert"), btn2 -> {});
			//
			menu_file.open();
		});
		menu.addButton(literal("Edit"), btn -> {});
		menu.addButton(literal("Source"), btn -> {});
		menu.addButton(literal("Refactor"), btn -> {});
		menu.addButton(literal("Navigate"), btn -> {});
		menu.addButton(literal("Search"), btn -> {});
		menu.addButton(literal("Project"), btn -> {});
		menu.addButton(literal("Run"), btn -> {});
		menu.addButton(literal("Window"), btn -> {});
		menu.addButton(literal("Help"), btn -> {});
		menu.addButton(literal("Close"), btn -> close());
		
		//return the menu
		return menu;
	}
	// --------------------------------------------------
	protected final TPanelElement init_panel(int x, int y, int width, int height)
	{
		//create a testing panel
		final var panel = new TPanelElement(x, y, width, height);
		panel.setScrollFlags(TPanelElement.SCROLL_BOTH);
		panel.setSmoothScroll(true);
		panel.setScrollPadding(5);
		addChild(panel);
		
		/*final var scroll_x = new TScrollBarWidget(panel.getX(), panel.getEndY(), panel.getWidth(), 10, panel);
		addChild(scroll_x);
		final var scroll_y = new TScrollBarWidget(panel.getEndX(), panel.getY(), 10, panel.getHeight(), panel);
		addChild(scroll_y);*/
		
		//create some test elements
		for(int i = 0; i < 14; i++) panel.addChild(new TButtonWidget(0, 0, 70, 20, literal("Test 1:" + i)));
		panel.addChild(tex = new TTextureElement(0, 0, 70, 70, new UITexture()));
		if(cacheTex != null) tex.setTexture(cacheTex);
		for(int i = 0; i < 14; i++) panel.addChild(new TButtonWidget(0, 0, 70, 20, literal("Test 2: " + i)));
		new UITableLayout(4).apply(panel);
		
		//return the panel
		return panel;
	}
	// ==================================================
	public static enum TestEnum implements ITextProvider
	{
		OPTION_1(literal("Enum Option 1")),
		OPTION_2(literal("Enum Option 2")),
		OPTION_3(literal("Enum Option 3"));

		private final Text text;
		public final @Override Text getText() { return this.text; }
		private TestEnum(Text text) { this.text = text; }
	}
	// ==================================================
}
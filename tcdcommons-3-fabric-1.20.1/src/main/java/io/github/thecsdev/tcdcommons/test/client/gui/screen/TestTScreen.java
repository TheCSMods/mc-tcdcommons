package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel.HEIGHT;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TEntityRendererElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenPlus;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSelectEnumWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSelectWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSelectWidget.SimpleEntry;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSliderWidget;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;

public final class TestTScreen extends TScreenPlus
{
	// ==================================================
	public final Screen parent;
	// ==================================================
	public TestTScreen(Screen parent) { super(literal("Test TScreen")); this.parent = parent; }
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
		final var btn_select = new TSelectWidget<SimpleEntry>(10, 10, 160, 20);
		btn_select.addEntry(new SimpleEntry(literal("Option 1")));
		btn_select.addEntry(new SimpleEntry(literal("Option 2")));
		btn_select.addEntry(new SimpleEntry(literal("Option 3")));
		btn_select.addEntry(new SimpleEntry(literal("4 noitpO")));
		btn_select.addEntry(new SimpleEntry(literal("Super duper long option 5 because why not...")));
		panel.addChild(btn_select, true);
		
		final var btn_selectEnum = new TSelectEnumWidget<TestEnum>(10, 40, 160, 20);
		panel.addChild(btn_selectEnum, true);
		
		//create a test entity renderer
		final var ent = new TEntityRendererElement(10, 70, 100, 100, EntityType.ARMOR_STAND);
		ent.setFollowsCursor(true);
		panel.addChild(ent, true);
		
		final var entOverlay = new TFillColorElement(ent.getX(), ent.getY(), ent.getWidth(), ent.getHeight());
		entOverlay.setColor(-922746881);
		panel.addChild(entOverlay, false);
		
		final var entOverlayZSlider = new TSliderWidget(ent.getX(), ent.getEndY(), ent.getWidth(), 10, 0);
		entOverlayZSlider.setOnClick(__ -> entOverlay.setZOffset((float) (entOverlayZSlider.getValue() * 100)));
		panel.addChild(entOverlayZSlider, false);
		
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
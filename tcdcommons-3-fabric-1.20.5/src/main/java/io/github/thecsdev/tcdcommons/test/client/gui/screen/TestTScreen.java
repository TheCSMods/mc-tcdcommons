package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel.HEIGHT;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import io.github.thecsdev.tcdcommons.api.client.gui.layout.UIHorizontalGridLayout;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TStackTracePanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenPlus;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import net.minecraft.client.gui.screen.Screen;

public final class TestTScreen extends TScreenPlus
{
	// ==================================================
	public final Screen parent;
	// ==================================================
	public TestTScreen(Screen parent)
	{
		super(literal(TestTScreen.class.getSimpleName()));
		this.parent = parent;
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
			menu_file.addButton(literal("Open file"), btn2 ->
			{
				TFileChooserScreen.builder().showOpenFileDialog(result ->
				{
					System.out.println("File chooser results: " +
							result.getReturnValue() + " | " +
							result.getSelectedFile());
				});
			});
			menu_file.addSeparator();
			menu_file.addButton(literal("Save As"), btn2 ->
			{
				TFileChooserScreen.builder().showSaveFileDialog(result ->
				{
					System.out.println("File chooser results: " +
							result.getReturnValue() + " | " +
							result.getSelectedFile());
				});
			});
			//
			menu_file.open();
		});
		menu.addButton(literal("Edit"), btn -> {});
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
		
		//add testing stuff to the panel
		final var dontAsk = new TButtonWidget(0, 0, panel.getWidth() - 10, 20, literal("Testing button that will do something random..."));
		dontAsk.setOnClick(__ ->
		{
			final var stp = new TStackTracePanel(
					getWidth() / 4, 25, getWidth() / 2, getHeight() - 50,
					new Exception("A fox did not wish to sleep.",
							new RuntimeException("As a consequence, I have put a reference to them, in this debug error dialog.",
									new Error("If you are reading this, tell them to go to sleep, NOW!"))));
			stp.setZOffset(64);
			addChild(stp);
		});
		panel.addChild(dontAsk);
		
		for(int i = 0; i < 10; i++)
			panel.addChild(new TButtonWidget(0, 0, (int)((Math.random() / 2) * (panel.getWidth() - 10)), 20));
		
		new UIHorizontalGridLayout().apply(panel);
		
		//return the panel
		return panel;
	}
	// ==================================================
}
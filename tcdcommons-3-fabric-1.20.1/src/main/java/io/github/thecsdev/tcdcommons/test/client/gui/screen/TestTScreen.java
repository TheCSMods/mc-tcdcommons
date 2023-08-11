package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import java.awt.Color;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TEntityRendererElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TMenuBarPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenPlus;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TCheckboxWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSliderWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TTextFieldWidget;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.entity.EntityType;

public final class TestTScreen extends TScreenPlus
{
	// ==================================================
	public final Screen parent;
	// ==================================================
	public TestTScreen(Screen parent)
	{
		super(literal("Test TScreen"));
		this.parent = parent;
	}
	public final void open() { TCDCommonsClient.MC_CLIENT.setScreen(getAsScreen()); }
	public final @Override void close() { TCDCommonsClient.MC_CLIENT.setScreen(this.parent); }
	// --------------------------------------------------
	public final @Override void init()
	{
		//a menu
		final var menu = new TMenuBarPanel(this);
		menu.addButton(literal("File"), btn ->
		{
			final var menu_file = new TContextMenuPanel(btn);
			menu_file.addButton(literal("New"), btn2 -> {});
			menu_file.addButton(literal("Open file"), btn2 -> {});
			menu_file.addButton(literal("Open Projects from File System"), btn2 -> {});
			menu_file.addButton(literal("Recent files >"), btn2 -> {});
			//TODO - SEPARATOR
			menu_file.addButton(literal("Close Editor"), btn2 -> {});
			menu_file.addButton(literal("Close All Editors"), btn2 -> {});
			//TODO - SEPARATOR
			menu_file.addButton(literal("Save"), btn2 -> {});
			menu_file.addButton(literal("Save As"), btn2 -> {});
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
		
		//create a test entity renderer
		final var ent = new TEntityRendererElement(10, getHeight() - 210, 100, 100, EntityType.ARMOR_STAND);
		ent.setFollowsCursor(true);
		addChild(ent);
		
		final var entOverlay = new TFillColorElement(ent.getX(), ent.getY(), ent.getWidth(), ent.getHeight());
		entOverlay.setColor(-922746881);
		addChild(entOverlay);
		
		final var entOverlayZSlider = new TSliderWidget(ent.getX(), ent.getEndY(), ent.getWidth(), 10, 0);
		entOverlayZSlider.setOnClick(__ -> entOverlay.setZOffset((float) (entOverlayZSlider.getValue() * 100)));
		addChild(entOverlayZSlider);
		
		//create a testing panel
		final var panel = new TPanelElement(getWidth() / 4, 20, getWidth() / 2, getHeight() - 40);
		//panel.setScrollFlags(TPanelElement.SCROLL_VERTICAL); - automatic by scroll-bar
		panel.setSmoothScroll(true);
		panel.setScrollPadding(5);
		addChild(panel);
		
		final var scroll_x = new TScrollBarWidget(panel.getX(), panel.getEndY(), panel.getWidth(), 10, panel);
		addChild(scroll_x);
		final var scroll_y = new TScrollBarWidget(panel.getEndX(), panel.getY(), 10, panel.getHeight(), panel);
		addChild(scroll_y);
		
		final var btn = new TButtonWidget(20, 20, 100, 20, literal("Hello World"));
		btn.setOnClick(__ -> System.out.println("Hello world, you just clicked a button!"));
		panel.addChild(btn, true);
		
		//a checkbox
		panel.addChild(new TCheckboxWidget(60, 60, 100, 20, literal("Just a normal checkbox")), true);
		final var cb2 = new TCheckboxWidget(300, 100, 150, 20, literal("Totally normal checkbox..."));
		cb2.setHorizontalAlignment(HorizontalAlignment.LEFT, HorizontalAlignment.RIGHT);
		panel.addChild(cb2, true);
		
		//some text input
		final var tf = new TTextFieldWidget(20, 140, 120, 20);
		tf.setTooltip(Tooltip.of(literal("This is a text field. Type whatever. "
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
		panel.addChild(tf, true);
		
		//some labels
		final var l1 = new TLabelElement(5, 160, panel.getWidth() - 10, 20, literal("Hello world! I am a label."));
		l1.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
		l1.setTextColor(Color.YELLOW.getRGB());
		panel.addChild(l1, true);
		
		//a slider
		panel.addChild(new TSliderWidget(20, 200, 150, 20, literal("I am a slider"), 0.5), true);
		
		//a button hidden far down; gotta scroll
		final var btn_hidden = new TButtonWidget(40, 360, 160, 20, literal("heya! hide and seek :p"));
		btn_hidden.setEnabled(false);
		panel.addChild(btn_hidden, true);
		
		//test
		/*	final var panelTopmost = panel.getChildren().getTopmostElements();
		System.out.println("-");
		System.out.println(Objects.toString(panelTopmost.Item1));
		System.out.println(Objects.toString(panelTopmost.Item2));
		System.out.println(Objects.toString(panelTopmost.Item3));
		System.out.println(Objects.toString(panelTopmost.Item4));
		System.out.println("-");*/
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.test.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TEntityRendererElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TMenuBarPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenPlus;
import io.github.thecsdev.tcdcommons.api.client.gui.util.Direction2D;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TCheckboxWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TDynamicSliderWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSelectEnumWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TTextFieldWidget;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;

public final class TestTScreen extends TScreenPlus
{
	// ==================================================
	public enum TestEnum { Option1, Option2, Option3, Option4, Option5, Garry, AnExtremelyLongOptionJustForTestingSake }
	// ==================================================
	public final Screen parent;
	protected TMenuBarPanel menu;
	protected TPanelElement panel;
	protected TPanelElement panel2;
	// ==================================================
	public TestTScreen(Screen parent)
	{
		super(TextUtils.literal("Testing TScreen"));
		this.parent = parent;
	}
	// ==================================================
	@Override
	protected void init()
	{
		//test entity renderer
		{
			TPanelElement p = new TPanelElement(10, 20, 60, 60);
			p.addTChild(new TEntityRendererElement(0, 0, 60, 60, EntityType.ZOMBIE));
			addTChild(p);
		}
		
		init_menu();
		
		panel = new TPanelElement(width / 4, 20, width / 2, height - 40);
		panel.setScrollFlags(TPanelElement.SCROLL_BOTH);
		panel.setSmoothScroll(true);
		this.addTChild(panel);
		
		int x = panel.getTpeWidth() / 2 - panel.getTpeWidth() / 4;
		int w = panel.getTpeWidth() / 2;
		
		panel.addTChild(new TButtonWidget(
				x, 10, w, 20,
				TextUtils.fLiteral("§6Test 1"),
				arg0 -> {}));
		
		panel.addTChild(new TButtonWidget(
				x, 35, w, 20,
				TextUtils.fLiteral("§6Test 2"),
				arg0 -> {}));
		
		panel.addTChild(new TButtonWidget(
				x, 60, w, 20,
				TextUtils.fLiteral("§6Test 3 §r- §dClose"),
				arg0 -> close()));
		
		panel.addTChild(new TCheckboxWidget(
				x, 85, w, 20,
				TextUtils.fLiteral("§6Test 4 §r- §aA text with a"),
				false));
		
		TCheckboxWidget cw = new TCheckboxWidget(
				x, 110, w, 20,
				TextUtils.fLiteral("§6Test 5 §r- §bHello reverse"),
				false, true);
		cw.setHorizontalAlignment(HorizontalAlignment.RIGHT, HorizontalAlignment.RIGHT);
		cw.setTooltip(TextUtils.literal("Hello world! This is a tooltip."));
		panel.addTChild(cw);
		
		panel.addTChild(new TDynamicSliderWidget(x, 135, w, 20, 0, TextUtils.literal("Testing 1"), null));
		{
			var sw = new TDynamicSliderWidget(x, 160, w, 20, 0.5, TextUtils.literal("Testing 2"), null);
			sw.setSliderDirection(Direction2D.LEFT);
			panel.addTChild(sw);
		}
		panel.addTChild(new TDynamicSliderWidget(x, 185, w, 20, 1, TextUtils.literal("Testing 3"), null));
		
		/*panel.addTChild(new TSelectWidget(x, 160, w, 20)
				.addDropdownOption(TextUtils.fLiteral("Option 1"), null)
				.addDropdownOption(TextUtils.fLiteral("Option 2"), null)
				.addDropdownOption(TextUtils.fLiteral("Option 3"), null)
				.addDropdownSeparator()
				.addDropdownOption(TextUtils.fLiteral("Another option 1"), null)
				.addDropdownOption(TextUtils.fLiteral("Another option 2"), null)
				);*/
		panel.addTChild(new TSelectEnumWidget<>(x, 210, w, 20, TestEnum.class));
		
		panel2 = new TPanelElement(x, 235, w, 100);
		panel.addTChild(panel2);
		
		panel2.addTChild(new TButtonWidget(5, 5, 20, 20, TextUtils.literal("1"), btn -> {}));
		panel2.addTChild(new TButtonWidget(30, 5, 20, 20, TextUtils.literal("2"), btn -> {}));
		panel2.addTChild(new TButtonWidget(55, 5, 20, 20, TextUtils.literal("3"), btn -> {}));
		panel2.addTChild(new TTextFieldWidget(5, 30, w - 10, 65));
		
		//scrolly
		addTChild(new TScrollBarWidget(panel.getTpeX(), panel.getTpeEndY(), panel.getTpeWidth(), 12, panel));
		addTChild(new TScrollBarWidget(panel.getTpeEndX(), panel.getTpeY(), 12, panel.getTpeHeight(), panel));
	}
	// --------------------------------------------------
	protected void init_menu()
	{
		menu = new TMenuBarPanel(width / 4, 0, width / 2, 15);
		{
			var menu_file = menu.addItem(TextUtils.literal("File"));
			menu_file.addDropdownOption(TextUtils.literal("New"), null);
			menu_file.addDropdownOption(TextUtils.literal("Open file"), null);
			menu_file.addDropdownOption(TextUtils.literal("Save changes"), null);
			menu_file.addDropdownSeparator();
			menu_file.addDropdownOption(TextUtils.literal("Restart"), null);
			menu_file.addDropdownOption(TextUtils.literal("Exit"), null);
		}
		{
			var menu_edit = menu.addItem(TextUtils.literal("Edit"));
			menu_edit.addDropdownOption(TextUtils.literal("Undo"), null);
			menu_edit.addDropdownOption(TextUtils.literal("Redo"), null);
			menu_edit.addDropdownSeparator();
			menu_edit.addDropdownOption(TextUtils.literal("Copy selection"), null);
			menu_edit.addDropdownOption(TextUtils.literal("Paste"), null);
		}
		{
			var menu_view = menu.addItem(TextUtils.literal("View"));
			menu_view.addDropdownOption(TextUtils.literal("Everything"), null);
			menu_view.addDropdownOption(TextUtils.literal("Nothing"), null);
		}
		{
			var menu_help = menu.addItem(TextUtils.literal("Help"));
			menu_help.addDropdownOption(TextUtils.literal("You called for help."), null);
			menu_help.addDropdownOption(TextUtils.literal("But nobody came..."), null);
		}
		addTChild(menu);
	}
	// --------------------------------------------------
	protected boolean moveRight = true;
	protected boolean moveDown = true;
	protected float moveDelta = 0;
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		//render super
		super.render(matrices, mouseX, mouseY, delta);
		
		//test movement
		/*moveDelta += delta;
		if(moveDelta > 0.5f)
		{
			//move
			panel.move((moveRight ? 1 : -1), (moveDown ? 1 : -1));
			moveDelta = 0;
			
			//x check
			if(panel.getTpeX() < 0) moveRight = true;
			else if(panel.getTpeX() + panel.getTpeWidth() > this.width) moveRight = false;
			
			//y check
			if(panel.getTpeY() < 0) moveDown = true;
			else if(panel.getTpeY() + panel.getTpeHeight() > this.height) moveDown = false;
		}*/
	}
	// --------------------------------------------------
	// public @Override void close() { MinecraftClient.getInstance().setScreen(parent); }
	// ==================================================
}
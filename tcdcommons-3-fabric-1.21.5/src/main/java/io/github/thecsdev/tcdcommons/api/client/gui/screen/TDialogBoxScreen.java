package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.layout.UILayout;
import io.github.thecsdev.tcdcommons.api.client.gui.layout.UIListLayout;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TBlankElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Axis2D;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.enumerations.VerticalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * A {@link TScreen} that shows a {@link Text}ual dialog box.
 */
public final class TDialogBoxScreen extends TScreen implements IParentScreenProvider
{
	// ==================================================
	private @Nullable Screen parent;
	private @Nullable Text message;
	// ==================================================
	public TDialogBoxScreen(@Nullable Screen parent, Text title, Text message)
		throws NullPointerException
	{
		super(Objects.requireNonNull(title));
		this.parent = parent;
		this.message = Objects.requireNonNull(message);
	}
	// --------------------------------------------------
	public final @Override Screen getParentScreen() { return this.parent; }
	public final @Override void close() { MC_CLIENT.setScreen(this.parent); }
	public final @Override void renderBackground(TDrawContext pencil)
	{
		if(this.parent != null) this.parent.render(pencil, pencil.mouseX, pencil.mouseY, pencil.deltaTime);
		else super.renderBackground(pencil);
	}
	// ==================================================
	public final @Nullable Text getMessage() { return this.message; }
	// ==================================================
	protected final @Override void init()
	{
		//preparation
		final Text msg = (this.message != null) ? this.message : literal("null");
		
		//add a content pane onto which all the elements will be added
		final var contentPane = new TFillColorElement(0, 0, getWidth(), getHeight());
		contentPane.setColor(0x22FFFFFF);
		contentPane.setZOffset(TCDCommonsClient.MAGIC_ITEM_Z_OFFSET);
		addChild(contentPane, false);
		
		//create the main dialog box panel
		final var panel = new TPanelElement(0, 0, getWidth() / 2, (int)(getHeight() / 1.5f));
		panel.setScrollFlags(0);
		panel.setScrollPadding(0);
		panel.setBackgroundColor(0xff555555);
		panel.setOutlineColor(0xff000000);
		contentPane.addChild(panel, false);
		
		//create the title-bar panel
		final var panel_title = new TPanelElement(0, 0, panel.getWidth(), 30);
		panel_title.setScrollFlags(0);
		panel_title.setScrollPadding(0);
		panel_title.setOutlineColor(0xff000000);
		panel.addChild(panel_title);
		
		final var lbl_title = new TLabelElement(0, 0, panel_title.getWidth(), panel_title.getHeight());
		lbl_title.setText(getTitle());
		lbl_title.setTextSideOffset(10);
		panel_title.addChild(lbl_title);
		
		//create the footer panel
		final var panel_footer = new TPanelElement(0, panel.getHeight() - 30, panel.getWidth(), 30);
		panel_footer.setScrollFlags(0);
		panel_footer.setScrollPadding(0);
		panel_footer.setOutlineColor(0xff000000);
		panel.addChild(panel_footer);
		
		final var btn_done = new TButtonWidget(panel_footer.getWidth() - 65, 5, 60, 20);
		btn_done.setText(translatable("gui.done"));
		btn_done.setOnClick(__ -> close());
		panel_footer.addChild(btn_done);
		
		//the message panel
		final var panel_message = new TPanelElement(
				0, panel_title.getHeight(),
				panel.getWidth() - 8,
				panel.getHeight() - (panel_title.getHeight() + panel_footer.getHeight()));
		panel_message.setScrollFlags(TPanelElement.SCROLL_VERTICAL);
		panel_message.setScrollPadding(10);
		panel_message.setBackgroundColor(0);
		panel_message.setOutlineColor(0);
		panel.addChild(panel_message);
		
		final var tr = getTextRenderer();
		final int fh = getTextRenderer().fontHeight;
		final var msg_lines = tr.wrapLines(msg, panel_message.getWidth() - (panel_message.getScrollPadding() * 2));
		for(final var line : msg_lines)
		{
			final var n1 = UILayout.nextChildVerticalRect(panel_message);
			final var lbl = new TBlankElement(n1.x, n1.y + 2, n1.width, fh)
			{
				public final @Override void render(TDrawContext pencil) {
					pencil.drawText(tr, line, getX(), getY(), 0xccffffff, true);
				}
			};
			panel_message.addChild(lbl, false);
		}
		
		final var scroll_panelMessage = new TScrollBarWidget(
				panel_message.getEndX(), panel_message.getY(),
				8, panel_message.getHeight(),
				panel_message, false);
		panel.addChild(scroll_panelMessage, false);
		
		//finally, center the dialog box
		new UIListLayout(Axis2D.Y, VerticalAlignment.CENTER, HorizontalAlignment.CENTER).apply(contentPane);
	}
	// ==================================================
}
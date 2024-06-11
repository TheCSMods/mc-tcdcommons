package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.layout.UIListLayout;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.TitleBar;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.TitleBar.TitleBarProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.input.MouseDragHelper;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Axis2D;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.enumerations.VerticalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * A {@link TScreen} that shows a stack trace to the user.
 */
public final class TStackTraceScreen extends TScreen implements IParentScreenProvider
{
	// ==================================================
	private final Screen parent;
	private final Throwable throwable;
	// ==================================================
	public TStackTraceScreen(@Nullable Screen parent, Throwable throwable) throws NullPointerException
	{
		super(literal(throwable.getClass().getName()));
		this.parent = parent;
		this.throwable = Objects.requireNonNull(throwable);
	}
	public final @Override Screen getParentScreen() { return this.parent; }
	public final Throwable getThrowable() { return this.throwable; }
	public final @Override void close() { MC_CLIENT.setScreen(this.parent); }
	// ==================================================
	protected final @Override void init()
	{
		//add a content pane onto which all the elements will be added
		final var contentPane = new TFillColorElement(0, 0, getWidth(), getHeight());
		contentPane.setColor(0x22FFFFFF);
		contentPane.setZOffset(TCDCommonsClient.MAGIC_ITEM_Z_OFFSET);
		addChild(contentPane, false);
		
		//create and a stack trace panel
		contentPane.addChild(new STWindowPanel(), false);
	}
	// --------------------------------------------------
	public final @Override void renderBackground(TDrawContext pencil)
	{
		if(this.parent != null) this.parent.render(pencil, pencil.mouseX, pencil.mouseY, pencil.deltaTime);
		else super.renderBackground(pencil);
	}
	// ==================================================
	/**
	 * The {@link TStackTraceScreen} panel that will take the appearance of a window.
	 */
	private final class STWindowPanel extends TRefreshablePanelElement
	{
		// ==================================================
		public static final float TEXT_SCALE = 0.8f;
		// --------------------------------------------------
		protected final MouseDragHelper dragHelper;
		// ==================================================
		public STWindowPanel()
		{
			super(20, 20, TStackTraceScreen.this.getWidth() - 40, TStackTraceScreen.this.getHeight() - 40);
			setBackgroundColor(0xFF252525);
			this.dragHelper = new MouseDragHelper() {
				protected final @Override void apply(int deltaX, int deltaY) { move(deltaX, deltaY); }
			};
		}
		// ==================================================
		public final @Override boolean input(TInputContext inputContext)
		{
			switch(inputContext.getInputType())
			{
				//return true on mouse presses so as to allow dragging
				case MOUSE_PRESS: return true;
				//for mouse drag, forward the drag to the drag helper if draggable
				case MOUSE_DRAG:
					//if(!true) break;
					return this.dragHelper.onMouseDrag(inputContext.getMouseDelta());
				//once the dragging ends, snap to parent bounds, so the panel cannot be dragged out of bounds
				case MOUSE_DRAG_END:
					MouseDragHelper.snapToParentBounds(STWindowPanel.this);
					return true;
				default: break;
			}
			
			//return false by default
			return false;
		}
		// --------------------------------------------------
		protected final @Override void init()
		{
			//initialize the title bar
			final var titleBar = new TitleBar(0, 0, getWidth(), new TitleBarProxy()
			{
				public final @Override Text getTitle() { return TStackTraceScreen.this.getTitle(); }
				public final @Override boolean canClose() { return true; }
				public final @Override void onClose() { TStackTraceScreen.this.close(); }
			});
			addChild(titleBar, true);
			
			//initialize the panel
			final var panel = new TPanelElement(
					0, titleBar.getHeight(),
					getWidth() - 9, getHeight() - (titleBar.getHeight() + 9));
			panel.setBackgroundColor(0x00000000);
			panel.setOutlineColor(0x00000000);
			addChild(panel, true);
			
			//add stack trace elements to the panel
			boolean dark_bg = true;
			for(final var ste : getLinesFromThrowable(TStackTraceScreen.this.throwable))
			{
				dark_bg = !dark_bg;
				
				final var bg_lbl = new TFillColorElement(0, 0, getWidth() - 2, 15);
				bg_lbl.setColor(dark_bg ? 0x11FFFFFF : 0x22FFFFFF);
				panel.addChild(bg_lbl, false);
				
				final var lbl_ste = new TLabelElement(10, 0, bg_lbl.getWidth()-10, bg_lbl.getHeight());
				lbl_ste.setText(literal(ste.toString()));
				lbl_ste.setTextColor(0xAAFFFFFF);
				bg_lbl.addChild(lbl_ste, true);
			}
			new UIListLayout(Axis2D.Y, VerticalAlignment.TOP, HorizontalAlignment.CENTER, 0)
				.apply(panel);
			
			//initialize scroll-bars for the panel
			final var scroll_x = new TScrollBarWidget(
					panel.getX(), panel.getEndY(),
					panel.getWidth(), 8,
					panel);
			final var scroll_y = new TScrollBarWidget(
					panel.getEndX(), panel.getY(),
					8, panel.getHeight(),
					panel);
			addChild(scroll_x, false);
			addChild(scroll_y, false);
		}
		// --------------------------------------------------
		public final @Override void render(TDrawContext pencil) { pencil.pushTTextScale(TEXT_SCALE); super.render(pencil); }
		// ==================================================
		private static final String[] getLinesFromThrowable(Throwable throwable)
		{
			final var sw = new StringWriter();
			final var pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			return sw.toString().replaceAll("\t", "    ").split("\\r?\\n");
		}
		// ==================================================
	}
	// ==================================================
}
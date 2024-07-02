package io.github.thecsdev.tcdcommons.api.client.gui.panel;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.awt.Rectangle;
import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.layout.UILayout;
import io.github.thecsdev.tcdcommons.api.client.gui.layout.UIListLayout;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TTextureElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.client.gui.util.input.MouseDragHelper;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Axis2D;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.enumerations.VerticalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * A {@link TPanelElement} that displays information about
 * a {@link Throwable} that was thrown.
 */
public final class TStackTracePanel extends TRefreshablePanelElement
{
	// ==================================================
	private @Nullable String    title       = null;
	private @Nullable String    description = null;
	private @Nullable Throwable throwable   = null;
	private @Nullable Runnable  closeAction = null;
	// --------------------------------------------------
	private final MouseDragHelper dragHelper = MouseDragHelper.forTElement(this);
	// ==================================================
	public TStackTracePanel(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TStackTracePanel(int x, int y, int width, int height, Throwable throwable)
		throws NullPointerException
	{
		super(x, y, width, height);
		setScrollFlags(0);
		setScrollPadding(0);
		setBackgroundColor(0xff8a8a8a);
		setOutlineColor(0xFF000000);
		setZOffset(TCDCommonsClient.MAGIC_ITEM_Z_OFFSET);
		
		this.throwable = throwable;
		this.closeAction = () ->
		{
			final @Nullable var p = getParent();
			if(p != null) p.removeChild(this);
		};
	}
	// --------------------------------------------------
	public final void setTitle(@Nullable String title) { this.title = title; refresh(); }
	public final void setDescription(@Nullable String description) { this.description = description; refresh(); }
	public final void setThrowable(@Nullable Throwable throwable) { this.throwable = throwable; refresh(); }
	public final void setCloseAction(@Nullable Runnable closeAction) { this.closeAction = closeAction; /*refresh();*/ }
	//
	public final @Nullable String getTitle() { return this.title; }
	public final @Nullable String getDescription() { return this.description; }
	public final @Nullable Throwable getThrowable() { return this.throwable; }
	public final @Nullable Runnable getCloseAction() { return this.closeAction; }
	// ==================================================
	public final @Override void render(TDrawContext pencil)
	{
		//fake shadow effect
		for(int i = 10; i > 1; i--)
			pencil.fill(getX() - i, getY() - i, getEndX() + i, getEndY() + i, 0x11000000);
		
		//normal super render
		super.render(pencil);
	}
	// --------------------------------------------------
	protected final @Override void init()
	{
		//prepare
		final var throwable = (this.throwable != null) ?
				this.throwable :
				new NullPointerException("No Throwable was provided for this panel.");
		final var title = (this.title != null) ? this.title : translatable("mco.errorMessage.generic").getString();
		final var description = (this.description != null) ? this.description : throwable.getLocalizedMessage();
		
		final var tr = getTextRenderer();
		final var fh = tr.fontHeight;
		
		//title-bar
		final var panel_title = new TPanelElement(0, 0, getWidth(), getTextRenderer().fontHeight + 14)
		{
			public final @Override boolean input(TInputContext inputContext)
			{
				//handle mouse dragging
				switch(inputContext.getInputType())
				{
					//return true on mouse presses so as to allow dragging
					case MOUSE_PRESS: return true;
					//for mouse drag, forward the drag to the drag helper if draggable
					case MOUSE_DRAG: return TStackTracePanel.this.dragHelper.onMouseDrag(inputContext.getMouseDelta());
					//once the dragging ends, snap to parent bounds, so the panel cannot be dragged out of bounds
					case MOUSE_DRAG_END: MouseDragHelper.snapToParentBounds(TStackTracePanel.this); return true;
					default: break;
				}
				//return false by default
				return false;
			}
		};
		panel_title.setScrollFlags(0);
		panel_title.setScrollPadding(0);
		panel_title.setBackgroundColor(0xff4b4b4b);
		panel_title.setOutlineColor(0xFF000000);
		addChild(panel_title);
		{
			final var ico = new TTextureElement(5, 5, panel_title.getHeight() - 10, panel_title.getHeight() - 10);
			ico.setTexture(new UITexture(
					Identifier.of(getModID(), "textures/gui/icons.png"),
					new Rectangle(192, 0, 64, 64)));
			ico.setTextureColor(1, 1, 0);
			panel_title.addChild(ico);
			
			final var lbl = new TLabelElement(panel_title.getHeight() + 5, 0, panel_title.getWidth(), panel_title.getHeight());
			lbl.setTextHorizontalAlignment(HorizontalAlignment.LEFT);
			lbl.setTextColor(0xFFFFFFFF);
			lbl.setTextScale(0.85f);
			lbl.setText(literal(title).formatted(Formatting.BOLD));
			panel_title.addChild(lbl);
			
			final var btn_close = new TButtonWidget(
					panel_title.getWidth() - panel_title.getHeight(), 0,
					panel_title.getHeight(), panel_title.getHeight(),
					literal("X"))
			{
				protected final @Override void renderBackground(TDrawContext pencil) {}
				public final @Override void postRender(TDrawContext pencil) { pencil.drawTBorder(0xFF000000); }
			};
			btn_close.setOnClick(__ -> { if(this.closeAction != null) this.closeAction.run(); });
			panel_title.addChild(btn_close);
		}
		
		//content-pane
		final int sp = 10;
		final var panel_content = new TPanelElement(
				0, panel_title.getHeight(),
				getWidth(), getHeight() - panel_title.getHeight());
		panel_content.setScrollPadding(sp);
		panel_content.setScrollFlags(0);
		panel_content.setBackgroundColor(0);
		panel_content.setOutlineColor(0);
		addChild(panel_content);
		{
			//description panel
			final var panel_d = new TPanelElement(sp, sp, panel_content.getWidth() - (sp * 2), 50);
			panel_d.setScrollFlags(TPanelElement.SCROLL_BOTH);
			panel_d.setScrollPadding(0);
			panel_d.setBackgroundColor(0);
			panel_d.setOutlineColor(0);
			panel_content.addChild(panel_d);
			
			final var pd_lines = Arrays.stream(description.split("\\r?\\n")).map(s -> literal(s)).toArray(Text[]::new);
			UILayout.initLines(panel_d, 0xCFCFCF, pd_lines);
			
			//stack-trace panel
			final var panel_t = new TPanelElement(
					sp,
					sp + panel_d.getHeight() + 5,
					panel_d.getWidth(),
					panel_content.getHeight() - (sp * 2) - panel_d.getHeight() - 5 - 25);
			panel_t.setScrollFlags(0);
			panel_t.setScrollPadding(0);
			panel_t.setBackgroundColor(0xFF4B4B4B);
			panel_t.setOutlineColor(0xff000000);
			panel_content.addChild(panel_t);
			{
				final var panel_st = new TPanelElement(0, 0, panel_t.getWidth() - 8, panel_t.getHeight() - 8);
				panel_st.setScrollFlags(TPanelElement.SCROLL_BOTH);
				panel_st.setScrollPadding(7);
				panel_st.setBackgroundColor(0);
				panel_st.setOutlineColor(0xff000000);
				panel_t.addChild(panel_st);
				{
					Arrays.stream(ExceptionUtils.getStackTrace(throwable).split("\\r?\\n"))
						.map(s ->
						{
							final var el = new TLabelElement(0, 0, tr.getWidth(s) + 30, fh + 4);
							el.setTextHorizontalAlignment(HorizontalAlignment.LEFT);
							el.setText(literal(s.replaceAll("\t", "    ")));
							el.setTextColor(s.startsWith("\t") ? 0xffaaa398 : 0xffc0c398);
							return el;
						})
						.forEach(el -> panel_st.addChild(el, false));
					new UIListLayout(Axis2D.Y, VerticalAlignment.TOP, HorizontalAlignment.LEFT).apply(panel_st);
				}
				
				final var scroll_h = new StpSbw(
						panel_st.getX(), panel_st.getEndY(), panel_st.getWidth(), 8,
						panel_st, false);
				panel_t.addChild(scroll_h, false);
				
				final var scroll_v = new StpSbw(
						panel_st.getEndX(), panel_st.getY(), 8, panel_st.getHeight(),
						panel_st, false);
				panel_t.addChild(scroll_v, false);
			}
			
			//action-bar panel
			final var panel_a = new TPanelElement(panel_t.getX(), panel_t.getEndY(), panel_t.getWidth(), 25);
			panel_a.setScrollFlags(0);
			panel_a.setScrollPadding(0);
			panel_a.setBackgroundColor(0);
			panel_a.setOutlineColor(0);
			panel_content.addChild(panel_a, false);
			{
				final var btn_copyTrace = new TButtonWidget(0, 0, 20, 20);
				btn_copyTrace.setTooltip(Tooltip.of(translatable("chat.copy")));
				panel_a.addChild(btn_copyTrace, false);
				
				final var bct_ico = new TTextureElement(3, 3, 14, 14);
				bct_ico.setTexture(new UITexture(
						Identifier.of(getModID(), "textures/gui/icons.png"),
						new Rectangle(0, 64, 64, 64)));
				bct_ico.setTextureColor(0.8f, 0.8f, 0.8f);
				btn_copyTrace.addChild(bct_ico);
				
				final var btn_okay = new TButtonWidget(0, 0, Math.min(125, panel_a.getWidth() - 25), 20);
				btn_okay.setText(translatable("gui.ok"));
				btn_okay.setOnClick(__ -> { if(this.closeAction != null) this.closeAction.run(); });
				panel_a.addChild(btn_okay, false);
				
				new UIListLayout(Axis2D.X, VerticalAlignment.BOTTOM, HorizontalAlignment.RIGHT, 5).apply(panel_a);
			}
		}
	}
	// ==================================================
	private static final class StpSbw extends TScrollBarWidget
	{
		static final int COLOR = 0xFF666666;
		public StpSbw(int x, int y, int width, int height, TPanelElement target) { super(x, y, width, height, target); }
		public StpSbw(int x, int y, int width, int height, TPanelElement target, boolean autoSetScrollFlags) { super(x, y, width, height, target, autoSetScrollFlags); }
		public final @Override void renderSliderProgressBar(TDrawContext pencil) {}
		public final @Override void postRender(TDrawContext pencil) { pencil.drawTBorder(COLOR); }
		public final @Override void renderSliderKnob(TDrawContext pencil, int knobX, int knobY, int knobWidth, int knobHeight) {
			pencil.fill(knobX, knobY, knobX + knobWidth, knobY + knobHeight, COLOR);
		}
		public final @Override void render(TDrawContext pencil)
		{
			renderSliderProgressBar(pencil);
			renderSliderKnob(pencil);
			pencil.drawTElementTextTH(this.text, HorizontalAlignment.CENTER);
		}
	}
	// ==================================================
}
package thecsdev.tcdcommons.api.client.gui.panel;

import java.awt.Point;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import thecsdev.tcdcommons.api.client.gui.TElement;
import thecsdev.tcdcommons.api.client.gui.events.TPanelEvents;
import thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import thecsdev.tcdcommons.api.client.gui.util.TElementList;
import thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import thecsdev.tcdcommons.api.util.math.Tuple2;
import thecsdev.tcdcommons.api.util.math.Tuple4;

public class TPanelElement extends TElement
{
	// ==================================================
	public static final int COLOR_OUTLINE = 1358954495;
	public static final int COLOR_OUTLINE_FOCUSED = -5570561;
	
	/**
	 * A scroll flag used to define the scrolling behavior
	 * for {@link #setScrollFlags(int)}.
	 */
	public static final int SCROLL_HORIZONTAL = 0b010,
							SCROLL_VERTICAL   = 0b100,
							SCROLL_BOTH       = SCROLL_HORIZONTAL | SCROLL_VERTICAL;
	// ==================================================
	/**
	 * Defines the scroll behavior for this {@link TPanelElement}.<br/>
	 * 2nd bit - {@link #SCROLL_HORIZONTAL}<br/>
	 * 3rd bit - {@link #SCROLL_VERTICAL}<br/>
	 */
	protected int scrollFlags;
	
	/**
	 * Defines how sensitive scrolling input from
	 * mouse and keyboard is going to be.
	 */
	protected int scrollSensitivity;
	
	protected int scrollPadding;
	protected boolean smoothScroll;
	protected float smoothScrollSpeed;
	// --------------------------------------------------
	/**
	 * A shortcut for the topmost elements of this {@link TPanelElement}.<br/>
	 * See {@link TElementList#getTopmostElements()}.
	 */
	protected final Tuple4<TElement, TElement, TElement, TElement> topmosts;
	// --------------------------------------------------
	/**
	 * Used by {@link #mouseDragged(double, double, double, double, int)}
	 * to keep track of decimal scroll drag values.
	 */
	protected double scrollDragX, scrollDragY;
	
	/**
	 * Used by the smooth scrolling feature to smoothly scroll this panel.
	 */
	protected double scrollVelocityX, scrollVelocityY;
	// --------------------------------------------------
	private final TPanelEvents __events = new TPanelEvents(this);
	// ==================================================
	public TPanelElement(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.topmosts = getTChildren().getTopmostElements();
		setScrollFlags(0);
		setScrollSensitivity(20);
		setScrollPadding(10);
		setSmoothScroll(false);
		this.smoothScrollSpeed = 1;
	}
	public @Override TPanelEvents getEvents() { return this.__events; }
	public @Override boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return !gainingFocus; }
	// ==================================================
	public @Override void setPosition(int x, int y, int flags)
	{
		//obtain differences
		int dx = x - this.x;
		int dy = y - this.y;
		
		//call super
		super.setPosition(x, y, flags);
		
		//handle drag scrolling on dynamic elements
		if(isBeingDragged())
			mouseDragged(screen.getMouseX(), screen.getMouseY(), -dx, -dy, 0);
	}
	// ==================================================
	/**
	 * Defines the scroll behavior for this {@link TPanelElement}.<br/>
	 * See {@link #scrollFlags}.
	 * @param flags The scroll settings.
	 */
	public void setScrollFlags(int flags) { this.scrollFlags = flags; }
	
	/**
	 * Returns the scroll behavior for this {@link TPanelElement}.<br/>
	 * See {@link #scrollFlags}.
	 */
	public int getScrollFlags() { return this.scrollFlags; }
	
	public void setScrollPadding(int padding) { this.scrollPadding = padding; }
	public int getScrollPadding() { return this.scrollPadding; }
	// --------------------------------------------------
	/**
	 * Returns the input {@link #scrollSensitivity}.<br/>
	 * See {@link #scrollSensitivity}.
	 */
	public int getScrollSensitivity() { return this.scrollSensitivity; }
	
	/**
	 * Sets the input {@link #scrollSensitivity}.<br/>
	 * See {@link #scrollSensitivity}.
	 * @param value The sensitivity value.
	 */
	public void setScrollSensitivity(int value) { this.scrollSensitivity = value; }
	// --------------------------------------------------
	/**
	 * When true, the mouse scrolling will be smooth.
	 */
	public boolean getSmoothScroll() { return MinecraftClient.isFancyGraphicsOrBetter() &&  this.smoothScroll; }
	
	/**
	 * Sets {@link #getSmoothScroll()}.
	 * @param smoothScroll Whether or not scrolling will be smooth.
	 */
	public void setSmoothScroll(boolean smoothScroll) { this.smoothScroll = smoothScroll; }
	// ==================================================
	/**
	 * Used to quickly check for a scroll flag in {@link #scrollFlags}.
	 * @param flagToCheck For example {@link #SCROLL_HORIZONTAL}.
	 */
	protected boolean sFlag(int flagToCheck) { return (this.scrollFlags & flagToCheck) == flagToCheck; }
	
	/**
	 * Returns true if this {@link TPanelElement} has
	 * scrolling enabled.
	 */
	public final boolean isScrollable() { return sFlag(SCROLL_VERTICAL) || sFlag(SCROLL_HORIZONTAL); }
	// --------------------------------------------------
	/**
	 * Used for inputting scroll input from a mouse wheel.
	 * @param scrollAmount The input scroll amount.
	 */
	public boolean inputScroll(int scrollAmount)
	{
		//for vertical and both, scroll vertically
		if(sFlag(SCROLL_VERTICAL) || sFlag(SCROLL_BOTH))
			return inputVerticalScroll(scrollAmount);
		//for horizontal, scroll horizontally
		else if(sFlag(SCROLL_HORIZONTAL))
			return inputHorizontalScroll(scrollAmount);
		//for none, do nothing
		else return false;
	}
	
	/**
	 * Used for inputting scroll input from a mouse drag.
	 * @param xScrollAmount The X scroll amount.
	 * @param yScrollAmount The Y scroll amount.
	 */
	public boolean inputScroll(int xScrollAmount, int yScrollAmount)
	{
		if(sFlag(SCROLL_BOTH)) return inputHorizontalScroll(xScrollAmount) | inputVerticalScroll(yScrollAmount);
		else if(sFlag(SCROLL_VERTICAL)) return inputVerticalScroll(yScrollAmount);
		else if(sFlag(SCROLL_HORIZONTAL)) return inputHorizontalScroll(xScrollAmount);
		return false;
	}
	
	public boolean inputHorizontalScroll(int scrollAmount)
	{
		//make sure topmost elements are full
		if(!sFlag(SCROLL_HORIZONTAL) || !topmosts.isFull() || scrollAmount == 0) return false;
		
		//calculate
		if(scrollAmount > 0)
		{
			int a = topmosts.Item3.getTpeX();
			int b = getTpeX() + getScrollPadding();
			int c = -(a - b);
			if(scrollAmount > c)
				scrollAmount = Math.max(c, 0);
		}
		else
		{
			int a = topmosts.Item4.getTpeEndX();
			int b = getTpeX() + getTpeWidth() - getScrollPadding();
			int c = (b - a);
			if(scrollAmount < c)
				scrollAmount = Math.min(c, 0);
		}
		
		//scroll
		if(scrollAmount == 0) return false;
		moveChildren(scrollAmount, 0);
		
		//invoke event and return
		final int delta = scrollAmount;
		getEvents().SCROLL_H.p_invoke(handler -> handler.accept(delta));
		return true;
	}
	
	public boolean inputVerticalScroll(int scrollAmount)
	{
		//make sure topmost elements are full
		if(!sFlag(SCROLL_VERTICAL) || !topmosts.isFull() || scrollAmount == 0) return false;
		
		//calculate
		if(scrollAmount > 0)
		{
			int a = topmosts.Item1.getTpeY();
			int b = getTpeY() + getScrollPadding();
			int c = -(a - b);
			if(scrollAmount > c)
				scrollAmount = Math.max(c, 0);
		}
		else
		{
			int a = topmosts.Item2.getTpeEndY();
			int b = getTpeY() + getTpeHeight() - getScrollPadding();
			int c = (b - a);
			if(scrollAmount < c)
				scrollAmount = Math.min(c, 0);
		}
		
		//scroll
		if(scrollAmount == 0) return false;
		final int delta = scrollAmount;
		moveChildren(0, delta);
		
		//invoke event and return
		getEvents().SCROLL_V.p_invoke(handler -> handler.accept(delta));
		return true;
	}
	// --------------------------------------------------
	@Nullable
	private Tuple2<Point, Point> __getHorizontalSeCnvElm()
	{
		if(!topmosts.isFull()) return null;
		Point seCnv = new Point(getTpeX() + getScrollPadding(), getTpeEndX() - getScrollPadding());
		Point seElm = new Point(topmosts.Item3.getTpeX(), topmosts.Item4.getTpeEndX());
		return new Tuple2<Point, Point>(seCnv, seElm);
	}
	
	@Nullable
	private Tuple2<Point, Point> __getVerticalSeCnvElm()
	{
		if(!topmosts.isFull()) return null;
		Point seCnv = new Point(getTpeY() + getScrollPadding(), getTpeEndY() - getScrollPadding());
		Point seElm = new Point(topmosts.Item1.getTpeY(), topmosts.Item2.getTpeEndY());
		return new Tuple2<Point, Point>(seCnv, seElm);
	}
	// --------------------------------------------------
	/**
	 * Calculates and returns the horizontal
	 * scroll amount. Do not call this frequently.
	 */
	public double getHorizontalScroll()
	{
		if(!topmosts.isFull()) return 0;
		double distFrom0 = (getTpeX() + getScrollPadding()) - topmosts.Item3.getTpeX();
		double distFrom1 = (getTpeEndX() - getScrollPadding()) - topmosts.Item4.getTpeEndX();
		if(distFrom0 <= 0) return 0;
		else if(distFrom1 >= 0) return 1;
		distFrom0 = Math.abs(distFrom0);
		distFrom1 = Math.abs(distFrom1);
		return distFrom0 / (distFrom0 + distFrom1);
	}
	
	/**
	 * Calculates and returns the vertical
	 * scroll amount. Do not call this frequently.
	 */
	public double getVerticalScroll()
	{
		if(!topmosts.isFull()) return 0;
		double distFrom0 = (getTpeY() + getScrollPadding()) - topmosts.Item1.getTpeY();
		double distFrom1 = (getTpeEndY() - getScrollPadding()) - topmosts.Item2.getTpeEndY();
		if(distFrom0 <= 0) return 0;
		else if(distFrom1 >= 0) return 1;
		distFrom0 = Math.abs(distFrom0);
		distFrom1 = Math.abs(distFrom1);
		return distFrom0 / (distFrom0 + distFrom1);
	}
	// --------------------------------------------------
	/**
	 * Sets the horizontal scroll amount.<br/>
	 * See {@link #getHorizontalScroll()}.
	 * @param amount01 The scroll amount in ranging from 0 to 1.
	 */
	public void setHorizontalScroll(double amount01)
	{
		if(!topmosts.isFull()) return;
		//clamp the amount value
		amount01 = MathHelper.clamp(amount01, 0, 1);
		int moveBy = getTpeX() + getScrollPadding() - topmosts.Item3.getTpeX(); //calculate this
		//calculate how much to move based on amount, and then add that to moveBy
		{
			Tuple2<Point, Point> seCnvElm = __getHorizontalSeCnvElm();
			seCnvElm.Item1.y -= seCnvElm.Item1.x; seCnvElm.Item1.x = 0;
			seCnvElm.Item2.y -= seCnvElm.Item2.x; seCnvElm.Item2.x = 0;
			
			int cnvW = seCnvElm.Item1.x + seCnvElm.Item1.y;
			int elmW = seCnvElm.Item2.x + seCnvElm.Item2.y;
			
			double d0 = elmW - cnvW;
			if(d0 > 0) moveBy -= (int)(d0 * amount01);
		}
		//move the children
		//inputHorizontalScroll(moveBy); -- performance
		{
			final int delta = moveBy;
			moveChildren(delta, 0);
			getEvents().SCROLL_H.p_invoke(handler -> handler.accept(delta));
		}
	}
	
	/**
	 * Sets the vertical scroll amount.<br/>
	 * See {@link #getVerticalScroll()}.
	 * @param amount01 The scroll amount in ranging from 0 to 1.
	 */
	public void setVerticalScroll(double amount01)
	{
		if(!topmosts.isFull()) return;
		//clamp the amount value
		amount01 = MathHelper.clamp(amount01, 0, 1);
		int moveBy = getTpeY() + getScrollPadding() - topmosts.Item3.getTpeY(); //calculate this
		//calculate how much to move based on amount, and then add that to moveBy
		{
			Tuple2<Point, Point> seCnvElm = __getVerticalSeCnvElm();
			seCnvElm.Item1.y -= seCnvElm.Item1.x; seCnvElm.Item1.x = 0;
			seCnvElm.Item2.y -= seCnvElm.Item2.x; seCnvElm.Item2.x = 0;
			
			int cnvW = seCnvElm.Item1.x + seCnvElm.Item1.y;
			int elmW = seCnvElm.Item2.x + seCnvElm.Item2.y;
			
			double d0 = elmW - cnvW;
			if(d0 > 0) moveBy -= (int)(d0 * amount01);
		}
		//move the children
		//inputVerticalScroll(moveBy); -- performance
		{
			final int delta = moveBy;
			moveChildren(0, delta);
			getEvents().SCROLL_V.p_invoke(handler -> handler.accept(delta));
		}
	}
	// --------------------------------------------------
	/**
	 * Returns what should be the new knob size for
	 * a horizontal {@link TScrollBarWidget}. Ranges
	 * from 0 to 1, one being the maximum knob size.
	 */
	public double getHorizontalScrollKnobSize01()
	{
		Tuple2<Point, Point> seCnvElm = __getHorizontalSeCnvElm();
		if(seCnvElm == null) return 1;
		double cnvW = Math.abs(seCnvElm.Item1.x - seCnvElm.Item1.y);
		double elmW = Math.abs(seCnvElm.Item2.x - seCnvElm.Item2.y);
		if(cnvW > elmW) return 1;
		return MathHelper.clamp(cnvW / Math.max(elmW, 1), 0, 1);
	}
	
	/**
	 * Returns what should be the new knob size for
	 * a vertical {@link TScrollBarWidget}. Ranges
	 * from 0 to 1, one being the maximum knob size.
	 */
	public double getVerticalScrollKnobSize01()
	{
		Tuple2<Point, Point> seCnvElm = __getVerticalSeCnvElm();
		if(seCnvElm == null) return 1;
		double cnvW = Math.abs(seCnvElm.Item1.x - seCnvElm.Item1.y);
		double elmW = Math.abs(seCnvElm.Item2.x - seCnvElm.Item2.y);
		if(cnvW > elmW) return 1;
		return MathHelper.clamp(cnvW / Math.max(elmW, 1), 0, 1);
	}
	// ==================================================
	/**
	 * Input enough horizontal and vertical scroll to make
	 * a specific child {@link TElement} visible on the viewport.<br/>
	 * <br/>
	 * <b>Note 1:</b> No scrolling will be done if the child is already
	 * visible within the bounds of the viewport.<br/>
	 * <b>Note 2:</b> The child doesn't actually have to be a direct
	 * child of this panel, it can be a nested child as well.
	 * @param child The child {@link TElement} to scroll to.
	 */
	public void scrollToChild(TElement child)
	{
		//null check
		if(child == null) return;
		
		//define scroll values
		int inputX = 0, inputY = 0;
		
		//----- calculate scroll values
		//horizontal
		{
			var ceX = child.getTpeEndX();
			var teX = this.getTpeEndX() - this.getScrollPadding();
			var diffA = ceX - teX;
			if(diffA > 0) inputX = -diffA;
			
			var cX = child.getTpeX();
			var tX = this.getTpeX() + this.getScrollPadding();
			var diffB = cX - tX;
			if(diffB < 0) inputX = -diffB;
		}
		//vertical
		{
			var ceY = child.getTpeEndY();
			var teY = this.getTpeEndY() - this.getScrollPadding();
			var diffA = ceY - teY;
			if(diffA > 0) inputY = -diffA;
			
			var cY = child.getTpeY();
			var tY = this.getTpeY() + this.getScrollPadding();
			var diffB = cY - tY;
			if(diffB < 0) inputY = -diffB;
		}
		
		//scroll
		if(inputX != 0 || inputY != 0)
			inputScroll(inputX, inputY);
	}
	// --------------------------------------------------
	/**
	 * Applies scroll values from {@link #scrollDragX} and {@link #scrollDragY}.
	 * @return True if the scroll drag values had any scroll to apply, and were then applied.
	 */
	protected boolean applyScrollDrag()
	{
		//get the FLOOR value of the totals
		int sdX = (int)this.scrollDragX;
		int sdY = (int)this.scrollDragY;
		
		//this part is important.
		//continuing past this point would reset the values
		if(sdX == 0 && sdY == 0) return false;
		
		//subtract the FLOOR totals from the totals
		this.scrollDragX -= sdX;
		this.scrollDragY -= sdY;
		
		//input the scroll values
		boolean h = inputHorizontalScroll(sdX);
		boolean v = inputVerticalScroll(sdY);
		if(sdX != 0 && !h)
		{
			this.scrollDragX = 0;
			this.scrollVelocityX = 0;
		}
		if(sdY != 0 && !v)
		{
			this.scrollDragY = 0;
			this.scrollVelocityY = 0;
		}
		return h || v;
	}
	// ==================================================
	/**
	 * Renders the background for this {@link TPanelElement}.
	 * By default, it fills a rectangle, but you may override it and make it do something else.<br/>
	 * Needs to be called manually from {@link #render(MatrixStack, int, int, float)}.
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the {@link Screen}.
	 * @param mouseY The Y mouse cursor position on the {@link Screen}.
	 * @param deltaTime The time elapsed since the last render.
	 */
	protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, GuiUtils.applyAlpha(1342177280, getAlpha()));
	}
	
	/**
	 * Handles smooth scrolling. Needs to be called once
	 * every frame from {@link #render(MatrixStack, int, int, float)}.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	protected void renderSmoothScroll(float deltaTime)
	{
		//handle time & smooth scrolling
		//(only works when not being dragged. we don't want conflicting features)
		if(!(getSmoothScroll() && !isBeingDragged())) return;
		
		//vertical
		if(this.scrollVelocityY != 0)
		{
			double sign = this.scrollVelocityY * deltaTime * Math.abs(this.smoothScrollSpeed) * 0.5;
			this.scrollVelocityY -= sign;
			this.scrollDragY += sign;
			if(Math.abs(this.scrollVelocityY) <= Math.abs(sign))
				this.scrollVelocityY = 0;
		}
		//horizontal
		if(this.scrollVelocityX != 0)
		{
			double sign = this.scrollVelocityX * deltaTime * Math.abs(this.smoothScrollSpeed) * 0.5;
			this.scrollVelocityX -= sign;
			this.scrollDragX += sign;
			if(Math.abs(this.scrollVelocityX) <= Math.abs(sign))
				this.scrollVelocityX = 0;
		}
		
		applyScrollDrag();
	}
	
	public @Override void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		renderBackground(matrices, mouseX, mouseY, deltaTime);
		renderSmoothScroll(deltaTime);
	}
	
	public @Override void postRender(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		drawOutline(matrices, GuiUtils.applyAlpha(isFocused() ? COLOR_OUTLINE_FOCUSED : COLOR_OUTLINE, getAlpha()));
	}
	// ==================================================
	@Override
	public boolean mousePressed(int mouseX, int mouseY, int button)
	{
		this.scrollDragX = 0;
		this.scrollDragY = 0;
		this.scrollVelocityX = 0;
		this.scrollVelocityY = 0;
		return super.mousePressed(mouseX, mouseY, button);
	}
	
	public @Override boolean mouseDragged(double mouseX, double mouseY, double deltaX, double deltaY, int button)
	{
		//add delta to the total
		this.scrollDragX += deltaX;
		this.scrollDragY += deltaY;
		//apply
		return applyScrollDrag();
	}
	
	public @Override boolean mouseScrolled(int mouseX, int mouseY, int amount)
	{
		//do nothing when being drag-scrolled
		if(isBeingDragged()) return false;
		
		//handle the mouse scroll
		int scrollAmount = amount * getScrollSensitivity();
		
		//for vertical and both, scroll vertically
		if(sFlag(SCROLL_VERTICAL) || sFlag(SCROLL_BOTH))
		{
			if(getSmoothScroll()) { this.scrollVelocityY += scrollAmount; return true; }
			return inputVerticalScroll(scrollAmount);
		}
		//for horizontal, scroll horizontally
		else if(sFlag(SCROLL_HORIZONTAL))
		{
			if(getSmoothScroll()) { this.scrollVelocityX += scrollAmount; return true; }
			return inputHorizontalScroll(scrollAmount);
		}
		//for none, do nothing
		else return false;
	}
	// --------------------------------------------------
	public @Override boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//265 - up arrow
		//264 - down arrow
		//263 - left arrow
		//262 - right arrow
		boolean b0 = false;
		if(keyCode == 265) b0 = b0 || inputVerticalScroll(-scrollSensitivity);
		else if(keyCode == 264) b0 = b0 || inputVerticalScroll(scrollSensitivity);
		if(keyCode == 263) b0 = b0 || inputHorizontalScroll(-scrollSensitivity);
		else if(keyCode == 262) b0 = b0 || inputHorizontalScroll(scrollSensitivity);
		return b0;
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.panel;

import java.awt.Point;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TElementList;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputDiscoveryPhase;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputType;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.math.Tuple2;
import io.github.thecsdev.tcdcommons.api.util.math.Tuple4;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public @Virtual class TPanelElement extends TElement
{
	// ==================================================
	public static final int COLOR_OUTLINE = 1358954495;
	public static final int COLOR_OUTLINE_FOCUSED = -5570561;
	public static final int COLOR_BACKGROUND = 1342177280;
	
	/**
	 * A scroll flag used to define the scrolling behavior
	 * for {@link #setScrollFlags(int)}.
	 */
	public static final int SCROLL_HORIZONTAL = 0b010,
							SCROLL_VERTICAL   = 0b100,
							SCROLL_BOTH       = SCROLL_HORIZONTAL | SCROLL_VERTICAL;
	// ==================================================
	/**
	 * A shortcut for the topmost elements of this {@link TPanelElement}.<br/>
	 * See {@link TElementList#getTopmostElements()}.
	 */
	protected final Tuple4<TElement, TElement, TElement, TElement> topmosts;
	// --------------------------------------------------
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
	//protected boolean isBeingDragged;
	
	/**
	 * Used by {@link TInputContext.InputType#MOUSE_DRAG} inputs
	 * to keep track of decimal scroll drag values.
	 */
	protected double scrollDragX, scrollDragY;
	
	/**
	 * Used by the smooth scrolling feature to smoothly scroll this panel.
	 */
	protected double scrollVelocityX, scrollVelocityY;
	// --------------------------------------------------
	protected int backgroundColor, outlineColor;
	// --------------------------------------------------
	public final TEvent<TPanelElementEvent_Scrolled> eScrolledHorizontally = TEventFactory.createLoop();
	public final TEvent<TPanelElementEvent_Scrolled> eScrolledVertically = TEventFactory.createLoop();
	// ==================================================
	public TPanelElement(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		@SuppressWarnings("removal")
		final var t = getChildren().getTopmostElements();
		this.topmosts = t;
		
		this.scrollFlags = 0;
		this.scrollSensitivity = 20;
		this.scrollPadding = 0;
		this.smoothScroll = false;
		this.smoothScrollSpeed = 1;
		
		this.scrollDragX = 0;
		this.scrollDragY = 0;
		this.scrollVelocityX = 0;
		this.scrollVelocityY = 0;
		
		this.backgroundColor = COLOR_BACKGROUND;
		this.outlineColor = COLOR_OUTLINE;
	}
	// --------------------------------------------------
	public final int getBackgroundColor() { return this.backgroundColor; }
	public @Virtual void setBackgroundColor(int color) { this.backgroundColor = color; }
	//
	public final int getOutlineColor() { return this.outlineColor; }
	public @Virtual void setOutlineColor(int color) { this.outlineColor = color; }
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil)
	{
		pencil.drawTFill(this.backgroundColor);
		renderSmoothScroll(pencil.deltaTime);
	}
	public @Virtual @Override void postRender(TDrawContext pencil)
	{
		if(isFocused()) pencil.drawTBorder(COLOR_OUTLINE_FOCUSED);
		else pencil.drawTBorder(this.outlineColor);
	}
	
	/**
	 * Handles smooth scrolling. Needs to be called once every frame from {@link #render(TDrawContext)}.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	protected final void renderSmoothScroll(float deltaTime)
	{
		//handle time & smooth scrolling
		//(only works when not being dragged. we don't want conflicting features)
		if(!(getSmoothScroll() && !isDragging())) return;
		
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
	// ==================================================
	/**
	 * Used to quickly check for a scroll flag in {@link #scrollFlags}.
	 * @param flagToCheck For example {@link #SCROLL_HORIZONTAL}.
	 */
	public final boolean hasScrollFlag(int flagToCheck) { return (this.scrollFlags & flagToCheck) == flagToCheck; }
	
	/**
	 * Returns true if this {@link TPanelElement} has
	 * scrolling enabled.
	 */
	public final boolean isScrollable() { return hasScrollFlag(SCROLL_VERTICAL) || hasScrollFlag(SCROLL_HORIZONTAL); }
	// --------------------------------------------------
	/**
	 * Returns the scroll behavior for this {@link TPanelElement}.<br/>
	 * See {@link #scrollFlags}.
	 */
	public final int getScrollFlags() { return this.scrollFlags; }
	
	/**
	 * Defines the scroll behavior for this {@link TPanelElement}.<br/>
	 * See {@link #scrollFlags}.
	 * @param flags The scroll settings.
	 */
	public @Virtual void setScrollFlags(int flags) { this.scrollFlags = flags; }
	// --------------------------------------------------
	/**
	 * Returns the input {@link #scrollSensitivity}.<br/>
	 * See {@link #scrollSensitivity}.
	 */
	public final int getScrollSensitivity() { return this.scrollSensitivity; }
	
	/**
	 * Sets the input {@link #scrollSensitivity}.<br/>
	 * See {@link #scrollSensitivity}.
	 * @param value The sensitivity value.
	 */
	public @Virtual void setScrollSensitivity(int value) { this.scrollSensitivity = value; }
	// --------------------------------------------------
	/**
	 * When true, the mouse scrolling will be smooth.
	 */
	public final boolean getSmoothScroll() { return MinecraftClient.isFancyGraphicsOrBetter() && this.smoothScroll; }
	
	/**
	 * Sets {@link #getSmoothScroll()}.
	 * @param smoothScroll Whether or not scrolling will be smooth.
	 */
	public @Virtual void setSmoothScroll(boolean smoothScroll) { this.smoothScroll = smoothScroll; }
	// --------------------------------------------------
	//semi-deprecated; was used in v2.x; use nested panels instead
	public final @Beta int getScrollPadding() { return this.scrollPadding; }
	public @Virtual @Beta void setScrollPadding(int scrollPadding) { this.scrollPadding = scrollPadding; }
	// ==================================================
	/**
	 * Calculates and returns the horizontal scroll amount.
	 * @apiNote Costs performance. Do not call this frequently.
	 */
	public final double getHorizontalScrollAmount()
	{
		if(!topmosts.isFull()) return 0;
		double distFrom0 = (getX() + getScrollPadding()) - topmosts.Item3.getX();
		double distFrom1 = (getEndX() - getScrollPadding()) - topmosts.Item4.getEndX();
		if(distFrom0 <= 0) return 0;
		else if(distFrom1 >= 0) return 1;
		distFrom0 = Math.abs(distFrom0);
		distFrom1 = Math.abs(distFrom1);
		return distFrom0 / (distFrom0 + distFrom1);
	}
	
	/**
	 * Calculates and returns the vertical scroll amount.
	 * @apiNote Costs performance. Do not call this frequently.
	 */
	public final double getVerticalScrollAmount()
	{
		if(!topmosts.isFull()) return 0;
		double distFrom0 = (getY() + getScrollPadding()) - topmosts.Item1.getY();
		double distFrom1 = (getEndY() - getScrollPadding()) - topmosts.Item2.getEndY();
		if(distFrom0 <= 0) return 0;
		else if(distFrom1 >= 0) return 1;
		distFrom0 = Math.abs(distFrom0);
		distFrom1 = Math.abs(distFrom1);
		return distFrom0 / (distFrom0 + distFrom1);
	}
	// --------------------------------------------------
	/**
	 * Get horizontal start-end {@link Point}s of canvas and elements bounding boxes.<br/>
	 * Used for scroll calculations.
	 * @return The bounding-box start-end {@link Point}s for the canvas and its elements.
	 */
	protected final @Internal @Nullable Tuple2<Point, Point> __getHorizontalSeCnvElm()
	{
		if(!topmosts.isFull()) return null;
		Point seCnv = new Point(getX() + getScrollPadding(), getEndX() - getScrollPadding());
		Point seElm = new Point(topmosts.Item3.getX(), topmosts.Item4.getEndX());
		return new Tuple2<Point, Point>(seCnv, seElm);
	}
	
	/**
	 * Get vertical start-end {@link Point}s of canvas and elements bounding boxes.<br/>
	 * Used for scroll calculations.
	 * @return The bounding-box start-end {@link Point}s for the canvas and its elements.
	 */
	protected final @Internal @Nullable Tuple2<Point, Point> __getVerticalSeCnvElm()
	{
		if(!topmosts.isFull()) return null;
		Point seCnv = new Point(getY() + getScrollPadding(), getEndY() - getScrollPadding());
		Point seElm = new Point(topmosts.Item1.getY(), topmosts.Item2.getEndY());
		return new Tuple2<Point, Point>(seCnv, seElm);
	}
	// --------------------------------------------------
	/**
	 * Sets the horizontal scroll amount.<br/>
	 * @param amount01 The scroll amount in ranging from 0 to 1.
	 * @see #getHorizontalScrollAmount()
	 * @apiNote Costs performance. Do not call this frequently.
	 */
	public final void setHorizontalScrollAmount(double amount01)
	{
		if(!topmosts.isFull()) return;
		//clamp the amount value
		amount01 = MathHelper.clamp(amount01, 0, 1);
		int moveBy = getX() + getScrollPadding() - topmosts.Item3.getX(); //calculate this
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
			this.eScrolledHorizontally.invoker().invoke(this, delta);
		}
	}
	
	/**
	 * Sets the vertical scroll amount.
	 * @param amount01 The scroll amount in ranging from 0 to 1.
	 * @see #getVerticalScrollAmount()
	 * @apiNote Costs performance. Do not call this frequently.
	 */
	public final void setVerticalScrollAmount(double amount01)
	{
		if(!topmosts.isFull()) return;
		//clamp the amount value
		amount01 = MathHelper.clamp(amount01, 0, 1);
		int moveBy = getY() + getScrollPadding() - topmosts.Item1.getY(); //calculate this
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
			this.eScrolledVertically.invoker().invoke(this, delta);
		}
	}
	// --------------------------------------------------
	/**
	 * Returns what should be the new knob size for a horizontal {@link TScrollBarWidget}.
	 * <br/>Ranges from 0 to 1, one being the maximum knob size.
	 */
	public final double getHorizontalScrollKnobSize01()
	{
		Tuple2<Point, Point> seCnvElm = __getHorizontalSeCnvElm();
		if(seCnvElm == null) return 1;
		double cnvW = Math.abs(seCnvElm.Item1.x - seCnvElm.Item1.y);
		double elmW = Math.abs(seCnvElm.Item2.x - seCnvElm.Item2.y);
		if(cnvW > elmW) return 1;
		return MathHelper.clamp(cnvW / Math.max(elmW, 1), 0, 1);
	}
	
	/**
	 * Returns what should be the new knob size for a vertical {@link TScrollBarWidget}.
	 * <br/>Ranges from 0 to 1, one being the maximum knob size.
	 */
	public final double getVerticalScrollKnobSize01()
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
	 * a specific child {@link TElement} visible on the viewport.
	 * @param child The child {@link TElement} to scroll to.
	 * @apiNote No scrolling will be done if the child is already
	 * visible within the bounds of the viewport.
	 * @apiNote The child doesn't actually have to be a direct child
	 * of this panel, it can be a nested child as well.
	 */
	public final void scrollToChild(TElement child)
	{
		//null check
		if(child == null) return;
		
		//define scroll values
		int inputX = 0, inputY = 0;
		
		//----- calculate scroll values
		//horizontal
		{
			var ceX = child.getEndX();
			var teX = this.getEndX() - this.getScrollPadding();
			var diffA = ceX - teX;
			if(diffA > 0) inputX = -diffA;
			
			var cX = child.getX();
			var tX = this.getX() + this.getScrollPadding();
			var diffB = cX - tX;
			if(diffB < 0) inputX = -diffB;
		}
		//vertical
		{
			var ceY = child.getEndY();
			var teY = this.getEndY() - this.getScrollPadding();
			var diffA = ceY - teY;
			if(diffA > 0) inputY = -diffA;
			
			var cY = child.getY();
			var tY = this.getY() + this.getScrollPadding();
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
	protected final boolean applyScrollDrag()
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
	public @Virtual @Override boolean input(TInputContext inputContext, InputDiscoveryPhase inputPhase)
	{
		//essentials
		if(inputPhase == InputDiscoveryPhase.BROADCAST &&
			inputContext.getInputType() == InputType.MOUSE_RELEASE &&
			inputContext.getMouseButton() == 0)
		{
			//clear drag flags when the mouse releases (do not return from here)
			this.scrollDragX = 0;
			this.scrollDragY = 0;
		}
		//return super
		return super.input(inputContext, inputPhase);
	}
	// --------------------------------------------------
	public @Virtual @Override boolean input(TInputContext inputContext)
	{
		//respect super
		if(super.input(inputContext))
			return true;
		//and make sure the parent screen is in place
		else if(getParentTScreen() == null)
			return false;
		
		//don't handle keyboard-related inputs if this element isn't focused
		if(inputContext.getInputType().isKeyboardRelated())
			if(getParentTScreen().getFocusedElement() != this)
				return false;
		
		//handle scrolling based on user input
		switch(inputContext.getInputType())
		{
			case MOUSE_SCROLL:
				//do nothing when being drag-scrolled
				if(isDragging()/*this.isBeingDragged*/) break;
				//input scroll
				final var amt = inputContext.getScrollAmount();
				amt.x *= this.scrollSensitivity;
				amt.y *= this.scrollSensitivity;
				if(this.smoothScroll) return inputSmoothScroll((int)amt.x, (int)amt.y);
				else return inputScroll((int)amt.x, (int)amt.y);
			case MOUSE_PRESS:
				//make sure it's LMB
				if(inputContext.getMouseButton() != 0) break;
				//return true to allow mouse dragging
				return true;
			case MOUSE_DRAG:
				//do nothing if not being dragged
				if(!isDragging()) break;
				//add delta to the total
				if(hasScrollFlag(SCROLL_HORIZONTAL))
					this.scrollDragX += inputContext.getMouseDelta().x;
				if(hasScrollFlag(SCROLL_VERTICAL))
					this.scrollDragY += inputContext.getMouseDelta().y;
				//apply drag
				return applyScrollDrag();
			case KEY_PRESS:
				//input scroll based on the pressed key
				//265 - up arrow; 264 - down arrow; 263 - left arrow; 262 - right arrow
				final var keyCode = inputContext.getKeyboardKey().keyCode;
				boolean b0 = false;
				if(keyCode == 265) b0 = b0 || inputVerticalScroll(-scrollSensitivity);
				else if(keyCode == 264) b0 = b0 || inputVerticalScroll(scrollSensitivity);
				if(keyCode == 263) b0 = b0 || inputHorizontalScroll(-scrollSensitivity);
				else if(keyCode == 262) b0 = b0 || inputHorizontalScroll(scrollSensitivity);
				return b0;
			default: break;
		}
		return false;
	}
	// --------------------------------------------------
	/**
	 * Same as {@link #inputScroll(int)}, but <i>smooth</i>.
	 * @param scrollAmount The input scroll amount.
	 */
	public final boolean inputSmoothScroll(int scrollAmount)
	{
		if(hasScrollFlag(SCROLL_VERTICAL) || hasScrollFlag(SCROLL_BOTH))
			return inputSmoothScroll(0, scrollAmount);
		else if(hasScrollFlag(SCROLL_HORIZONTAL))
			return inputSmoothScroll(scrollAmount, 0);
		else return false;
	}
	
	/**
	 * Same as {@link #inputScroll(int, int)}, but <i>smooth</i>.
	 * @param scrollAmount The input scroll amount.
	 */
	public final boolean inputSmoothScroll(int scrollAmountX, int scrollAmountY)
	{
		boolean b = false;
		//input vertically when has vertical flag
		if(hasScrollFlag(SCROLL_VERTICAL)) { this.scrollVelocityY += scrollAmountY; b = true; }
		//input horizontally when has horizontal flag
		if(hasScrollFlag(SCROLL_HORIZONTAL)) { this.scrollVelocityX += scrollAmountX; b = true; }
		//return true if the scroll was applied
		return b;
	}
	
	/**
	 * Used for inputting scroll input from a mouse wheel.
	 * <p>
	 * <i>Note: Unaffected by {@link #getScrollSensitivity()}.
	 * That only applies to {@link #input(TInputContext)}.</i>
	 * 
	 * @param scrollAmount The input scroll amount.
	 */
	public final boolean inputScroll(int scrollAmount)
	{
		//for vertical and both, scroll vertically
		if(hasScrollFlag(SCROLL_VERTICAL) || hasScrollFlag(SCROLL_BOTH))
			return inputVerticalScroll(scrollAmount);
		//for horizontal, scroll horizontally
		else if(hasScrollFlag(SCROLL_HORIZONTAL))
			return inputHorizontalScroll(scrollAmount);
		//for none, do nothing
		else return false;
	}
	
	/**
	 * Used for inputting scroll input from a mouse drag.
	 * <p>
	 * <i>Note: Unaffected by {@link #getScrollSensitivity()}.
	 * That only applies to {@link #input(TInputContext)}.</i>
	 * 
	 * @param xScrollAmount The X scroll amount.
	 * @param yScrollAmount The Y scroll amount.
	 */
	public final boolean inputScroll(int xScrollAmount, int yScrollAmount)
	{
		if(hasScrollFlag(SCROLL_BOTH)) return inputHorizontalScroll(xScrollAmount) | inputVerticalScroll(yScrollAmount);
		else if(hasScrollFlag(SCROLL_VERTICAL)) return inputVerticalScroll(yScrollAmount);
		else if(hasScrollFlag(SCROLL_HORIZONTAL)) return inputHorizontalScroll(xScrollAmount);
		return false;
	}
	// --------------------------------------------------
	/**
	 * Scrolls the {@link TPanelElement} horizontally.
	 * <p>
	 * <i>Note: Unaffected by {@link #getScrollSensitivity()}.
	 * That only applies to {@link #input(TInputContext)}.</i>
	 * 
	 * @param scrollAmount The scroll amount.
	 */
	public final boolean inputHorizontalScroll(int scrollAmount)
	{
		//make sure topmost elements are full
		if(!hasScrollFlag(SCROLL_HORIZONTAL) || !topmosts.isFull() || scrollAmount == 0) return false;
		
		//calculate
		if(scrollAmount > 0)
		{
			int a = topmosts.Item3.getX();
			int b = getX() + getScrollPadding();
			int c = -(a - b);
			if(scrollAmount > c)
				scrollAmount = Math.max(c, 0);
		}
		else
		{
			int a = topmosts.Item4.getEndX();
			int b = getX() + getWidth() - getScrollPadding();
			int c = (b - a);
			if(scrollAmount < c)
				scrollAmount = Math.min(c, 0);
		}
		
		//scroll
		if(scrollAmount == 0) return false;
		moveChildren(scrollAmount, 0);
		
		//invoke event and return
		this.eScrolledHorizontally.invoker().invoke(this, scrollAmount);
		return true;
	}
	
	/**
	 * Scrolls the {@link TPanelElement} vertically.
	 * <p>
	 * <i>Note: Unaffected by {@link #getScrollSensitivity()}.
	 * That only applies to {@link #input(TInputContext)}.</i>
	 * 
	 * @param scrollAmount The scroll amount.
	 */
	public final boolean inputVerticalScroll(int scrollAmount)
	{
		//make sure topmost elements are full
		if(!hasScrollFlag(SCROLL_VERTICAL) || !topmosts.isFull() || scrollAmount == 0) return false;
		
		//calculate
		if(scrollAmount > 0)
		{
			int a = topmosts.Item1.getY();
			int b = getY() + getScrollPadding();
			int c = -(a - b);
			if(scrollAmount > c)
				scrollAmount = Math.max(c, 0);
		}
		else
		{
			int a = topmosts.Item2.getEndY();
			int b = getY() + getHeight() - getScrollPadding();
			int c = (b - a);
			if(scrollAmount < c)
				scrollAmount = Math.min(c, 0);
		}
		
		//scroll
		if(scrollAmount == 0) return false;
		final int delta = scrollAmount;
		moveChildren(0, delta);
		
		//invoke event and return
		this.eScrolledVertically.invoker().invoke(this, delta);
		return true;
	}
	// ==================================================
	public static interface TPanelElementEvent_Scrolled { public void invoke(TPanelElement element, int scrollDelta); }
	// ==================================================
}
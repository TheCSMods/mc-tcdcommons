package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.SCROLL_HORIZONTAL;
import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.SCROLL_VERTICAL;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.TPanelElementEvent_Scrolled;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Direction2D;

public @Virtual class TScrollBarWidget extends TSliderWidget
{
	// ==================================================
	protected final TPanelElement target;
	protected boolean isValueDirty = false;
	protected boolean isKnobSizeDirty = false;
	// ==================================================
	public TScrollBarWidget(int x, int y, int width, int height, TPanelElement target) { this(x, y, width, height, target, true); }
	public TScrollBarWidget(int x, int y, int width, int height, TPanelElement target, boolean autoSetScrollFlags)
	{
		super(x, y, width, height, null, 0);
		this.target = Objects.requireNonNull(target);
		
		//try to smartly predict the slider direction based on dimensions
		if(autoSetScrollFlags)
		{
			final boolean b = (height >= width);
			final int sf = this.target.getScrollFlags();
			setSliderDirection(b ? Direction2D.DOWN : Direction2D.RIGHT);
			this.target.setScrollFlags(b ? (sf | SCROLL_VERTICAL) : (sf | SCROLL_HORIZONTAL));
		}
		
		//refresh the value here
		refreshValue();
		refreshKnobSize();
		
		//handle panel events
		final TPanelElementEvent_Scrolled onTargetScrollH = (element, scrollDelta) ->
		{
			if(this.getSliderDirection().isHorizontal())
				this.isValueDirty = true;
		};
		final TPanelElementEvent_Scrolled onTargetScrollV = (element, scrollDelta) ->
		{
			if(this.getSliderDirection().isVertical())
				this.isValueDirty = true;
		};
		final TElementEvent_ChildAR onTargetChildAR = (element, child, repositioned) ->
		{
			this.isKnobSizeDirty = true;
			this.isValueDirty = true;
		};
		
		this.target.eScrolledHorizontally.register(onTargetScrollH);
		this.target.eScrolledVertically.register(onTargetScrollV);
		this.target.eChildAdded.register(onTargetChildAR);
		this.target.eChildRemoved.register(onTargetChildAR);
	}
	// --------------------------------------------------
	public final TPanelElement getTarget() { return this.target; }
	// ==================================================
	public @Virtual @Override void tick()
	{
		super.tick();
		if(this.isKnobSizeDirty) refreshKnobSize();
		if(this.isValueDirty) refreshValue();
	}
	// --------------------------------------------------
	/**
	 * Refreshes this slider's {@link #getValue()} based on
	 * the {@link #getTarget()}'s current scroll value.
	 */
	public void refreshValue()
	{
		if(isDragging()) return;
		switch(getSliderDirection())
		{
			//ordered from most likely to least likely.
			//DO NOT apply value here. StackOverflow will take place if you do
			case RIGHT: setValue(getTarget().getHorizontalScrollAmount(), false); break;
			case DOWN: setValue(getTarget().getVerticalScrollAmount(), false); break;
			case UP: setValue(1 - getTarget().getVerticalScrollAmount(), false); break;
			case LEFT: setValue(1 - getTarget().getHorizontalScrollAmount(), false); break;
			default: break;
		}
	}
	
	/**
	 * Refreshes this slider's {@link #getKnobSize()} based on
	 * the {@link #getTarget()}'s elements.
	 */
	public void refreshKnobSize()
	{
		if(getSliderDirection().isHorizontal())
			setKnobSize((int)(getTarget().getHorizontalScrollKnobSize01() * (getWidth()/2)));
		else setKnobSize((int)(getTarget().getVerticalScrollKnobSize01() * (getHeight()/2)));
	}
	// --------------------------------------------------
	public @Virtual @Override void setValue(double value, boolean applyValue)
	{
		//first set the value as normal
		super.setValue(value, applyValue);
		//and then apply it to the scrollable panel
		if(applyValue) applyValueToScroll();
	}
	
	/**
	 * Applies the {@link #getValue()} to the {@link #getTarget()}'s scroll amount.
	 */
	protected final void applyValueToScroll()
	{
		//apply current value to the target panel
		final double val = getValue();
		switch(getSliderDirection())
		{
			//ordered from most likely to least likely
			case RIGHT: this.target.setHorizontalScrollAmount(val); break;
			case DOWN: this.target.setVerticalScrollAmount(val); break;
			case UP: this.target.setVerticalScrollAmount(1 - val); break;
			case LEFT: this.target.setHorizontalScrollAmount(1 - val); break;
			default: break;
		}
	}
	// ==================================================
	public @Virtual @Override boolean input(TInputContext inputContext)
	{
		//first forward event to `super`
		if(super.input(inputContext))
			return true;
			
		//handle input based on type
		switch(inputContext.getInputType())
		{
			//handle mouse scrolling
			case MOUSE_SCROLL:
				final int amountX = (int)inputContext.getScrollAmount().x;
				final int amountY = (int)inputContext.getScrollAmount().y;
				if(getSliderDirection().isHorizontal())
					return target.inputHorizontalScroll(target.getScrollSensitivity() * amountX);
				else return target.inputVerticalScroll(target.getScrollSensitivity() * amountY);
				
			//break for all other input types
			default: break;
		}
		
		//return false if not handled
		return false;
	}
	// ==================================================
}
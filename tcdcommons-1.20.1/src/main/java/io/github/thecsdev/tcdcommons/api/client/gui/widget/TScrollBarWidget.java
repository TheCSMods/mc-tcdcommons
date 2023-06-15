package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.TPanelElementEvent_Scroll;
import io.github.thecsdev.tcdcommons.api.client.gui.util.Direction2D;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.util.math.MathHelper;

public class TScrollBarWidget extends AbstractTSliderWidget
{
	// ==================================================
	protected final TPanelElement target;
	// --------------------------------------------------
	//hold references to the event handlers to prevent garbage collection
	//protected final @Deprecated Consumer<Integer> target_ehScrollH, target_ehScrollV;
	//protected final @Deprecated TriConsumer<TElement, Boolean, Boolean> target_ehChildAR;
	// ==================================================
	public TScrollBarWidget(int x, int y, int width, int height, TPanelElement target)
	{
		//base setup of super and of fields and settings
		super(x, y, width, height, 0);
		this.target = Objects.requireNonNull(target, "target must not be null.");
		setDrawMessage(false);
		
		//try to smartly predict the slider direction
		if(height >= width)
		{
			setSliderDirection(Direction2D.DOWN);
			this.target.setScrollFlags(this.target.getScrollFlags() | TPanelElement.SCROLL_VERTICAL);
		}
		else
		{
			setSliderDirection(Direction2D.RIGHT);
			this.target.setScrollFlags(this.target.getScrollFlags() | TPanelElement.SCROLL_HORIZONTAL);
		}
		
		//refresh the value here
		refreshValue();
		refreshKnobSize();
		
		//handle panel events
		final TPanelElementEvent_Scroll onTargetScrollH = (element, scrollDelta) ->
		{
			if(this.getSliderDirection().isHorizontal())
				this.refreshValue();
		};
		final TPanelElementEvent_Scroll onTargetScrollV = (element, scrollDelta) ->
		{
			if(this.getSliderDirection().isVertical())
				this.refreshValue();
		};
		final TElementEvent_ChildAR onTargetChildAR = (element, child, repositioned) -> refreshKnobSize();
		
		this.target.eScrollHorizontally.register(onTargetScrollH);
		this.target.eScrollVertically.register(onTargetScrollV);
		this.target.eChildAdded.register(onTargetChildAR);
		this.target.eChildRemoved.register(onTargetChildAR);
		
		/*target_ehScrollH = target.getEvents().SCROLL_H.addWeakEventHandler((dX) ->
		{
			if(getSliderDirection().isHorizontal())
				refreshValue();
		});
		target_ehScrollV = target.getEvents().SCROLL_V.addWeakEventHandler((dY) ->
		{
			if(getSliderDirection().isVertical())
				refreshValue();
		});
		target_ehChildAR = target.getEvents().CHILD_AR
				.addWeakEventHandler((child, added, repositioned) -> refreshKnobSize());*/
	}
	
	/**
	 * Returns the {@link TPanelElement} that this
	 * {@link TScrollBarWidget} is targeting.
	 */
	public final TPanelElement getTarget() { return this.target; }
	// ==================================================
	/**
	 * Refreshes this slider's {@link #getValue()} based on
	 * the {@link #getTarget()}'s current scroll value.
	 */
	public void refreshValue()
	{
		if(isBeingDragged()) return;
		switch(getSliderDirection())
		{
			//ordered from most likely to least likely.
			//DO NOT apply value here. StackOverflow will take place if you do
			case RIGHT: setValue(getTarget().getHorizontalScroll(), false); break;
			case DOWN: setValue(getTarget().getVerticalScroll(), false); break;
			case UP: setValue(1 - getTarget().getVerticalScroll(), false); break;
			case LEFT: setValue(1 - getTarget().getHorizontalScroll(), false); break;
			default: break;
		}
	}
	
	/**
	 * Refreshes this slider's {@link #getKnobSize()} based on
	 * the {@link #getTParent()}'s elements.
	 */
	public void refreshKnobSize()
	{
		if(getSliderDirection().isHorizontal())
			setKnobSize((int)(getTarget().getHorizontalScrollKnobSize01() * (getTpeWidth()/2)));
		else setKnobSize((int)(getTarget().getVerticalScrollKnobSize01() * (getTpeHeight()/2)));
	}
	// --------------------------------------------------
	@Override
	protected void applyValue()
	{
		double val = getValue();
		switch(getSliderDirection())
		{
			//ordered from most likely to least likely
			case RIGHT: getTarget().setHorizontalScroll(val); break;
			case DOWN: getTarget().setVerticalScroll(val); break;
			case UP: getTarget().setVerticalScroll(1 - val);
			case LEFT: getTarget().setHorizontalScroll(1 - val); break;
			default: break;
		}
	}
	// --------------------------------------------------
	@Override
	protected void updateMessage()
	{
		String str = MathHelper.clamp((int)(getValue() * 100), 0, 100) + "%";
		setMessage(TextUtils.literal(str));
	}
	// ==================================================
	@Override
	public boolean mouseScrolled(int mouseX, int mouseY, int amount)
	{
		//return super.mouseScrolled(mouseX, mouseY, -amount);
		TPanelElement target = getTarget();
		if(getSliderDirection().isHorizontal())
			return target.inputHorizontalScroll(target.getScrollSensitivity() * amount);
		else return target.inputVerticalScroll(target.getScrollSensitivity() * amount);
	}
	// ==================================================
}
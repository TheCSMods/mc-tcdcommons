package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TBlankElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * A {@link TBlankElement} that listens to "child added/removed" events
 * for its parent element, and enforces specific layout rules to its siblings.
 */
public abstract class UILayout extends TBlankElement
{
	// ==================================================
	private final TElementEvent_ParentChanged ehParentChanged;
	private final TElementEvent_ChildAR ehSiblingAdded;
	private final TElementEvent_ChildAR ehSiblingRemoved;
	private final TElementEvent_Resized ehParentResized;
	// --------------------------------------------------
	/**
	 * Default "parent scroll padding" returned by {@link #getParentScrollPadding()}
	 * when the parent element is not a {@link TPanelElement}.
	 */
	protected int defaultPsp = 0;
	// ==================================================
	public UILayout()
	{
		//super
		super(0, 0, 0, 0);
		
		//sibling add/remove handlers
		this.ehSiblingAdded = (parent, child, repositioned) ->
		{
			//validate parent
			if(parent != UILayout.this.getParent()) return;
			//invoke sibling method
			UILayout.this.onSiblingAdded(child, repositioned);
		};
		this.ehSiblingRemoved = (parent, child, repositioned) ->
		{
			//validate parent
			if(parent != UILayout.this.getParent()) return;
			//invoke sibling method
			UILayout.this.onSiblingRemoved(child, repositioned);
		};
		
		//parent resize handler
		this.ehParentResized = (parent, oldWidth, oldHeight) ->
		{
			//validate parent
			if(parent != UILayout.this.getParent()) return;
			//invoke the method
			onParentResized(oldWidth, oldHeight);
		};
		
		//parent change handler
		this.ehParentChanged = (__, oldParent, newParent) ->
		{
			//remove sibling listeners from old parent
			if(oldParent instanceof TElement)
			{
				final var el = (TElement)oldParent;
				el.eChildAdded.unregister(this.ehSiblingAdded);
				el.eChildRemoved.unregister(this.ehSiblingRemoved);
				el.eResized.unregister(this.ehParentResized);
			}
			//add sibling listeners to the new parent
			if(newParent instanceof TElement)
			{
				final var el = (TElement)newParent;
				el.eChildAdded.register(this.ehSiblingAdded);
				el.eChildRemoved.register(this.ehSiblingRemoved);
				el.eResized.register(this.ehParentResized);
			}
			
			//update position
			setPosition(0, 0, 0);
		};
		this.eParentChanged.register(this.ehParentChanged);
	}
	// --------------------------------------------------
	public final @Override void setPosition(int x, int y, int flags)
	{
		final int sPadding = getParentScrollPadding();
		super.setPosition(sPadding, sPadding, SP_RELATIVE);
	}
	public final @Override void setSize(int width, int height, int flags) { super.setSize(0, 0, 0); }
	// ==================================================
	/**
	 * See {@link #defaultPsp} for more info.
	 */
	public final int getDefaultPSP() { return this.defaultPsp; }
	public final void setDefaultPSP(int defaultPsp) { this.defaultPsp = Math.abs(defaultPsp); }
	
	/**
	 * If the {@link #getParent()} is a {@link TPanelElement},
	 * returns {@link TPanelElement#getScrollPadding()}, and 0 otherwise.
	 */
	protected @Virtual int getParentScrollPadding()
	{
		final var parent = getParent();
		if(parent instanceof TPanelElement)
			return ((TPanelElement)parent).getScrollPadding();
		else return this.defaultPsp;
	}
	// --------------------------------------------------
	protected @Virtual void onSiblingAdded(TElement sibling, boolean repositioned) {}
	protected @Virtual void onSiblingRemoved(TElement sibling, boolean repositioned) {}
	protected @Virtual void onParentResized(int oldWidth, int oldHeight) {}
	// ==================================================
}
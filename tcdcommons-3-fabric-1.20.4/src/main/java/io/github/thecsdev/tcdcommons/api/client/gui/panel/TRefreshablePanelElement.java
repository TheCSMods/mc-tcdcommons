package io.github.thecsdev.tcdcommons.api.client.gui.panel;

import io.github.thecsdev.tcdcommons.api.client.gui.util.event.handler.TElementEvent_Runnable;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;

/**
 * A {@link TPanelElement} that features {@link #refresh()} and {@link #init()}.<p>
 * Use {@link #refresh()} to "refresh" this {@link TPanelElement}.
 * @see #refresh()
 * @see #init()
 */
public abstract class TRefreshablePanelElement extends TPanelElement
{
	// ==================================================
	public final TEvent<TElementEvent_Runnable<TRefreshablePanelElement>> eRefreshed = TEventFactory.createLoop();
	// --------------------------------------------------
	private final TElementEvent_ParentChanged ehParentChanged = (self, oldP, newP) ->
	{
		//when this element is added to a non-null parent that isn't a refreshable panel...
		if(newP != null && !(newP instanceof TRefreshablePanelElement))
			//...automatically refresh this panel
			refresh();
	};
	private final TElementEvent_Resized ehResized = (self, oldW, oldH) ->
	{
		if(getParent() != null)
			refresh();
	};
	// ==================================================
	public TRefreshablePanelElement(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.eParentChanged.register(this.ehParentChanged);
		this.eResized.register(this.ehResized);
	}
	// ==================================================
	/**
	 * Refreshes this {@link TRefreshablePanelElement} by clearing all children
	 * with {@link #clearChildren()}, and then calls {@link #init()}.
	 * <p>
	 * Additionally, any child {@link TRefreshablePanelElement}s will also be refreshed.
	 * 
	 * @apiNote Do not call {@link #refresh()} from within {@link #init()} on children
	 * that are also {@link TRefreshablePanelElement}s. Those children will automatically be refreshed.
	 */
	public final void refresh()
	{
		//make sure the event handler is properly registered
		if(!this.eParentChanged.isRegistered(this.ehParentChanged))
			this.eParentChanged.register(this.ehParentChanged);
		if(!this.eResized.isRegistered(this.ehResized))
			this.eResized.register(this.ehResized);
		//clear all children before (re)initializing
		clearChildren();
		//(re)initialize
		init();
		//iterate all children, and refresh and refreshable children
		for(final var child : getChildren())
			if(child instanceof TRefreshablePanelElement)
				((TRefreshablePanelElement)child).refresh();
		//invoke the 'refreshed' event
		eRefreshed.invoker().invoke(this);
	}
	// ---------------------------------------------------
	/**
	 * Initializes this {@link TRefreshablePanelElement}.
	 */
	protected abstract void init();
	// ==================================================
}
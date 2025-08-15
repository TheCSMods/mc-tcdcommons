package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item.TMenuPanelButton;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item.TMenuPanelSeparator;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public abstract class TMenuPanel extends TPanelElement
{
	// ==================================================
	protected final TElementEvent_ChildAR ehChildAR = (parent, child, repositioned) -> realignChildren();
	// --------------------------------------------------
	public final TEvent<TMenuPanelEvent_ReAlignChildren> eRealignChildren = TEventFactory.createLoop();
	// ==================================================
	public TMenuPanel(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.scrollPadding = 1;
		
		this.eChildAdded.register(this.ehChildAR);
		this.eChildRemoved.register(this.ehChildAR);
	}
	// --------------------------------------------------
	public final @Override void setScrollFlags(int flags) throws UnsupportedOperationException { throw new UnsupportedOperationException(); }
	public final @Override void setScrollPadding(int scrollPadding) throws UnsupportedOperationException { throw new UnsupportedOperationException(); }
	/*public @Virtual @Override void setSize(int width, int height, int flags)
	{
		super.setSize(width, height, flags);
		
		//turns out this is VERY DANGEROUS. it causes StackOverflowError,
		//so definitely don't do this. call it manually after resizing instead
		if((flags & SS_INVOKE_EVENT) == SS_INVOKE_EVENT)
			realignChildren(); -- DO NOT DO THIS
	}*/
	// ==================================================
	/**
	 * Re-aligns the child elements of this {@link TMenuPanel} in accordance with its preferred layout.<p>
	 * It ensures the event handlers are properly registered, performs the realignment by invoking
	 * the abstract {@link #onRealignChildren()} method, and finally invokes the event to signal the realignment.
	 * @see #onRealignChildren()
	 * @see #eRealignChildren
	 */
	public final void realignChildren()
	{
		//ensure the event handlers are working properly
		if(!this.eChildAdded.isRegistered(this.ehChildAR))
			this.eChildAdded.register(this.ehChildAR);
		if(!this.eChildRemoved.isRegistered(this.ehChildAR))
			this.eChildRemoved.register(this.ehChildAR);
		
		//on-realign-children
		onRealignChildren();
		
		//finally, invoke the event
		this.eRealignChildren.invoker().invoke(this);
	}
	// --------------------------------------------------
	/**
	 * Called in {@link #realignChildren()}.<br/>
	 * Place the child re-aligning logic here.
	 */
	public abstract void onRealignChildren();
	// ==================================================
	/**
	 * Creates and adds a new {@link TMenuPanelButton} with
	 * the given text and on-click action.
	 * @param text The menu item {@link Component}.
	 * @param onClick The on-click action for the menu item.
	 * @return The created and added {@link TMenuPanelButton}.
	 */
	public @Virtual TMenuPanelButton addButton(Component text, Consumer<TButtonWidget> onClick)
	{
		final var item = new TMenuPanelButton(this, text);
		item.setOnClick(onClick);
		return item;
	}
	// --------------------------------------------------
	/**
	 * Creates and adds a new {@link TMenuPanelSeparator}.
	 */
	public @Virtual TMenuPanelSeparator addSeparator() { return new TMenuPanelSeparator(this); }
	// ==================================================
	public static interface TMenuPanelEvent_ReAlignChildren { public void invoke(TMenuPanel element); }
	// ==================================================
}
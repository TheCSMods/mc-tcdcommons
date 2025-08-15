package io.github.thecsdev.tcdcommons.api.client.gui.panel.menu;

import com.google.common.annotations.Beta;
import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.item.TMenuPanelButton;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputDiscoveryPhase;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputType;
import io.github.thecsdev.tcdcommons.api.client.gui.util.event.handler.TElementEvent_Runnable;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public @Virtual class TContextMenuPanel extends TMenuPanel
{
	// ==================================================
	protected final TElement target;
	protected final int initWidh, initHeight;
	// --------------------------------------------------
	private @Beta @Nullable TContextMenuPanel parentContextMenu; //target parent context menu
	private @Beta @Nullable TContextMenuPanel childContextMenu;
	// --------------------------------------------------
	public final TEvent<TElementEvent_Runnable<TContextMenuPanel>> eOpened = TEventFactory.createLoop();
	public final TEvent<TElementEvent_Runnable<TContextMenuPanel>> eClosed = TEventFactory.createLoop();
	// ==================================================
	/**
	 * Creates a new {@link TContextMenuPanel} for a given {@link TContextMenuPanel}.
	 * @param target The {@link TElement} this {@link TContextMenuPanel} is for.
	 */
	public TContextMenuPanel(TElement target)
	{
		super(0, 0, Math.max(target.getWidth(), 100), 5);
		this.backgroundColor = Color.BLACK.getRGB();
		this.scrollFlags = SCROLL_VERTICAL;
		this.zOffset = 200 + 100;
		
		this.initWidh = this.width;
		this.initHeight = this.height;
		
		this.target = Objects.requireNonNull(target);
		
		//(secured) this event handler ensures eOpened and eClosed work properly
		this.eParentChanged.register(this.ehParentChanged);
		//(unsecured) close this context menu if the target's parent is another context menu and it closed
		if(target.getParent() instanceof TContextMenuPanel)
		{
			this.parentContextMenu = ((TContextMenuPanel)target.getParent());
			this.parentContextMenu.childContextMenu = this;
			this.parentContextMenu.eClosed.register(parentCM -> this.close());
		}
		else this.parentContextMenu = null;
		
		//finally, let the target know by invoking its event
		this.target.eContextMenu.invoker().invoke(this.target, this);
	}
	// --------------------------------------------------
	public final TElement getTarget() { return this.target; }
	public final @Override boolean canBeAddedTo(TParentElement futureParent) { return futureParent instanceof TScreen; }
	// --------------------------------------------------
	protected final TElementEvent_ParentChanged ehParentChanged = (self, oldP, newP) ->
	{
		//if the new parent is not null, this menu is opening...
		if(newP != null)
		{
			//reposition this menu
			if(getParentTScreen() == null)
				throw new IllegalStateException("No parent screen? This shouldn't even happen.");
			realignPositionToTarget();
			
			//close any already opened context menus that aren't the parent context menu
			{
				final TContextMenuPanel otherContextMenu = (TContextMenuPanel) getParentTScreen()
						.findChild(c -> c instanceof TContextMenuPanel && c != this && c != this.parentContextMenu, true);
				if(otherContextMenu != null) otherContextMenu.close();
			}
			
			//focus on this context menu panel now, for tab navigation purposes
			getParentTScreen().setFocusedElement(this, false);
			
			//invoke the closing event
			this.eOpened.invoker().invoke(this);
		}
		//...else this menu is closing.
		else this.eClosed.invoker().invoke(this);
	};
	// ==================================================
	public @Virtual @Override TMenuPanelButton addButton(Component text, Consumer<TButtonWidget> onClick)
	{
		final var item = super.addButton(text, onClick);
		item.eClicked.register(btn -> TContextMenuPanel.this.close()); //close context menu on click
		return item;
	}
	// --------------------------------------------------
	public @Virtual @Override boolean input(TInputContext inputContext, InputDiscoveryPhase inputPhase)
	{
		//respect super
		if(super.input(inputContext, inputPhase))
			return true;
		//only listening on PREEMPT here
		else if(inputPhase != InputDiscoveryPhase.PREEMPT)
			return false;
		
		//handle based on input type
		if(inputContext.getInputType() == InputType.MOUSE_PRESS)
		{
			final var h = findHoveredContextMenu(getParentTScreen());
			if(h != this) //TODO - Add nested context menu support
			{
				close();
				return true;
			}
		}
		else if(inputContext.getInputType() == InputType.KEY_PRESS)
		{
			//close this context menu when the user presses the ESCAPE key
			if(inputContext.getKeyboardKey().keyCode == 256)
			{
				close();
				return true;
			}
		}
		return false;
	}
	// ==================================================
	/**
	 * Opens this {@link TContextMenuPanel} by adding it to
	 * the {@link #getTarget()}'s parent {@link TScreen}.
	 * @return this
	 */
	public final boolean open()
	{
		if(isOpen() || this.target.getParentTScreen() == null)
			return false;
		return this.target.getParentTScreen().addChild(this, false);
	}
	
	/**
	 * Closes this {@link TContextMenuPanel} by removing it from
	 * the {@link #getParent()} element.
	 */
	public final boolean close() { if(!isOpen()) return false; return getParent().removeChild(this, false); }
	
	/**
	 * Returns true if {@link #getParent()} is not null.
	 */
	public final boolean isOpen() { return getParent() != null; }
	// --------------------------------------------------
	/**
	 * Checks if this {@link TContextMenuPanel} or one of its children is
	 * focused or not. If not, then {@link #close()} will be called.
	 */
	public final boolean closeIfNotFocused()
	{
		final var pts = getParentTScreen();
		if(pts == null) return false;
		final var fe = pts.getFocusedElement();
		if(fe == null || (fe != this && fe.findParent(p -> p == this) == null))
		{
			close();
			return true;
		}
		else return false;
	}
	// --------------------------------------------------
	/**
	 * Automatically invoked by {@link #ehParentChanged}.<br/>
	 * Aligns this {@link TContextMenuPanel} for the {@link #target} element.<br/>
	 * Usually invoked when this {@link TContextMenuPanel} is shown on the screen.
	 */
	protected @Virtual void realignPositionToTarget()
	{
		//define initial position
		int newX = this.target.getX();
		int newY = this.target.getEndY();
		//realign
		if(getParentTScreen() != null)
		{
			final int screenWidth = getParentTScreen().getWidth();
			final int screenHeight = getParentTScreen().getHeight();
			// Re-align X
			if(newX + this.width > screenWidth) { newX -= (this.width - target.getWidth()); }
			// Re-align Y
			if(newY + this.height > screenHeight) { newY -= (this.height + target.getHeight()); }
		}
		//set the position
		setPosition(newX, newY, false);
	}
	// --------------------------------------------------
	public final @Override void onRealignChildren()
	{
		//ensure the event handlers are working properly
		if(this.eParentChanged.isRegistered(this.ehParentChanged))
			this.eParentChanged.register(this.ehParentChanged);
		
		//reset the size of this panel
		final int sp = this.scrollPadding, sp2 = sp * 2;
		this.width = this.initWidh;
		this.height = this.initHeight - sp;
		
		//prepare to iterate all children
		final TScreen scHalfWHScreen = (getParentTScreen() != null) ? getParentTScreen() : this.target.getParentTScreen();
		final int scHalfWidth = (scHalfWHScreen != null) ? (scHalfWHScreen.getWidth() / 3) : this.initWidh;
		final int scHalfHeight = (scHalfWHScreen != null) ? (scHalfWHScreen.getHeight() / 3) : 100;
		
		//iterate all children, and re-adjust them
		TElement previous = null;
		for(final var child : getChildren())
		{
			//first update the child's position;
			//focus on positioning it after the previous child
			int nextX = getX() + sp, nextY = getY() + sp;
			if(previous != null)
			{
				nextX = previous.getX();
				nextY = previous.getEndY();
			}
			child.setPosition(nextX, nextY, false);
			
			//next up, update the child's size;
			//width should be same as parent width (unless parent is smaller), while height is up to the child element
			if(this.width - sp2 < child.getWidth())
				this.width = Math.min(scHalfWidth, child.getWidth() + sp2);
			if(this.height < scHalfHeight) this.height += child.getHeight();
			
			//child.setSize(getWidth() - sp2, child.getHeight()); - do this in the next phase
			
			//set previous
			previous = child;
		}
		//now that this panel's size has adjusted to fit the menu items,
		//adjust all menu item widths to have the same width
		{
			final int gwMsp2 = getWidth() - sp2;
			for(final var child : getChildren())
				child.setSize(gwMsp2, child.getHeight());
		}
		
		//finally, handle setSize but while triggering proper flags as well
		this.height--; //setSize won't run without an actual change being made...
		setSize(this.width, this.height + 1, SS_ALL & ~SS_INVOKE_EVENT); //...so fake the change
		//^ do not invoke event to avoid StackOverflowError in case someone
		//re-instates the automatic re-alignment code
	}
	// ==================================================
	/**
	 * Tries to find any hovered {@link TContextMenuPanel}s for a given {@link TScreen}.
	 * @param targetScreen The target {@link TScreen}.
	 */
	public static @Nullable TContextMenuPanel findHoveredContextMenu(final TScreen targetScreen)
	{
		//null check
		if(targetScreen == null) return null;
		final var hovered = targetScreen.getHoveredElement(); //performance & prevent concurrent modification
		if(hovered == null) return null;
		//find
		if(hovered instanceof TContextMenuPanel) return (@Nullable TContextMenuPanel) hovered;
		else return (@Nullable TContextMenuPanel) hovered.findParent(c -> c instanceof TContextMenuPanel);
	}
	
	/**
	 * Looks for a {@link TContextMenuPanel} in a target {@link TScreen},
	 * and if one is found, it gets closed.
	 * @param targetScreen The target {@link TScreen}.
	 * @return True if a {@link TContextMenuPanel} was found and closed.
	 */
	public static boolean tryCloseAContextMenu(final TScreen targetScreen)
	{
		//null check
		if(targetScreen == null) return false;
		//perform the find operation
		final TContextMenuPanel cp = (TContextMenuPanel)targetScreen.findChild(c -> c instanceof TContextMenuPanel, true);
		//if found, close it and return true...
		if(cp != null) { cp.close(); return true; }
		//...and return false otherwise
		else return false;
	}
	// ==================================================
}
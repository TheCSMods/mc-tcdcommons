package io.github.thecsdev.tcdcommons.api.client.gui;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenWrapper;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.ITooltipProvider;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProvider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.text.Text;

public abstract class TElement implements TParentElement, ITooltipProvider
{
	// ==================================================
	/** Makes {@link #setPosition} set the position relative to parent. */
	public static final int SP_RELATIVE       = 0b1;
	/** Makes {@link #setPosition} move the {@link #children} alongside this element. */
	public static final int SP_MOVE_CHILDREN  = 0b10;
	/** Makes {@link #setPosition} update the parent's topmost {@link #children}. */
	public static final int SP_UPDATE_TOPMOST = 0b100;
	/** Makes {@link #setPosition} invoke the {@link #eMoved} event. */
	public static final int SP_INVOKE_EVENT   = 0b1000;
	/** A {@link #setPosition} flag containing all flags. */
	public static final int SP_ALL            = 0b1111;
	// --------------------------------------------------
	/** Makes {@link #setSize} update the parent's topmost {@link #children}. */
	public static final int SS_UPDATE_TOPMOST = 0b100;
	/** Makes {@link #setSize} invoke the {@link #eResized} event. */
	public static final int SS_INVOKE_EVENT   = 0b1000;
	/** A {@link #setSize} flag containing all flags. */
	public static final int SS_ALL            = 0b1111;
	// ==================================================
	@Internal @Nullable TParentElement __parent;
	private @Internal @Nullable TElement __parentElement;
	private @Internal @Nullable TScreen __parentScreen;
	// --------------------------------------------------
	private final TElementList children = new TElementList(this);
	// --------------------------------------------------
	protected int x = 0, y = 0, width = 20, height = 20;
	protected float alpha = 1;
	protected float zOffset = 0;
	protected @Nullable Tooltip tooltip;
	protected @Nullable TooltipPositioner tooltipPositioner;
	// --------------------------------------------------
	public final TEvent<TElementEvent_Moved> eMoved = TEventFactory.createLoop();
	public final TEvent<TElementEvent_Resized> eResized = TEventFactory.createLoop();
	public final TEvent<TElementEvent_ChildAR> eChildAdded = TEventFactory.createLoop();
	public final TEvent<TElementEvent_ChildAR> eChildRemoved = TEventFactory.createLoop();
	public final TEvent<TElementEvent_ParentChanged> eParentChanged = TEventFactory.createLoop();
	public final TEvent<TElementEvent_ContextMenu> eContextMenu = TEventFactory.createLoop();
	// ==================================================
	public TElement(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		this.alpha = 1;
		this.zOffset = 0;
		this.tooltip = null;
		this.tooltipPositioner = null; //initially null for performance reasons (lazy)
	}
	
	/**
	 * Intended for internal use only.<br/>
	 * Used for keeping track of the {@link #__parent} {@link TParentElement}s.
	 * @param parent The current or new parent element for this {@link TElement}.
	 */
	@Internal void __updateParent(final TParentElement parent)
	{
		//ignore if parent is the same, and handle parent assigning
		this.__parent = parent;
		//handle parent being TElement
		if(parent instanceof TElement)
		{
			final var parentElement = (TElement)parent;
			this.__parentElement = parentElement;
			this.__parentScreen = parentElement.__parentScreen;
		}
		//handle parent being TScreen
		else if(parent instanceof TScreen)
		{
			this.__parentElement = null;
			this.__parentScreen = (TScreen)parent;
		}
		//other parent types are unsupported
		else
		{
			this.__parentElement = null;
			this.__parentScreen = null;
		}
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link TElementList} containing the list of all
	 * children for this {@link TElement}.
	 */
	public final @Override TElementList getChildren() { return this.children; }
	
	/**
	 * Returns the parent {@link TParentElement} of this {@link TElement}.
	 */
	public final @Nullable @Override TParentElement getParent() { return this.__parent; }
	
	/**
	 * If {@link #getParent()} is a {@link TElement}, the {@link TParentElement}
	 * will be returned as a {@link TElement}. Null is returned otherwise.
	 */
	public final @Nullable TElement getParentTElement() { return this.__parentElement; }
	
	/**
	 * If {@link #getParent()} is a {@link TScreen}, that {@link TScreen} will be returned.<br/>
	 * If however, {@link #getParent()} is a {@link TElement}, then that {@link TElement}'s
	 * {@link TScreen} reference will be returned.<br/>This allows nested children to access
	 * their root {@link TScreen} parent through their {@link TElement} parent.
	 */
	public final @Nullable TScreen getParentTScreen() { return this.__parentScreen; }
	// --------------------------------------------------
	public final @Override int getX() { return this.x; }
	public final @Override int getY() { return this.y; }
	public final @Override int getWidth() { return this.width; }
	public final @Override int getHeight() { return this.height; }
	// --------------------------------------------------
	public final void move(int deltaX, int deltaY) { setPosition(this.x + deltaX, this.y + deltaY, false); }
	public final void moveChildren(int dX, int dY) { forEachChild(child -> { child.x += dX; child.y += dY; }, true); }
	// --------------------------------------------------
	//public final void setPosition(int x, int y) { setPosition(x, y, false); } - avoid confusion; it even confused me
	public final void setPosition(int x, int y, boolean relativeToParent)
	{
		int flags = SP_ALL;
		if(!relativeToParent) flags &= ~SP_RELATIVE;
		setPosition(x, y, flags);
	}
	public @Virtual void setPosition(int x, int y, int flags)
	{
		//handle SP_RELATIVE
		if((flags & SP_RELATIVE) == SP_RELATIVE && this.__parent != null)
		{
			x += this.__parent.getX();
			y += this.__parent.getY();
		}
		
		//don't bother setting if it's all the same
		if(this.x == x && this.y == y) return;
		
		//calculate by how much this element is about to move
		final int dX = x - this.x, dY = y - this.y;
		//assign new position
		this.x = x; this.y = y;
		
		//move all children accordingly
		if((flags & SP_MOVE_CHILDREN) == SP_MOVE_CHILDREN)
			moveChildren(dX, dY);
		
		//update parent top-most children
		if((flags & SP_UPDATE_TOPMOST) == SP_UPDATE_TOPMOST && this.__parent != null)
			this.__parent.getChildren().updateTopmostChildren();
		
		//invoke event
		if((flags & SP_INVOKE_EVENT) == SP_INVOKE_EVENT)
			this.eMoved.invoker().invoke(this, dX, dY);
	}
	// --------------------------------------------------
	public final void setSize(int width, int height) { setSize(width, height, SS_ALL); }
	public @Virtual void setSize(int width, int height, int flags)
	{
		//don't bother setting if it's all the same
		if(this.width == width && this.height == height) return;
		
		//obtain old size and assign new size
		final int oldWidth = this.width, oldHeight = this.height;
		this.width = width; this.height = height;
		
		//update parent top-most children
		if((flags & SS_UPDATE_TOPMOST) == SS_UPDATE_TOPMOST && this.__parent != null)
			this.__parent.getChildren().updateTopmostChildren();
		
		//invoke event
		if((flags & SS_INVOKE_EVENT) == SS_INVOKE_EVENT)
			this.eResized.invoker().invoke(this, oldWidth, oldHeight);
	}
	// ==================================================
	public final float getAlpha() { return this.alpha ; }
	public final void setAlpha(float alpha) { this.alpha = Math.max(0, Math.min(1, alpha)); }
	public final boolean isVisible() { return this.alpha > 0.05f && (getParentTElement() == null || getParentTElement().isVisible()); }
	public final boolean isEnabledAndVisible() { return isVisible() && isEnabled(); }
	//
	public final TextRenderer getTextRenderer()
	{
		if(this.__parentScreen != null)
			return this.__parentScreen.getTextRenderer();
		else return MC_CLIENT.textRenderer;
	}
	public final ItemRenderer getItemRenderer()
	{
		if(this.__parentScreen != null)
			return this.__parentScreen.getItemRenderer();
		else return MC_CLIENT.getItemRenderer();
	}
	//
	/**
	 * Returns the sum of {@link #getZOffset()} for this {@link TElement},
	 * while also taking into account any {@link TParentElement}s.<p>
	 * Please use {@link #setZOffset(float)} to modify the z-offset for this {@link TElement}.
	 * @see #getZOffset()
	 * @see #setZOffset(float)
	 */
	public final @Override float getZIndex() { return (this.__parent != null ? this.__parent.getZIndex() : 0) + getZOffset(); }
	public final float getZOffset() { return this.zOffset; }
	public @Virtual void setZOffset(float zOffset) { this.zOffset = zOffset; }
	//
	public final @Override boolean isEnabled() { return TParentElement.super.isEnabled(); }
	public @Virtual @Override boolean getEnabled() { return true; }
	public @Virtual boolean isFocusable() { return false; }
	public @Virtual boolean isHoverable() { return true; }
	//
	public final boolean isHovered() { return (this.__parentScreen != null && this.__parentScreen.getHoveredElement() == this); }
	public final boolean isFocused() { return (this.__parentScreen != null && this.__parentScreen.getFocusedElement() == this); }
	public final boolean isDragging() { return (this.__parentScreen != null && this.__parentScreen.getDraggingElement() == this); }
	public final boolean isFocusedOrHovered() { return isFocused() || isHovered(); }
	// --------------------------------------------------
	/**
	 * A utility method that creates and returns a new {@link TContextMenuPanel}
	 * instance for this {@link TElement}. Use this to create your own
	 * custom context menus for this {@link TElement}.
	 * @return A {@link TContextMenuPanel} for this {@link TElement}, or
	 * {@code null} if this {@link TElement} does not support context menus.
	 * @apiNote Do not do anything other than create and return a {@link TContextMenuPanel}
	 * instance from here. Any events such as {@link #eContextMenu} will be invoked automatically.
	 */
	public @Virtual @Nullable TContextMenuPanel createContextMenu()
	{
		//create the context menu, the context menu event gets invoked automatically
		final var menu = new TContextMenuPanel(this);
		//if the menu has any menu items added, return the menu, and return null otherwise
		return (menu.getChildren().size() > 0) ? menu : null;
	}
	// --------------------------------------------------
	/**
	 * Used for periodic updates for this {@link TElement}.
	 * @apiNote Called automatically by {@link TScreenWrapper}. Do not call it yourself.
	 * @apiNote For performance reasons, do not perform any expensive operations in here.
	 */
	public @Virtual void tick() {}
	public abstract @Override void render(TDrawContext pencil);
	public @Virtual void postRender(TDrawContext pencil) {}
	// --------------------------------------------------
	public @Virtual @Override boolean input(TInputContext inputContext) { return false; }
	// ==================================================
	public final @Nullable Tooltip getTooltip() { return this.tooltip; }
	public @Virtual void setTooltip(@Nullable Tooltip tooltip) { this.tooltip = tooltip; }
	public final TooltipPositioner getTooltipPositioner()
	{
		return this.tooltipPositioner != null ?
				this.tooltipPositioner :
				(this.tooltipPositioner = GuiUtils.createDefaultTooltipPositioner(this));
	}
	// ==================================================
	/**
	 * Returns the {@link TElement} that comes <b>before</b> this
	 * one in the hierarchy of parent/child relations.<p>
	 * Primarily used for "tab navigation".
	 */
	public final @Nullable TElement previous()
	{
		//obtain the starting point | prioritize screen
		TParentElement start = getParentTScreen();
		if(start == null) start = getParent();
		if(start == null) return null;
		
		//begin the iteration process
		final var flag = new AtomicReference<TElement>(null);
		start.findChild(child ->
		{
			//break the loop once "this" child is found;
			if(child == this) return true;
			//until "this" child is found, keep setting the flag
			//for what the previous child is
			flag.set(child);
			return false;
		}, true);
		return flag.get();
	}
	
	/**
	 * Returns the {@link TElement} that comes <b>after</b> this
	 * one in the hierarchy of parent/child relations.<p>
	 * Primarily used for "tab navigation".
	 */
	public final @Nullable TElement next()
	{
		//return first child if there is one
		if(this.children.size() > 0)
			return this.children.iterator().next();
		
		//----- ----- iterating parent method
		//obtain the starting point | prioritize screen
		TParentElement start = getParentTScreen();
		if(start == null) start = getParent();
		if(start == null) return null;
		
		//begin the iteration process
		final var flag = new AtomicBoolean(false);
		return start.findChild(child ->
		{
			//once "this" child is found, the flag is set (and return false)...
			if(child == this) { flag.set(true); return false; }
			//...after which this code here will return the next child
			return flag.get();
		}, true);
	}
	// ==================================================
	public @Virtual @Override String toString()
	{
		final String base = super.toString() + String.format("[%d, %d, %d, %d", this.x, this.y, this.width, this.height);
		if(this instanceof ITextProvider)
		{
			final ITextProvider itp = (ITextProvider) this;
			final Text txt = itp.getText();
			final String textString = txt != null ? txt.getString() : "";
			return base + ", \"" + textString + "\"]";
		}
		return base + "]";
	}
	// --------------------------------------------------
	/**
	 * Returns true if this {@link TElement} is okay with
	 * being added to a given {@link TParentElement}.
	 * @param futureParent The {@link TParentElement} that is being checked.
	 */
	public @Virtual boolean canBeAddedTo(TParentElement futureParent) { return futureParent != null; }
	// ==================================================
	public static interface TElementEvent_Moved { public void invoke(TElement element, int deltaX, int deltaY); }
	public static interface TElementEvent_Resized { public void invoke(TElement element, int oldWidth, int oldHeight); }
	public static interface TElementEvent_ChildAR { public void invoke(TElement element, TElement child, boolean repositioned); }
	public static interface TElementEvent_ParentChanged { public void invoke(TElement element, @Nullable TParentElement oldParent, @Nullable TParentElement newParent); }
	public static interface TElementEvent_ContextMenu { public void invoke(TElement element, TContextMenuPanel contextMenu); }
	// ==================================================
}
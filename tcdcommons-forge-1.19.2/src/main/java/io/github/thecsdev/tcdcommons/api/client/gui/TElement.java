package io.github.thecsdev.tcdcommons.api.client.gui;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.events.TElementEvents;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TElementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class TElement extends TDrawableHelper implements TParentElement
{
	// ==================================================
	/** Makes {@link #setPosition(int, int, int)} set the position relative to {@link #parent}. */
	public static final int SP_RELATIVE       = 0b1;
	/** Makes {@link #setPosition(int, int, int)} move the {@link #children} alongside this element. */
	public static final int SP_MOVE_CHILDREN  = 0b10;
	/** Makes {@link #setPosition(int, int, int)} update the {@link #parent}'s topmost {@link #children}. */
	public static final int SP_UPDATE_TOPMOST = 0b100;
	// --------------------------------------------------
	/**
	 * Automatically assigned by {@link TScreen} when rendering this element.<br/>
	 * Represents the {@link TScreen} that this element is located on.<br/>
	 * <br/>
	 * <b>Read only! Modifying this will have negative consequences.</b>
	 */
	public @Nullable TScreen screen;
	
	/**
	 * Automatically assigned by {@link TScreen} when rendering this element.<br/>
	 * Represents the {@link TElement} that is the parent of this element.<br/>
	 * <br/>
	 * <b>Read only! Modifying this will have negative consequences.</b>
	 */
	public @Nullable TElement parent;
	// --------------------------------------------------
	/**
	 * The position of this {@link TElement} relative to the {@link #screen}.<br/>
	 * <br/>
	 * <i>Read only. Use {@link #setPosition(int, int, boolean)} to
	 * set the position to another value. This will ensure the element
	 * gets repositioned and handled properly.</i>
	 */
	protected int x, y;
	
	/**
	 * The size of this {@link TElement} relative to the {@link #screen}.<br/>
	 * <br/>
	 * <i>Read only. Writing to this will result in children not behaving
	 * and rendering properly. Use {@link #updateRenderingBoundingBox()}
	 * for each child to resolve that issue if you really have to write to this.</i>
	 */
	protected int width, height;
	
	/** The list of children for this {@link TElement}. */
	protected final TElementList children;
	// --------------------------------------------------
	/**
	 * Whether this element is visible or not.
	 */
	protected boolean visible;
	
	/**
	 * The local alpha/opacity of this {@link TElement}. This field
	 * does not take into account the {@link #parent}'s alpha.
	 */
	protected float localAlpha;
	
	/**
	 * The tooltip text for this {@link TElement}.
	 */
	protected @Nullable Component tooltip;
	// ==================================================
	private final TElementEvents __events = new TElementEvents(this);
	// ==================================================
	public TElement(int x, int y, int width, int height)
	{
		//init children
		children = new TElementList(this);
		//init dimensions
		this.x = x;
		this.y = y;
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		//init parent info
		this.screen = null;
		this.parent = null;
		//init other info
		this.visible = true;
		this.localAlpha = 1;
		
		this.tooltip = null;
		
		//initialize the rectangle
		//updateRenderingBoundingBox(); - bad idea, as getTpe...() can be overridden
		RENDER_RECT.setLocation(this.x, this.y);
		RENDER_RECT.setSize(this.width, this.height);
	}
	// --------------------------------------------------
	public TElementEvents getEvents() { return this.__events; }
	// ==================================================
	@Override
	@Nullable
	public TParentElement getTParent() { return this.parent != null ? this.parent : this.screen; }
	
	@Override
	public TElementList getTChildren() { return this.children; }
	
	/**
	 * Returns the {@link #screen}'s {@link TextRenderer},
	 * or the {@link MinecraftClient}'s {@link TextRenderer}
	 * if the {@link #screen} is null.
	 */
	@SuppressWarnings("resource")
	public final Font getTextRenderer()
	{
		if(this.screen != null) return this.screen.getTextRenderer();
		return Minecraft.getInstance().font;
	}
	
	/**
	 * Returns the {@link #screen}'s {@link ItemRenderer},
	 * or the {@link MinecraftClient}'s {@link ItemRenderer}
	 * if the {@link #screen} is null.
	 */
	public final ItemRenderer getItemRenderer()
	{
		if(this.screen != null) return this.screen.getItemRenderer();
		return Minecraft.getInstance().getItemRenderer();
	}
	
	/**
	 * Returns the {@link #screen}'s client, or if the screen
	 * is null, then {@link MinecraftClient#getInstance()}.
	 */
	public final Minecraft getClient()
	{
		if(this.screen != null) return this.screen.getClient();
		return Minecraft.getInstance();
	}
	// --------------------------------------------------
	/**
	 * Manually updates the value of {@link #screen}
	 * based on the {@link #parent}'s {@link #screen}
	 * and then returns the {@link #screen}.
	 */
	public @Nullable final TScreen updateScreen()
	{
		if(this.parent != null)
			return (this.screen = this.parent.updateScreen());
		else return this.screen;
	}
	// ==================================================
	public @Override int getTpeX() { return this.x; }
	public @Override int getTpeY() { return this.y; }
	public @Override int getTpeWidth() { return this.width; }
	public @Override int getTpeHeight() { return this.height; }
	public @Override final int getTpeEndX() { return super.getTpeEndX(); }
	public @Override final int getTpeEndY() { return super.getTpeEndY(); }
	public final @Override <T extends TElement> boolean addTChild(T child) { return addTChild(child, true); }
	public final @Override <T extends TElement> boolean removeTChild(T child) { return removeTChild(child, true); }
	// ==================================================
	/**
	 * This {@link Rectangle} is used as "cache" so
	 * {@link #getRenderingBoundingBox()} doesn't have
	 * to create a new {@link Rectangle} every time it is called.
	 */
	protected final Rectangle RENDER_RECT = new Rectangle();
	
	public @Override @Nullable Rectangle getRenderingBoundingBox()
	{
		if(RENDER_RECT.width < 1 || RENDER_RECT.height < 1) return null;
		else return RENDER_RECT;
	}
	
	public @Override void updateRenderingBoundingBox()
	{
		//define stuff
		RENDER_RECT.setLocation(getTpeX(), getTpeY());
		RENDER_RECT.setSize(getTpeWidth(), getTpeHeight());
		
		//check if there is a parent. if not, return the bounds of this element.
		//then get the parent rectangle and use it to constrain the current rectangle
		TParentElement parent = getTParent();
		if(parent == null) return;
		Rectangle pRect = parent.getRenderingBoundingBox();
		if(pRect == null) { RENDER_RECT.width = 0; return; }
		
		//check the rectangle bounds, make sure the parent contains the child
		/*if(!pRect.contains(RENDER_RECT) && !pRect.intersects(RENDER_RECT))
		{
			//out of bounds, do not render
			RENDER_RECT.width = 0;
			return;
		}*/
		if(RENDER_RECT.x > pRect.x + pRect.width || RENDER_RECT.y > pRect.y + pRect.height)
		{ RENDER_RECT.width = 0; return; } //out of bounds
		
		//check top-left bounds
		else if(RENDER_RECT.x + RENDER_RECT.width < pRect.x || RENDER_RECT.y + RENDER_RECT.height < pRect.y)
		{ RENDER_RECT.width = 0; return; } //out of bounds
		
		//restrain the current rectangle
		RENDER_RECT.x = Math.max(RENDER_RECT.x, pRect.x);
		RENDER_RECT.y = Math.max(RENDER_RECT.y, pRect.y);
		RENDER_RECT.width = Math.min(RENDER_RECT.x + RENDER_RECT.width, pRect.x + pRect.width);
		RENDER_RECT.height = Math.min(RENDER_RECT.y + RENDER_RECT.height, pRect.y + pRect.height);
		RENDER_RECT.width -= RENDER_RECT.x;
		RENDER_RECT.height -= RENDER_RECT.y;
	}
	// ==================================================
	/**
	 * Returns true if this {@link TElement} is the currently
	 * focused element on the parent {@link #screen}.
	 */
	public boolean isFocused() { return (screen != null) && (screen.getFocusedTChild() == this); }
	
	/**
	 * Returns true if the cursor is currently hovering over this {@link TElement}.
	 * More specifically, if {@link TScreen#getHoveredTChild()} returns this {@link TElement}.
	 */
	public boolean isHovered() { return (screen != null) && (screen.getHoveredTChild() == this); }
	
	/**
	 * return {@link #isHovered()} || {@link #isFocused()};
	 */
	public boolean isFocusedOrHovered() { return isHovered() || isFocused(); }
	
	/**
	 * Returns true if the mouse cursor is currently
	 * attempting to drag this {@link TElement}.
	 */
	public final boolean isBeingDragged() { return this.screen != null && this.screen.getDraggingTChild() == this; }
	// --------------------------------------------------
	/**
	 * Returns true if {@link #getEnabled()} is true and
	 * the {@link #parent}'s {@link #isEnabled()} is also true.
	 */
	public final boolean isEnabled() { return getEnabled() && (this.parent == null || this.parent.isEnabled()); }
	
	/**
	 * Returns true if {@link #getVisible()} is true and
	 * the {@link #parent}'s {@link #isVisible()} is also true.
	 */
	public final boolean isVisible() { return getVisible() && (this.parent == null || this.parent.isVisible()); }
	
	/**
	 * Returns true if {@link #isEnabled()} and {@link #isVisible()}
	 * both return true.
	 */
	public final boolean isEnabledAndVisible() { return isEnabled() && isVisible(); }
	// --------------------------------------------------
	/**
	 * When this returns true, any mouse related events
	 * will be sent to the {@link #parent} instead.
	 */
	public boolean isClickThrough() { return false; }
	// --------------------------------------------------
	/**
	 * Returns true if this {@link TElement} is enabled.<br/>
	 * Doesn't account for parents being disabled. See {@link #isEnabled()}.
	 */
	public boolean getEnabled() { return true; }
	
	/**
	 * Returns true if this {@link TElement} is visible.<br/>
	 * Doesn't account for parents being invisible. See {@link #isVisible()}.
	 */
	public boolean getVisible() { return this.visible; }
	
	/**
	 * Sets {@link #visible}. See {@link #getVisible()}.
	 * @param visible The value to set.
	 */
	public void setVisible(boolean visible) { this.visible = visible; }
	// --------------------------------------------------
	@Override
	public float getAlpha() { return this.localAlpha * (parent != null ? parent.getAlpha() : 1); }
	
	/**
	 * Returns the {@link #localAlpha} (opacity) of this {@link TElement}
	 * while ignoring the {@link #parent}'s alpha (opacity).
	 */
	public float getLocalAlpha() { return this.localAlpha; }
	
	/**
	 * Sets the {@link #localAlpha} (opacity) of this {@link TElement}.
	 * @param alpha The alpha/opacity value.
	 */
	public float setAlpha(float alpha) { return this.localAlpha = alpha; }
	// ==================================================
	/**
	 * Returns the tooltip text that will be drawn on the
	 * screen when this {@link TElement} is focused or hovered.<br/>
	 * <br/>
	 * Note: Because of the way tooltips are rendered. This method is final.
	 */
	public final @Nullable Component getTooltip() { return this.tooltip; }
	
	/**
	 * Sets the {@link #getTooltip()} {@link Text} of this {@link TElement}.
	 * @param tooltip The new tooltip {@link Text}.
	 */
	public void setTooltip(@Nullable Component tooltip) { this.tooltip = tooltip; }
	// ==================================================
	/**
	 * Sets the {@link #x} and {@link #y} coordinate of this
	 * {@link TElement} to a given position.
	 * @param x The X position.
	 * @param y The Y position.
	 * @param relativeToParent Is the given XY position relative to the {@link #parent}?
	 */
	public final void setPosition(int x, int y, boolean relativeToParent)
	{
		int flags = SP_MOVE_CHILDREN | SP_UPDATE_TOPMOST;
		if(relativeToParent) flags |= SP_RELATIVE;
		setPosition(x, y, flags);
	}
	
	/**
	 * Sets the {@link #x} and {@link #y} coordinate of this
	 * {@link TElement} to a given position, while also letting
	 * you decide if you wish to update the {@link #parent}'s
	 * topmost {@link #children} elements.<br/>
	 * <br/>
	 * See {@link TElementList#updateTopmostChildren()}.
	 * @param x The X position.
	 * @param y The Y position.
	 * @param flags The special flags that define the behavior of this method.
	 */
	public void setPosition(int x, int y, int flags)
	{
		//relative to the parent?
		boolean rel2Parent = (parent != null) && ((flags & SP_RELATIVE) == SP_RELATIVE);
		x += (rel2Parent ? parent.x : 0);
		y += (rel2Parent ? parent.y : 0);
		
		//calculate the differences first,
		//and then assign the XY values
		int dx = x - this.x;
		int dy = y - this.y;
		this.x = x;
		this.y = y;
		
		//move children
		//int flagsWithNoUT = flags & (~SP_UPDATE_TOPMOST);
		if((flags & SP_MOVE_CHILDREN) == SP_MOVE_CHILDREN)
		{
			moveChildren(dx, dy);
			/*for(TElement child : getTChildren())
				child.setPosition(child.x + dx, child.y + dy, flagsWithNoUT);*/
		}
		
		//update parent's topmost children
		if((flags & SP_UPDATE_TOPMOST) == SP_UPDATE_TOPMOST && this.parent != null)
			this.parent.children.updateTopmostChildren();
		
		//invoke event
		getEvents().MOVED.p_invoke(handler -> handler.accept(dx, dy));
	}
	
	/**
	 * Moves this {@link TElement} using {@link #setPosition(int, int, boolean)}
	 * relative to the current position.
	 * @param x The number of X of pixels to move by.
	 * @param y The number of Y of pixels to move by.
	 */
	public final void move(int x, int y) { move(x, y, true); }
	
	/**
	 * Moves this {@link TElement} using {@link #setPosition(int, int, boolean)}
	 * relative to the current position.
	 * @param x The number of X of pixels to move by.
	 * @param y The number of Y of pixels to move by.
	 * @param invokeEvent When set to true, the {@link TElementEvents#MOVED} event will be invoked.
	 */
	public final void move(int x, int y, boolean invokeEvent)
	{
		//setPosition(getTpeX() + x, getTpeY() + y, false);
		this.x += x;
		this.y += y;
		moveChildren(x, y, invokeEvent);
		
		//invoke event
		if(invokeEvent) getEvents().MOVED.p_invoke(handler -> handler.accept(x, y));
	}
	
	/**
	 * Calls {@link #move(int, int)} on all of the {@link #children}.
	 * @param x The number of X of pixels to move by.
	 * @param y The number of Y of pixels to move by.
	 */
	public final void moveChildren(int x, int y) { moveChildren(x, y, true); }
	
	/**
	 * Calls {@link #move(int, int)} on all of the {@link #children}.
	 * @param x The number of X of pixels to move by.
	 * @param y The number of Y of pixels to move by.
	 * @param invokeEvent When set to true, the {@link TElementEvents#MOVED} event
	 * will be invoked for each child {@link TElement}.
	 */
	public final void moveChildren(int x, int y, boolean invokeEvent)
	{
		for(TElement child : this.children)
			child.move(x, y, invokeEvent);
	}
	// ==================================================
	/**
	 * Handle {@link #screen} ticking here. By default, it does nothing.<br/>
	 * <br/>
	 * <i>Deprecated for performance reasons. You can always override
	 * {@link TScreen#tick()} yourself, and handle ticking there.<i/>
	 */
	@Deprecated(since = "1.0", forRemoval = false)
	public void tick() {}
	
	@Override
	public final double getZIndex() { return (getTParent() != null ? getTParent().getZIndex() : 0) + getBlitOffset(); }
	
	/**
	 * Called by {@link TScreen} when rendering this {@link TElement}.<br/>
	 * Renders this {@link TElement} on the {@link TScreen}.
	 * @param matrices The {@link PoseStack}.
	 * @param mouseX The X mouse cursor position on the {@link TScreen}.
	 * @param mouseY The Y mouse cursor position on the {@link TScreen}.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	public abstract void render(PoseStack matrices, int mouseX, int mouseY, float deltaTime);
	
	/**
	 * Called by {@link TScreen} after rendering this {@link TElement}
	 * and all of it's children.<br/>
	 * Renders this {@link TElement} on the {@link TScreen}.<br/>
	 * <br/>
	 * See {@link #render(PoseStack, int, int, float)}.
	 * @param matrices The {@link PoseStack}.
	 * @param mouseX The X mouse cursor position on the {@link TScreen}.
	 * @param mouseY The Y mouse cursor position on the {@link TScreen}.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	public void postRender(PoseStack matrices, int mouseX, int mouseY, float deltaTime) {}
	// ==================================================
	/**
	 * Invoked by the {@link #parent} {@link TScreen} when the user presses
	 * a mouse button while hovering over this element.<br/>
	 * <br/>
	 * <b>Note:</b><br/>
	 * In order to allow dragging and scrolling of parent elements,
	 * returning true here will block<br/>
	 * {@link #mouseDragged(double, double, double, double, int)}<br/>
	 * from being called.
	 * @param mouseX The X mouse cursor position on the {@link TScreen}.
	 * @param mouseY The Y mouse cursor position on the {@link TScreen}.
	 * @param button The pressed mouse button.
	 * @return true if this {@link TElement} handled the mouse click.
	 */
	public boolean mousePressed(int mouseX, int mouseY, int button) { return false; }
	
	/**
	 * Invoked by the {@link #parent} {@link TScreen} when the
	 * user drags this {@link TElement}.<br/>
	 * <br/>
	 * <b>Note:</b><br/>
	 * In order to allow dragging and scrolling of parent elements,
	 * {@link #mousePressed(int, int, int)} must return false.
	 * @param mouseX The X mouse cursor position on the {@link TScreen}.
	 * @param mouseY The Y mouse cursor position on the {@link TScreen}.
	 * @param deltaX Cursor X movement.
	 * @param deltaY Cursor Y movement.
	 * @param button The mouse button used for dragging.
	 * @return true if the {@link TElement} handled the drag.
	 */
	public boolean mouseDragged(double mouseX, double mouseY, double deltaX, double deltaY, int button) { return false; }
	
	/**
	 * Invoked by the {@link #parent} {@link TScreen} when the
	 * user mouse scrolls while hovering over this {@link TElement}.
	 * @param mouseX The X mouse cursor position on the {@link TScreen}.
	 * @param mouseY The Y mouse cursor position on the {@link TScreen}.
	 * @param amount The scroll amount. Can be both positive and negative
	 * depending on the scroll direction.
	 * @return true if the {@link TElement} handled the scroll.
	 */
	public boolean mouseScrolled(int mouseX, int mouseY, int amount) { return false; }
	
	/**
	 * Invoked by the {@link #parent} {@link TScreen} when the user releases
	 * a mouse button if this element was pressed.
	 * @param mouseX The X mouse cursor position on the {@link TScreen}.
	 * @param mouseY The Y mouse cursor position on the {@link TScreen}.
	 * @param button The released mouse button.
	 * @return true if this {@link TElement} handled the mouse release.
	 */
	public boolean mouseReleased(int mouseX, int mouseY, int button) { return false; }
	
	/**
	 * Invoked by the {@link #parent} {@link TScreen} when the user presses
	 * a keyboard key while this element is focused.<br/>
	 * <br/>
	 * This {@link TElement} should return true if it captured and processed this key press.
	 * @param keyCode The pressed key's key code.
	 * @param scanCode The pressed key's scan code.
	 * @param modifiers If any modifier keys are pressed.
	 */
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
	
	/**
	 * Invoked by the {@link #parent} {@link TScreen} when the user
	 * inputs a keyboard character while this element is focused.<br/>
	 * <br/>
	 * This {@link TElement} should return true if it captured and processed this character input.
	 * @param character The input character.
	 * @param modifiers Special key modifiers.
	 */
	public boolean charTyped(char character, int modifiers) { return false; }
	
	/**
	 * Called by the {@link #parent} {@link TScreen} when attempting to focus
	 * on this {@link TElement} or when attempting to focus off of this
	 * {@link TElement}. This method should return true if it wishes to approve
	 * of the focus change, and false otherwise.<br/>
	 * <br/>
	 * <i>Note 1: The {@link #parent} {@link TScreen} may ignore this
	 * {@link TElement} returning false, and change the focus anyways.</i><br/>
	 * <i>Note 2: For mouse focus to work, {@link #mousePressed(int, int, int)}
	 * needs to return true before this method gets called.</i>
	 * @param focusOrigin What is causing the focus change. See {@link FocusOrigin}.
	 * @param gainingFocus True when attempting to focus on this element, and false otherwise.
	 * @return true if this element wishes to gain or lose a given focus.<br/>
	 * <b>Important: Avoid handling focus changes here, as the parent screen
	 * may choose to ignore the returned value.</b>
	 */
	public boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus)
	{
		//default settings
		return  //approve if losing focus in any way
				!gainingFocus ||
				//or if gaining/losing focus with tab navigation
				focusOrigin == FocusOrigin.TAB;
		//will not approve gaining focus from mouse clicks
	}
	// ==================================================
	private WeakReference<TContextMenuPanel> __contextMenu;
	
	/**
	 * Creates and shows a new {@link TContextMenuPanel}
	 * for this {@link TElement}.
	 * @return The created and opened context menu, or
	 * null if the context menu had no entries added to it.
	 */
	public final @Nullable TContextMenuPanel showContextMenu() { return showContextMenu(getTpeX(), getTpeEndY()); }
	
	/**
	 * Same as {@link #showContextMenu()}, but you can
	 * also define the context menu's coordinates here.
	 * @param x The X coordinate for the context menu.
	 * @param y The Y coordinate for the context menu.
	 * @see #showContextMenu()
	 */
	public final @Nullable TContextMenuPanel showContextMenu(int x, int y)
	{
		if(this.screen == null) return null;
		
		//create context menu
		var contextMenu = createContextMenu(x, y);
		if(contextMenu != null) this.__contextMenu = new WeakReference<>(contextMenu);
		else { this.__contextMenu = null; return null; }
		
		//show/open the context menu
		this.screen.addTChild(contextMenu, false);
		contextMenu.updatePositionAndSize();
		
		//return the context menu
		return contextMenu;
	}
	
	/**
	 * Returns the currently shown {@link TContextMenuPanel},
	 * or null if there are no related context menus opened.
	 * @see #showContextMenu().
	 */
	public final @Nullable TContextMenuPanel getShownContextMenu()
	{
		var contextMenu = (__contextMenu != null) ? __contextMenu.get() : null;
		if(contextMenu == null || contextMenu.screen == null)
			return null;
		return contextMenu;
	}
	
	/**
	 * Called by {@link #showContextMenu(int, int)} when
	 * creating the {@link TContextMenuPanel} for this {@link TElement}.<br/>
	 * <br/>
	 * Don't forget to call {@link #onContextMenu(TContextMenuPanel)} and
	 * {@link TElementEvents#CONTEXT_MENU} when overriding this.
	 * @param x The X coordinate for the context menu.
	 * @param y The Y coordinate for the context menu.
	 * @return The created context menu.
	 * @see #showContextMenu()
	 */
	protected @Nullable TContextMenuPanel createContextMenu(int x, int y)
	{
		//create context menu and call onContextMenu
		var contextMenu = new TContextMenuPanel(x, y, Mth.clamp(getTpeWidth(), 50, 150));
		onContextMenu(contextMenu);
		
		//invoke the event
		getEvents().CONTEXT_MENU.p_invoke(handler -> handler.accept(contextMenu));
		
		//return null if there are no entries added to it
		if(contextMenu.getTChildren().size() < 1)
			return null;
		
		//return the context menu
		return contextMenu;
	}
	
	/**
	 * Invoked when a {@link TContextMenuPanel} is being created
	 * for this {@link TElement}. Use this to add entries to
	 * the context menu.
	 */
	protected void onContextMenu(TContextMenuPanel contextMenu) {}
	// ==================================================
	/**
	 * Returns the {@link TElement} that comes before this
	 * {@link TElement}. Mainly used for Tab navigation.<br/>
	 * <i>Please avoid calling this method frequently. May cause
	 * lag, as it iterates parent's children.</i>
	 */
	@Nullable
	public TElement previous()
	{
		//TODO - Improve performance, use a different approach
		if(this.screen == null) return null;
		final AtomicReference<TElement> flag = new AtomicReference<TElement>(null);
		this.screen.forEachChild(child ->
		{
			//when this child is found, stop the loop
			if(child == this) return true;
			//else assign current child and continue the loop
			flag.set(child);
			return false;
		},
		true);
		//return the child that was assigned
		//before the loop ended
		return flag.get();
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link TElement} that follows after this
	 * {@link TElement}. Mainly used for Tab navigation.<br/>
	 * <i>Please avoid calling this method frequently. May cause
	 * lag, as it iterates parent's children.</i>
	 */
	@Nullable
	public TElement next()
	{
		//TODO - Improve performance, use a different approach
		if(this.screen == null) return null;
		final AtomicBoolean flag = new AtomicBoolean(false);
		return this.screen.forEachChild(child ->
		{
			if(child == this)
			{
				flag.set(true);
				return false; //will return true next time
			}
			return flag.get();
		},
		true);
	}
	// ==================================================
	/**
	 * Returns true if this {@link TElement} is okay with
	 * being added to a given {@link TParentElement}.
	 */
	public boolean canBeAddedTo(TParentElement parent) { return parent != null; }
	
	/**
	 * Called when this {@link TElement} is added to or
	 * removed from a {@link TParentElement}.<br/>
	 * <br/>
	 * <b>Do not call this outside of a {@link TElementList}!</b>
	 */
	public void onParentChanged() {}
	// ==================================================
	/**
	 * Draws a {@link MutableText} using the dimensions of this {@link TElement}.
	 * @param matrices The {@link PoseStack}.
	 * @param text The text to draw.
	 * @param alignment The horizontal text alignment. The vertical one is always center.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	public void drawTElementText(PoseStack matrices, Component text, HorizontalAlignment alignment, float deltaTime)
	{
		int color = GuiUtils.applyAlpha(getEnabled() ? 16777215 : 10526880, getAlpha());
		drawTElementText(matrices, text, alignment, color, deltaTime);
	}
	
	/**
	 * Draws a {@link MutableText} using the dimensions of this {@link TElement}.
	 * @param matrices The {@link PoseStack}.
	 * @param text The text to draw.
	 * @param alignment The horizontal text alignment. The vertical one is always center.
	 * @param color The color of the text.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	public void drawTElementText(PoseStack matrices, Component text, HorizontalAlignment alignment, int color, float deltaTime)
	{
		drawTElementText(matrices, text, alignment, color, 5, deltaTime);
	}
	
	/**
	 * Draws a {@link MutableText} using the dimensions of this {@link TElement}.
	 * @param matrices The {@link PoseStack}.
	 * @param text The text to draw.
	 * @param alignment The horizontal text alignment. The vertical one is always center.
	 * @param color The color of the text.
	 * @param padding The text padding.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	public void drawTElementText(PoseStack matrices, Component text, HorizontalAlignment alignment, int color, int padding, float deltaTime)
	{
		//obtain the text renderer
		if(this.screen == null) return;
		
		//draw the message
		if(text != null)
		{
			Font txtR = getTextRenderer();
			int x = getTpeX();
			int y = getTpeY() + (getTpeHeight() - 8) / 2;
			int width = getTpeWidth();
			
			switch(alignment)
			{
				case CENTER:
					drawCenteredText(matrices, txtR, text, x + width / 2, y, color);
					break;
				case LEFT:
					drawTextWithShadow(matrices, txtR, text, x + padding, y, color);
					break;
				case RIGHT:
					drawTextWithShadow(matrices, txtR, text, x + width - padding - txtR.width(text.getString()), y, color);
					break;
				default: return;
			}
		}
	}
	// ==================================================
}
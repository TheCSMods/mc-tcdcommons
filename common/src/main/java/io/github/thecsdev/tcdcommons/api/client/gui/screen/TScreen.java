package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TElementList;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.netty.util.internal.UnstableApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.blaze3d.platform.InputConstants.*;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

public abstract class TScreen implements TParentElement
{
	// ==================================================
	protected @Nullable Minecraft client = MC_CLIENT;
	protected final TElementList children = new TElementList(this);
	private final @Internal TScreenWrapper<?> __wrapper;
	// --------------------------------------------------
	protected @Nullable Component title;
	
	//assigned automatically
	@Internal @Nullable TElement __hovered = null; //keeps track of currently cursor-hovered element
	@Internal @Nullable TElement __dragging = null; //keeps track of currently dragged element
	@Internal @Nullable TElement __focused = null; //keeps track of the currently focused element
	
	@Internal int __draggingButton = -1; //keeps track of which mouse button is used to drag
	@Internal final Point __mousePosition = new Point(0, 0); //keeps track of current mouse cursor XY position
	// ==================================================
	public TScreen(Component title)
	{
		this.title = Objects.requireNonNull(title); //(1) title must be assigned first
		this.__wrapper = Objects.requireNonNull(createScreenWrapper());
	}
	protected @Virtual TScreenWrapper<?> createScreenWrapper() { return new TScreenWrapper<TScreen>(this); }
	// --------------------------------------------------
	public final Screen getAsScreen() { return this.__wrapper; }
	public final @Nullable Minecraft getClient() { return this.client; }
	public final Font getTextRenderer() { return this.client.font; }
	public final ItemRenderer getItemRenderer() { return this.client.getItemRenderer(); }
	//
	public final Component getTitle() { return this.title; }
	public final @Override boolean isEnabled() { return TParentElement.super.isEnabled(); }
	public final @Override boolean getEnabled() { return true; }
	public @Virtual void close() { this.__wrapper.Screen_super_close(); }
	protected @Virtual void onOpened() {}
	protected @Virtual void onClosed() {}
	//
	public @Virtual boolean shouldPause() { return true; }
	public @Virtual boolean shouldCloseOnEsc() { return true; }
	public @Virtual boolean shouldRenderInGameHud() { return true; }
	//
	public final boolean isOpen() { return MC_CLIENT.screen == this.__wrapper; }
	//
	public final Point getMousePosition() { return this.__mousePosition.getLocation(); }
	@Internal final void setMousePosition(int x, int y) { this.__mousePosition.x = x; this.__mousePosition.y = y; }
	// --------------------------------------------------
	public final @Nullable @Override TParentElement getParent() { return null; }
	public final @Override TElementList getChildren() { return this.children; }
	// ==================================================
	public final @Override int getX() { return 0; }
	public final @Override int getY() { return 0; }
	public final @Override int getWidth() { return this.__wrapper.width; }
	public final @Override int getHeight() { return this.__wrapper.height; }
	// --------------------------------------------------
	public final @Nullable TElement getDraggingElement() { return this.__dragging; }
	public final @Nullable TElement getHoveredElement() { return this.__hovered; }
	public final @Nullable TElement getFocusedElement() { return this.__focused; }
	public final boolean setFocusedElement(TElement child) { return setFocusedElement(child, false); }
	public final boolean setFocusedElement(TElement child, boolean askToFocus)
	{
		// If child is not null and either it doesn't belong to this screen or
		// (if we're asking to focus and the child isn't focusable), return false
		if(child != null && (child.getParentTScreen() != this || (askToFocus && !child.isFocusable())))
			return false;
		//focus and return
		this.__focused = child;
		return true;
	}
	// ==================================================
	/**
	 * Initializes this {@link TScreen}.<br/>
	 * Add {@link TElement}s to this {@link TScreen} here.
	 */
	protected abstract void init();
	
	/**
	 * Invoked whenever this {@link TScreen} ticks.<p>
	 * Same as {@link Screen#tick()}.
	 * @see Screen#tick()
	 * @apiNote Do not tick children yourself, {@link TScreenWrapper}
	 * already does that automatically.
	 */
	protected @Virtual void tick() {}
	// --------------------------------------------------
	/**
	 * There's no need for a z-index for {@link TScreen}s, so here it returns 0.
	 */
	public final @Override float getZIndex() { return 0; }
	
	/**
	 * Renders this {@link TScreen}.<p>
	 * This is where the background and the {@link #children} are drawn.
	 */
	public @Virtual @Override void render(TDrawContext pencil)
	{
		renderBackground(pencil);
		renderChildren(pencil);
		
		//TODO - Context menu checking system is hard-coded; Find another more efficient way!
		if(!(getChildren().getLastChild() instanceof TContextMenuPanel))
			renderTooltip(pencil);
	}
	
	/**
	 * Renders this {@link TScreen}'s background.<p>
	 * Should be called before rendering children in {@link #render(TDrawContext)}.
	 * @param pencil The {@link TDrawContext}.
	 * @see #render(TDrawContext)
	 * @see #renderChildren(TDrawContext)
	 */
	public @Virtual void renderBackground(TDrawContext pencil)
	{
		this.__wrapper.Screen_super_renderBackground(pencil, pencil.mouseX, pencil.mouseY, pencil.deltaTime);
	}
	// --------------------------------------------------
	/**
	 * Renders all children elements of this {@link TScreen}.
	 * @apiNote Not recommended to call outside of {@link TScreen#render(TDrawContext)}.
	 */
	protected final void renderChildren(final TDrawContext pencil)
	{
		//begin rendering children
		__renderChildren(pencil, null, 0);
	}
	//
	private final void __renderChildren(final TDrawContext pencil, final TElement teParent, final int iteration)
	{
		//null check, depth check, and prepare to render
		if(iteration > TParentElement.MAX_CHILD_NESTING_DEPTH)
			return;
		
		//iterate all children of this screen
		//(using forEachChild for parent/child relation updates, but must NOT be a nested loop tho.)
		final TParentElement parent = (teParent != null ? teParent : this);

		//push parent scissors
		final int pX = parent.getX(), pY = parent.getY(), pXW = pX + parent.getWidth(), pYH = pY + parent.getHeight();
		pencil.enableScissor(pX, pY, pXW, pYH); //constrain to parent bounds
		
		//iterate children and render them
		parent.forEachChild(child -> //using `forEachChild` for the `__updateParent` error correction
		{
			//push context, matrices, and alpha
			pencil.pose().pushMatrix();
			//pencil.getMatrices().translate(0, 0, child.getZOffset()); -- FIXME - Broken
			pencil.pushTShaderColor(1, 1, 1, child.getAlpha());
			
			//render the child, and its children
			pencil.updateContext(child); //now rendering the child
			child.render(pencil);
			
			__renderChildren(pencil, child, iteration + 1); //now rendering the child's children
			
			pencil.updateContext(child); //now back to rendering the child
			child.postRender(pencil);
			
			//pop context, matrices, and alpha
			pencil.updateContext(this); //now back to rendering 'this'
			//
			pencil.popTShaderColor();
			pencil.pose().popMatrix();
		},
		false /*do not nest, as nesting will be handled manually*/);
		
		//pop parent scissors
		pencil.disableScissor();
	}
	
	/**
	 * Renders the {@link Tooltip} for the currently selected or hovered {@link TElement}.
	 * @see #getFocusedElement()
	 * @see #getHoveredElement()
	 * @apiNote Not recommended to call outside of {@link TScreen#render(TDrawContext)}.
	 */
	protected final void renderTooltip(final TDrawContext pencil)
	{
		//render hovered child tooltip if possible
		var target = (this.__focused != null && this.__focused.getTooltip() != null) ?
				this.__focused : this.__hovered;
		
		if(target != null && target.getTooltip() == null) //bubble to parent if needed
			target = target.findParentTElement(p -> (p.getTooltip() != null));
		if(target == null) return; //if target not found, return
		
		final var tt = target.getTooltip();
		final var ttp = target.getTooltipPositioner();
		if(tt != null && ttp != null)
			this.__wrapper.setTooltip(tt, ttp, true);
	}
	// --------------------------------------------------
	public @Virtual @Override boolean input(TInputContext inputContext)
	{
		//by default, TScreen will not handle any inputs; only its elements will.
		//keep in mind the input system uses the "event bubbling" system similar to
		//"Document Object Model", therefore TScreen will be the last to receive an input.
		return false;
	}
	
	/**
	 * Used for handling "tab navigation". Call this to manually
	 * trigger tab navigation in a given direction.
	 * @param reverse When 'false', the tab-navigation will focus on the
	 * next element, and the previous element when 'true'.
	 */
	protected final boolean inputTabNavigation(boolean reverse)
	{
		//if no elements are focused, then focus on an initial one (first or last)
		if(this.__focused == null && this.children.size() > 0)
		{
			this.__focused = reverse ?
				findLastChild(c -> c.isFocusable(), true) :
				findChild(c -> c.isFocusable(), true);
		}
		//else if an element is selected, go for next or previous
		else if(this.__focused != null)
		{
			//ensure the focused child is valid, and belongs to this screen
			if(this.__focused.findParent(p -> p == this) == null)
			{
				this.__focused = null;
				return inputTabNavigation(reverse); //go again and return
			}
			
			//navigate forwards/backwards
			do
			{
				this.__focused = reverse ? this.__focused.previous() : this.__focused.next();
			}
			// Skip over non-focusable elements
			while(this.__focused != null && !this.__focused.isFocusable());
		}
		//else if nothing works, return false
		else return false;
		
		//handle post-navigation stuff
		__postTabNavigation();
		
		//if something works, return true
		return true;
	}
	
	/**
	 * Temporary workaround code for assisting the {@link TParentElement} and {@link TContextMenuPanel}
	 * element's behaviors. Call this after the user performs any form of GUI tab navigation.
	 */
	//TODO - this method is a workaround; find other ways to do this instead of hard-coding
	@UnstableApi
	protected @Internal final void __postTabNavigation()
	{
		//for panel elements, scroll to the focused child if possible
		//(note: a hard-coded feature for panels; maybe not the brightest idea ¯\_(ツ)_/¯)
		if(this.__focused != null && (this.__focused.getParent() instanceof TPanelElement))
			((TPanelElement)this.__focused.getParent()).scrollToChild(this.__focused);
		
		//handle context menus focus
		final TContextMenuPanel cmp = ((TContextMenuPanel)findChild(c -> c instanceof TContextMenuPanel, false));
		if(cmp != null && cmp.closeIfNotFocused())
			setFocusedElement(cmp.getTarget(), false); //focus back on the target if the context menu closed
	}
	
	/**
	 * Invoked when the user drags files onto the currently opened {@link TScreen}.
	 * @param files The {@link Path} {@link Collection} of the files that were dragged.
	 * @return True if the event was handled, and false otherwise.
	 */
	public @Virtual boolean filesDragged(Collection<Path> files) { return false; }
	// ==================================================
	/**
	 * An internal method called by the {@link TScreenWrapper} whenever
	 * the mouse cursor moves and the hovered element needs to be re-calculated.
	 * @param mouseX The mouse cursor X position.
	 * @param mouseY The mouse cursor Y position.
	 */
	@Internal final void __recalculateHoveredChild(int mouseX, int mouseY)
	{
		//start with null
		final AtomicReference<TElement> hovered = new AtomicReference<>(null);
		
		//iterate all children, and assign hovered child if found
		forEachChild(child ->
		{
			// Check if the mouse coordinates are within the GUI element
			//and its parents
			if(
					!child.isHoverable() || !child.isVisible() ||
					(hovered.get() != null && child.getZIndex() < hovered.get().getZIndex()) ||
					!__isMouseInElementBounds(mouseX, mouseY, child) ||
					child.findParent(p -> !__isMouseInElementBounds(mouseX, mouseY, p)) != null)
				return;
			
			//assign
			hovered.set(child);
		},
		true /*do nest, and check all children*/);
		
		//assign new hovered; assign here to avoid another thread reading
		//the value while this is still going
		this.__hovered = hovered.get();
	}
	
	@Internal final boolean __isMouseInElementBounds(int mX, int mY, TParentElement child)
	{
		final int cX = child.getX(),
				cY = child.getY(),
				cXW = cX + child.getWidth(),
				cYH = cY + child.getHeight();
		return (mX >= cX && mX <= cXW && mY >= cY && mY <= cYH);
	}
	// ==================================================
	public static boolean hasKeyDown(int keyCode) { return isKeyDown(MC_CLIENT.getWindow().getWindow(), keyCode); }
	public static boolean hasWndDown() { return hasKeyDown(KEY_LWIN) || hasKeyDown(KEY_RWIN); }
	// ==================================================
}
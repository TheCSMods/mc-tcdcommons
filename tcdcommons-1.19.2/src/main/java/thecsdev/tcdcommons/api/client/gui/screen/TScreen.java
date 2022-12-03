package thecsdev.tcdcommons.api.client.gui.screen;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import thecsdev.tcdcommons.TCDCommons;
import thecsdev.tcdcommons.api.client.gui.TElement;
import thecsdev.tcdcommons.api.client.gui.TParentElement;
import thecsdev.tcdcommons.api.client.gui.other.TTooltipElement;
import thecsdev.tcdcommons.api.client.gui.panel.TContextMenuPanel;
import thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import thecsdev.tcdcommons.api.client.gui.util.TElementList;
import thecsdev.tcdcommons.api.util.SubjectToChange;

/**
 * The main {@link Screen} type used by the {@link TCDCommons} API.<br/>
 * <br/>
 * <b>Regarding method naming in classes and interfaces:</b><br/>
 * Names such as getX, getY, getWidth, getHeight, getChildren,
 * and any other method names used by Minecraft itself are
 * avoided because while they work fine in the development environment,
 * they will end up referencing non-existing methods in the build
 * environment. In other words, the existing methods will get
 * re-obfuscated and the interfaces' methods will end up
 * referencing non-existing code.<br/>
 * <u>Avoid using method and field names that are used by Minecraft.</u>
 */
public abstract class TScreen extends Screen implements TParentElement
{
	// ==================================================
	/**
	 * Used to keep track of the last mouse-clicked element
	 * for tab navigation. When the mouse clicks a child
	 * element, the tab navigation should continue from where
	 * the clicked child is.
	 */
	private TElement clickedTChild;
	
	/**
	 * Keeps track of the currently focused child element
	 * for Tab navigation.
	 */
	private TElement focusedTChild;
	
	/**
	 * Keeps track of the last hovered child for mouse
	 * event purposes. Makes sure the element on top
	 * gets to capture a mouse event before the element
	 * below it when there are overlapping elements.
	 */
	private TElement hoveredTChild;
	
	//keep track of mouse XY as it moves
	protected final Point cursorPosition;
	// --------------------------------------------------
	/** The list of children for this {@link TScreen}. */
	private final TElementList tchildren;
	
	/**
	 * This element is used for drawing tooltip texts for
	 * focused and hovered {@link TElement} children.<br/>
	 * <br/>
	 * To override the tooltip behavior, see {@link #__createTooltip()}.
	 */
	@SubjectToChange("May be remvoved.")
	protected TTooltipElement tooltipElement;
	// ==================================================
	protected TScreen(Text title)
	{
		super(title);
		this.tchildren = new TElementList(this);
		this.cursorPosition = new Point();
	}
	
	/**
	 * Used to create a tooltip element that will be
	 * assigned to {@link #tooltipElement}.<br/>
	 * <br/>
	 * <b>Must not return null.</b>
	 */
	@SubjectToChange("This way of doing it is too messy.")
	protected TTooltipElement __createTooltip() { return new TTooltipElement(getTpeWidth() / 2); }
	// --------------------------------------------------
	@SuppressWarnings("resource")
	public @Override void close()
	{
		//planning to implement something like this in `tcdcommons` later.
		//TODO - Maybe hard-coding this isn't the best approach, use a different method?
		if(getTChildren().removeIf(child -> (child instanceof TContextMenuPanel)))
			return;
		//on closed
		onClosed();
		//invoke super and have it close the window
		if(getClient().currentScreen == this) super.close();
	}
	// --------------------------------------------------
	/**
	 * Used to initialize this {@link Screen}. Use this method
	 * to create and define the children of this {@link TScreen}.
	 */
	protected abstract @Override void init();
	
	/**
	 * Re-initializes this screen by calling
	 * {@link GuiUtils#initScreen(Screen)}.
	 */
	public final void reInit() { GuiUtils.initScreen(this); }
	// ==================================================
	/**
	 * Called when this {@link TScreen} is opened with
	 * {@link MinecraftClient#setScreen(Screen)}.
	 */
	//Note: Do not rename this method. It is invoked
	//using reflection in the MixinMinecraftClient.
	protected void onOpened() {}
	
	/**
	 * Called by {@link MinecraftClient} when this {@link TScreen}
	 * is either closed or when {@link MinecraftClient#setScreen(Screen)}
	 * is called to open another {@link Screen} while this {@link TScreen} is open.
	 */
	protected void onClosed() {}
	// --------------------------------------------------
	/**
	 * This {@link Rectangle} is used as "cache" so
	 * {@link #getRenderingBoundingBox()} doesn't have
	 * to create a new {@link Rectangle} every time it is called.
	 */
	protected final Rectangle RENDER_RECT = new Rectangle(0,0,0,0);
	public @Override @Nullable Rectangle getRenderingBoundingBox() { return RENDER_RECT; }
	public @Override void updateRenderingBoundingBox() { RENDER_RECT.setSize(getTpeWidth(), getTpeHeight()); }
	// ==================================================
	/**
	 * Returns the {@link MinecraftClient} from {@link Screen#client}.
	 */
	public MinecraftClient getClient() { return this.client; }
	
	/**
	 * Returns the {@link TextRenderer} that belongs to this {@link Screen}.
	 */
	public TextRenderer getTextRenderer() { return this.textRenderer; }
	
	/**
	 * Returns the {@link ItemRenderer} that belongs to this {@link Screen}.
	 */
	public ItemRenderer getItemRenderer() { return this.itemRenderer; }
	// --------------------------------------------------
	public final @Override int getTpeX() { return 0; }
	public final @Override int getTpeY() { return 0; }
	public final @Override int getTpeWidth() { return this.width; }
	public final @Override int getTpeHeight() { return this.height; }
	public final @Override int getTpeEndX() { return TParentElement.super.getTpeEndX(); }
	public final @Override int getTpeEndY() { return TParentElement.super.getTpeEndY(); }
	public final @Override double getZIndex() { return 0; }
	// --------------------------------------------------
	/**
	 * Return true if the {@link InGameHud} should be
	 * rendered while this {@link TScreen} is open.<br/>
	 * By default, this returns true.
	 */
	public boolean shouldRenderInGameHud() { return true; }
	// ==================================================
	public @Override final TParentElement getTParent() { return null; }
	public @Override TElementList getTChildren() { return this.tchildren; }
	// --------------------------------------------------
	/**
	 * Returns the currently focused {@link TElement} child.
	 */
	public @Nullable TElement getFocusedTChild()
	{
		if(this.focusedTChild == null) return null;
		return focusedTChild.screen == this ? focusedTChild : null;
	}
	
	/**
	 * Sets the currently focused {@link TElement} child, without asking
	 * the already focused child if it wishes to lose it's focus.<br/>
	 * See {@link #setFocusedTChild(TElement, boolean, FocusOrigin)}.
	 * @param child The {@link TElement} child that should be focused.
	 * @return true - always.
	 */
	public boolean setFocusedTChild(@Nullable TElement child) { return setFocusedTChild(child, false, FocusOrigin.UNKNOWN); }
	
	/**
	 * Sets the currently focused {@link TElement} child,
	 * while also letting you ask the currently {@link #getFocusedTChild()}
	 * if it wishes to lose it's focus.
	 * @param child The {@link TElement} child that should be focused.
	 * @param askForDefocus Whether or not to ask the currently
	 * {@link #getFocusedTChild()} if it wishes to lose it's focus or not.
	 * @param defocusOrigin If asking the currently focused child for
	 * de-focus, this will be the {@link FocusOrigin} for losing the focus.
	 * @return true if the currently focused child approved of losing
	 * it's focus, and the focused child was changed
	 */
	public boolean setFocusedTChild(@Nullable TElement child, boolean askForDefocus, FocusOrigin defocusOrigin)
	{
		//ask for de-focus if needed
		if(askForDefocus && this.focusedTChild != null)
		{
			boolean approved = this.focusedTChild.canChangeFocus(defocusOrigin, false);
			if(!approved) return false;
		}
		
		//change focus
		clickedTChild = child;
		focusedTChild = child;
		
		//cancel dragging
		if(isDragging())
			setDragging(false);
		
		//return
		return true;
	}
	
	/**
	 * Returns the {@link TElement} child the cursor is hovering over.
	 */
	public @Nullable TElement getHoveredTChild() { return this.hoveredTChild; }
	
	/**
	 * Returns the {@link TElement} child that is currently being dragged.
	 */
	public @Nullable TElement getDraggingTChild() { return isDragging() ? this.clickedTChild : null; }
	
	public int getMouseX() { return this.cursorPosition.x; }
	public int getMouseY() { return this.cursorPosition.y; }
	// ==================================================
	/**
	 * Automatically called by Minecraft when ticking {@link Screen}s.
	 */
	@Override
	public void tick()
	{
		super.tick();
		//who knew performance would be an issue...
		//forEachChild(child -> { child.tick(); return false; }, true);
	}
	
	/**
	 * Renders this {@link TScreen}.
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the {@link Screen}.
	 * @param mouseY The Y mouse cursor position on the {@link Screen}.
	 * @param delta The time elapsed since the last render.
	 */
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		//prepare to render (reset hovered child)
		this.hoveredTChild = null;
		
		//render background
		this.renderBackground(matrices);
		
		//render children
		forEachChild(
				child -> { renderChildTElement(matrices, mouseX, mouseY, delta, child, true); return false; },
				child -> { renderChildTElement(matrices, mouseX, mouseY, delta, child, false); return false; },
				true);
		
		//render tooltip - TODO - Improve this system
		@SubjectToChange("Too messy.")
		TElement target = this.focusedTChild != null && this.focusedTChild.getTooltip() != null ?
				this.focusedTChild : this.hoveredTChild;
		
		if(target != null && this.tooltipElement != null && target.getTooltip() != null)
		{
			//assign tooltip (careful: setTooltip triggers a performance costly update)
			if(this.tooltipElement.getTooltip() != target.getTooltip())
				this.tooltipElement.setTooltip(target.getTooltip());
			//align tooltip
			this.tooltipElement.screen = this;
			this.tooltipElement.parent = null;
			this.tooltipElement.refreshPosition(target, mouseX, mouseY);
			//render tooltip
			matrices.push();
			matrices.translate(0, 0, this.tooltipElement.getZIndex());
			this.tooltipElement.render(matrices, mouseX, mouseY, delta);
			matrices.pop();
		}
	}
	
	/**
	 * Renders a {@link TElement} child individually.
	 * Called by {@link #render(MatrixStack, int, int, float)}.
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the {@link Screen}.
	 * @param mouseY The Y mouse cursor position on the {@link Screen}.
	 * @param delta The time elapsed since the last render.
	 * @param child The child to render.
	 * @param isPreRender If this is post render, set it to false, and to true otherwise.
	 * Setting this to false will call {@link TElement#postRender} instead.
	 */
	protected void renderChildTElement(MatrixStack matrices, int mouseX, int mouseY, float delta,
			TElement child, boolean isPreRender)
	{
		//check if the element is visible
		if(child == null || !child.isVisible())
			return;
		
		//keep track of scissor parameters
		TParentElement parent = child.getTParent();
		Rectangle parentBox = parent.getRenderingBoundingBox();
		Rectangle childBox = child.getRenderingBoundingBox();
		if(parentBox == null || childBox == null) return;
		
		//update hovered child (only during render, not post render)
		if(isPreRender && !child.isClickThrough() && child.isEnabledAndVisible() &&
				(this.hoveredTChild == null || child.getZIndex() >= this.hoveredTChild.getZIndex()))
		{
			if(childBox != null && childBox.contains(this.cursorPosition)
					/*mouseX >= childBox.x &&
					mouseY >= childBox.y &&
					mouseX <= childBox.x + childBox.width &&
					mouseY <= childBox.y + childBox.height*/)
				hoveredTChild = child;
		}
		
		//push the matrices
		matrices.push();
		matrices.translate(0, 0, child.getZIndex());
		
		//and then render the child
		CURRENT_CHILD_SCISSORS = parentBox;
		GuiUtils.applyScissor(client, parentBox.x, parentBox.y, parentBox.width, parentBox.height, () ->
		{
			if(isPreRender) child.render(matrices, mouseX, mouseY, delta);
			else child.postRender(matrices, mouseX, mouseY, delta);
		});
		CURRENT_CHILD_SCISSORS = null;
		
		//pop the matrices and continue
		matrices.pop();
		return;
	}
	
	/**
	 * Used to keep track of the scissors settings applied to the currently rendered child.<br/>
	 * See {@link #renderChildTElement(MatrixStack, int, int, float, TElement, boolean)}.<br/>
	 * <br/>
	 * @see {@link #resetScissors()}.
	 */
	protected @Nullable Rectangle CURRENT_CHILD_SCISSORS;
	
	/**
	 * Have you used {@link GuiUtils#enableScissor(MinecraftClient, int, int, int, int)}
	 * or any other method that changes the scissor settings while rendering a {@link TElement}?
	 * If so, then you can use this method to reset the scissor settings back to default.
	 */
	public void resetScissors()
	{
		//disable
		GuiUtils.disableScissor();
		if(CURRENT_CHILD_SCISSORS == null) return;
		//re-enable CURRENT_CHILD_SCISSORS
		GuiUtils.enableScissor(client,
				CURRENT_CHILD_SCISSORS.x, CURRENT_CHILD_SCISSORS.y,
				CURRENT_CHILD_SCISSORS.width, CURRENT_CHILD_SCISSORS.height);
	}
	// ==================================================
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{		
		//forward the event to the children, until a child captures the click
		//also, update mouse XY
		int mX = (int)mouseX, mY = (int)mouseY;
		
		//obtain the captor
		TElement captor = this.hoveredTChild;
		while(captor != null && captor.isClickThrough())
			captor = captor.parent;
		
		//call the mouse pressed event on the captor
		boolean clickAccepted = (captor != null && captor.isEnabledAndVisible() && captor.mousePressed(mX, mY, button));
		
		//if the clicked child is not the focused one
		if(captor != this.focusedTChild)
		{
			//if the captor accepted the click, ask the captor if it wants focus
			if(clickAccepted && (captor == null || captor.canChangeFocus(FocusOrigin.MOUSE_CLICK, true)))
				//if so, then attempt to focus on it
				setFocusedTChild(captor, true, FocusOrigin.MOUSE_CLICK);
			else
			{
				//if the captor does not want focus, then clear the focus 
				setFocusedTChild(null, true, FocusOrigin.MOUSE_CLICK);
			}
		}
		
		//this is not just for drag, but for Tab navigation as well
		//must be placed here
		this.clickedTChild = captor;
		
		//apply drag (only if the click wasn't accepted)
		if(!clickAccepted && button == 0) setDragging(true);
		
		//return the result
		return captor != null;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		//check for drag
		if(!isDragging() || (this.clickedTChild == null || this.clickedTChild.screen != this))
			return false;
		
		//prepare to call the event handler
		TElement nextTry = this.clickedTChild;
		boolean dragged = false;
		
		//call the event, and if it rejects, call the event on the parent
		while(!dragged && nextTry != null)
		{
			dragged = //nextTry.isEnabled() &&
					nextTry.isVisible() &&
					!nextTry.isClickThrough() &&
					nextTry.mouseDragged(mouseX, mouseY, deltaX, deltaY, button);
			if(!dragged) nextTry = nextTry.parent;
		}
		
		//return true if the event was handled by either the
		//clicked child, or by one of it's parents
		return dragged;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		//call event handler on the clicked child
		boolean b0 = (this.clickedTChild != null) &&
				this.clickedTChild.isEnabled() &&
				this.clickedTChild.mouseReleased((int)mouseX, (int)mouseY, button);
		
		//clear drag and return
		setDragging(false);
		return b0;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		//the first potential target is the hovered child
		TElement captor = this.hoveredTChild;
		while(captor != null && captor.isClickThrough())
			captor = captor.parent;
		
		while(captor != null)
		{
			//ask the captor to process the mouse scroll
			if(captor.isEnabledAndVisible() && captor.mouseScrolled(getMouseX(), getMouseY(), (int)amount))
				//if it does, return true
				return true;
			//else ask the captor's parent to process it
			else captor = captor.parent;
		}
		
		//if nobody processes it, return false
		return false;
	}
	// --------------------------------------------------
	public @Override void mouseMoved(double mouseX, double mouseY)
	{
		//update mouse XY
		this.cursorPosition.setLocation(mouseX, mouseY);
		super.mouseMoved(mouseX, mouseY);
	}
	// --------------------------------------------------
	public @Override boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//if escape is pressed
		if(keyCode == 256 && shouldCloseOnEsc())
		{
			this.close();
			return true;
		}
		//if tab is pressed
		else if(keyCode == 258 && getTChildren().size() > 0)
		{
			//define the TElement that we'll focus on next
			boolean shift = hasShiftDown();
			FocusOrigin origin = FocusOrigin.TAB /*shift ? FocusOrigin.TAB_BACKWARD : FocusOrigin.TAB_FORWARD*/;
			TElement nextFocus = null;
			
			//if there is no clicked child and no focused child,
			//focus on the first child of this screen
			if(this.clickedTChild == null && this.focusedTChild == null)
			{
				if(!shift) nextFocus = getTChildren().get(0);
				else nextFocus = getLastTChild(true);
			}
			
			//if there is no focused child, but there is a clicked child,
			//focus on the last clicked child
			else if(this.clickedTChild != null && this.focusedTChild == null)
				nextFocus = this.clickedTChild;
			
			//if all above checks fail, focus on the previous/next child
			//of the already focused child
			else if(this.focusedTChild != null)
				nextFocus = shift ? this.focusedTChild.previous() : this.focusedTChild.next();
			
			//attempt to focus on the next element. if it refuses,
			//keep cycling children and re-trying on the following elements
			while(nextFocus != null)
			{
				//check if the next element is enabled, and then
				//ask the next element if it wishes to accept focus
				boolean accepted = !nextFocus.isClickThrough() &&
						nextFocus.isEnabledAndVisible() &&
						nextFocus.canChangeFocus(origin, true);
				//if it does, break
				if(accepted) break;
				//and if it doesn't, move on to the next one
				else nextFocus = shift ? nextFocus.previous() : nextFocus.next();
			}
			
			//set the next focused child TElement and then return
			this.setFocusedTChild(nextFocus);
			//this.setFocusedTChild(nextFocus, true, origin); -- don't ask
			
			//scroll to the child if inside of a panel
			//TODO - Maybe hard-coding this isn't the best approach, use a different method?
			__triggerScrollTo(nextFocus);
			
			//return
			return true;
		}
		
		//forward the event to the focused child
		return this.focusedTChild != null &&
				this.focusedTChild.isEnabled() &&
				this.focusedTChild.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@SubjectToChange(value = "Temporary workaround. Likely to be removed.")
	protected void __triggerScrollTo(TElement child)
	{
		if(child == null) return;
		//obtain the first parent panel
		TElement panel = child.parent;
		//make sure the parent is a scrollable panel.
		//if it isn't check the parent's parent
		while((panel instanceof TPanelElement) && ((TPanelElement)panel).getScrollFlags() == 0)
			panel = panel.parent;
		//if a scrollable panel is found, scroll to the focused child
		if(panel instanceof TPanelElement)
			((TPanelElement)panel).scrollToChild(child);
	}
	
	@Override
	public boolean charTyped(char character, int modifiers)
	{
		return this.focusedTChild != null &&
				this.focusedTChild.isEnabled() &&
				this.focusedTChild.charTyped(character, modifiers);
	}
	// ==================================================
	// The section below contains overridden Screen methods, some
	// oh which are deprecated, while others are repurposed.
	// ==================================================
	/**
	 * Repurposed and redirected to {@link #clearTChildren()}.
	 * Super will still be called just in case.
	 */
	@Override
	protected final void clearChildren()
	{
		super.clearChildren();
		//very important, as it is called by init(...)
		//when initializing and resizing the window
		this.clearTChildren();
		//update tooltip and the bounding box
		this.tooltipElement = __createTooltip();
		updateRenderingBoundingBox();
	}
	
	/**
	 * Unsupported and unused. Please use {@link #addTChild(TElement)} instead.
	 * @param <T> The {@link Drawable} type.
	 * @param drawable The child to add.
	 * @return The given drawable, without actually adding it.
	 */
	@Override
	@Deprecated
	protected final <T extends Drawable> T addDrawable(T drawable) { return drawable; }
	
	/**
	 * Unsupported and unused. Please use {@link #addTChild(TElement)} instead.
	 * @param <T> The {@link Selectable} {@link Drawable} {@link Element} type.
	 * @param drawableElement The child to add.
	 * @return The given drawable child, without actually adding it.
	 */
	@Override
	@Deprecated
	protected final <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) { return drawableElement; }
	
	/**
	 * Unsupported and unused. Please use {@link #addTChild(TElement)} instead.
	 * @param <T> The {@link Selectable} {@link Element} type.
	 * @param child The child to add.
	 * @return The given selectable child, without actually adding it.
	 */
	@Override
	@Deprecated
	protected final <T extends Element & Selectable> T addSelectableChild(T child) { return child; }
	
	/**
	 * Unsupported and unused. Please use {@link #getTChildren()} instead.
	 * @param <?> The {@link Element} type.
	 * @return A new empty list.
	 */
	@Override
	@Deprecated
	public final List<? extends Element> children() { return Lists.newArrayList(); }
	
	/**
	 * Unsupported and unused. Please use {@link #getFocusedTChild()} instead.
	 * @return super.{@link #getFocused()}
	 */
	@Override
	public final Element getFocused() { return super.getFocused(); }
	
	/**
	 * Unsupported and unused. Please use {@link #setFocusedTChild(TElement)} instead.
	 * @param focused The element the {@link Screen} should focus on.
	 */
	@Override
	@Deprecated
	public final void setFocused(Element focused)
	{
		super.setFocused(focused);
		//called by init(...) for clearing focus
		//when initializing and resizing the window
		if(focused == null)
			this.setFocusedTChild(null);
	}
	
	/**
	 * Unsupported and unused.
	 * @param lookForwards Is the shift key up?
	 */
	@Deprecated
	public final @Override boolean changeFocus(boolean lookForwards) { return super.changeFocus(lookForwards); }
	
	/**
	 * Called by {@link MinecraftClient} when the screen
	 * changes from this screen. Repurposed and redirected
	 * to {@link #onClosed()}.<br/><br/>
	 * See {@link #onClosed()}.
	 */
	@Deprecated
	public final @Override void removed() { super.removed(); }
	// ==================================================
}
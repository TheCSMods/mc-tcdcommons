package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofCharType;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofKeyboardPR;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofMouseCR;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofMouseDrag;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofMouseDragEnd;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofMouseMove;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.ofMouseScroll;
import static net.minecraft.client.util.InputUtil.GLFW_KEY_ESCAPE;
import static net.minecraft.client.util.InputUtil.GLFW_KEY_TAB;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputKeyboardKey;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IStatsListener;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

/**
 * The {@link TScreenWrapper} serves as an adapter for the {@link TScreen}
 * class. This class extends Minecraft's {@link Screen} class and translates
 * calls from the Minecraft engine into calls on the {@link TScreen} instances.
 *
 * <p>The purpose of this class is to isolate {@link TScreen} from Minecraft's
 * GUI code, making the mod more resilient to changes in the game's code. This
 * isolation makes {@link TScreen} act like a {@link Screen}, but in a more controlled 
 * and independent way, similar to how custom rendering engines interact with rendering APIs.
 *
 * <p>This class should remain thin, serving only as a pass-through layer to
 * {@link TScreen}. All interactions with the {@link Screen} class that the mod
 * needs should be encapsulated within this class, keeping {@link TScreen}
 * unaware of Minecraft's GUI code.
 * 
 * <p><b>Important:</b><br/>
 * If you wish to implement listener interfaces such as {@link IStatsListener},
 * then you may extend this class and create your own {@link TScreenWrapper} implementation.
 * However, <b>avoid interacting with Minecraft's {@link Screen} code</b> as much as possible!
 */
public @Virtual class TScreenWrapper<T extends TScreen> extends Screen
{
	// ==================================================
	protected final T target;
	// --------------------------------------------------
	protected @Nullable Window clientWindow;
	// ==================================================
	public TScreenWrapper(T target)
	{
		super(target.getTitle());
		this.target = target;
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link #target} {@link TScreen} for this {@link TScreenWrapper}.
	 */
	public final T getTargetTScreen() { return this.target; }
	public final void Screen_super_close() { super.close(); }
	// ==================================================
	public final @Override Text getTitle() { return this.target.getTitle(); }
	public final @Override boolean shouldPause() { return this.target.shouldPause(); }
	
	/**
	 * Returns <b>false</b> to fix the ESC input handling bug that
	 * prevents GUI elements from handling the key-press.<br/>
	 * Please refer to {@link TScreen#shouldCloseOnEsc()} instead.
	 */
	public final @Override boolean shouldCloseOnEsc() { return false; } //ESC input handling bug fix
	
	/**
	 * Invokes {@link TScreen#close()} on the {@link #getTargetTScreen()}.
	 * @see #Screen_super_close()
	 * @apiNote <b>Do not</b> call {@link #close()} from a {@link TScreen},
	 * as that will result in a {@link StackOverflowError}.
	 * Use {@link #Screen_super_close()} instead.
	 */
	public final @Override void close() { this.target.close(); }
	// --------------------------------------------------
	protected final @Override void init()
	{
		//clear children first before initializing again
		this.target.getChildren().clear();
		//assign necessary values
		this.target.client = this.client;
		this.clientWindow = this.client.getWindow();
		//init target TScreen
		this.target.init();
		//also init super
		super.init();
	}
	protected final @Override void clearChildren()
	{
		super.clearChildren();
		this.target.getChildren().clear();
	}
	// --------------------------------------------------
	public final @Override void tick()
	{
		//tick super
		super.tick();
		
		//recalculate mouse hover
		//(this is done here because GUI elements can change over time, independently of mouse movement)
		int i = (int)(this.client.mouse.getX() * this.clientWindow.getScaledWidth() / this.clientWindow.getWidth());
	    int j = (int)(this.client.mouse.getY() * this.clientWindow.getScaledHeight() / this.clientWindow.getHeight());
	    this.target.setMousePosition(i, j);
	    this.target.__recalculateHoveredChild(i, j);
	    
	    //tick target TScreen and its children
	    this.target.tick();
	    this.target.forEachChild(c -> c.tick(), true);
	}
	// --------------------------------------------------
	public final void Screen_super_renderBackground(DrawContext context, int mouseX, int mouseY, float delta) { super.renderBackground(context, mouseX, mouseY, delta); }
	public final @Override void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
	{
		/* This method started causing visual bugs, and as such, has been removed.
		 * The reason for the bugs is because `super.render` is used in `#render`
		 * Please use TScreen#renderBackground instead
		 */
	}
	public final @Override void renderPanoramaBackground(DrawContext context, float delta) { super.renderPanoramaBackground(context, delta); }
	public final @Override void applyBlur() { super.applyBlur(); }
	public final @Override void renderDarkening(DrawContext context) { super.renderDarkening(context); }
	//
	public final @Override void render(DrawContext drawContext, int mouseX, int mouseY, float deltaTime)
	{
		//TODO - possibly optimize drag/focus/hover flag checks
		//verify target's flags
		final var t = this.target;
		if(t.__dragging != null && t.__dragging.getParentTScreen() != t)
			t.__dragging = null;
		else if(t.__focused != null && t.__focused.getParentTScreen() != t)
			t.__focused = null;
		else if(t.__hovered != null && t.__hovered.getParentTScreen() != t)
			t.__hovered = null;
		
		//create the TDrawContext
		final var pencil = TDrawContext.of(drawContext, mouseX, mouseY, deltaTime);
		
		//render the target screen
		pencil.updateContext(getTargetTScreen());
		t.render(pencil);
		
		//render super on top of the target screen
		super.render(pencil, mouseX, mouseY, deltaTime);
	}
	// ==================================================
	public final @Override boolean keyPressed(int keyCode, int scanCode, int modifiers) { return __onTKeyPR(keyCode, scanCode, modifiers, true); }
	public final @Override boolean keyReleased(int keyCode, int scanCode, int modifiers) { return __onTKeyPR(keyCode, scanCode, modifiers, false); }
	//
	private final boolean __onTKeyPR(int keyCode, int scanCode, int modifiers, boolean isDown)
	{
		//Screen takes priority here, as its elements are rendered on top of TElement-s
		if(isDown ? super.keyPressed(keyCode, scanCode, modifiers) : super.keyReleased(keyCode, scanCode, modifiers))
			return true;
		
		//if the Screen doesn't handle it, forward it to TScreen
		else if(input(ofKeyboardPR(new InputKeyboardKey(keyCode, scanCode, modifiers), isDown)))
			return true;
		
		//lastly, handle ESC key-press
		else if(keyCode == GLFW_KEY_ESCAPE && isDown && this.target.shouldCloseOnEsc()) { close(); return true; }
		else return false;
	}
	// --------------------------------------------------
	public final @Override boolean charTyped(char chr, int modifiers)
	{
		//Screen takes priority here, as its elements are rendered on top of TElement-s
		if(super.charTyped(chr, modifiers))
			return true;
		//if the Screen doesn't handle it, forward it to TScreen
		return input(ofCharType(chr, modifiers));
	}
	// --------------------------------------------------
	public final @Override boolean mouseClicked(double mouseX, double mouseY, int button) { return __onTMouseCR(mouseX, mouseY, button, true); }
	
	public final @Override boolean mouseReleased(double mouseX, double mouseY, int button) { return __onTMouseCR(mouseX, mouseY, button, false); }
	//
	private final boolean __onTMouseCR(double mouseX, double mouseY, int button, boolean isDown)
	{
		//Screen takes priority here, as its elements are rendered on top of TElement-s
		if(isDown ? super.mouseClicked(mouseX, mouseY, button) : super.mouseReleased(mouseX, mouseY, button))
			return true;
		//if the Screen doesn't handle it, forward it to TScreen
		return input(ofMouseCR(button, isDown));
	}
	// --------------------------------------------------
	public final @Override void mouseMoved(double mouseX, double mouseY)
	{
		//update the target screen's mouse position
		this.target.setMousePosition((int)mouseX, (int)mouseY);
		
		//because the return type is not a boolean,
		//both take priority here, starting with TScreen
		input(ofMouseMove(mouseX, mouseY));
		super.mouseMoved(mouseX, mouseY);
	}
	
	public final @Override boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		//Screen takes priority here, as its elements are rendered on top of TElement-s
		if(super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
			return true;
		//if the Screen doesn't handle it, forward it to TScreen
		return input(ofMouseDrag(mouseX, mouseY, deltaX, deltaY, this.target.__draggingButton));
	}
	// --------------------------------------------------
	public final @Override boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount)
	{
		//Screen takes priority here, as its elements are rendered on top of TElement-s
		if(super.mouseScrolled(mouseX, mouseY, hAmount, vAmount))
			return true;
		//if the Screen doesn't handle it, forward it to TScreen
		return input(ofMouseScroll(mouseX, mouseY, hAmount, vAmount));
	}
	// --------------------------------------------------
	/**
	 * An input handling system that mimics the mechanics of DOM.<br/>
	 * By default, inputs are "forwarded" to the appropriate "target" GUI elements,
	 * after which the events "bubbles" towards the "root" aka {@link TScreen}.
	 * @param inputContext The {@link TInputContext}.
	 */
	protected final boolean input(TInputContext inputContext)
	{
		// # ==================== PHASE: BROADCAST ===============
		this.target.input(inputContext, TInputContext.InputDiscoveryPhase.BROADCAST);
		this.target.forEachChild(c -> c.input(inputContext, TInputContext.InputDiscoveryPhase.BROADCAST), true);
		
		// # ==================== PHASE: PREEMPT =================
		if(this.target.input(inputContext, TInputContext.InputDiscoveryPhase.PREEMPT) ||
				this.target.findChild(c -> c.input(inputContext, TInputContext.InputDiscoveryPhase.PREEMPT), true) != null)
			return true;
		
		// # ==================== PHASE: MAIN ====================
		//handle based on input type
		switch(inputContext.getInputType())
		{
		// -------------------- MOUSE RELATED
			//mouse move bubbles starting from hovered element, with no extra handling
			case MOUSE_MOVE: return inputMainPhaseBubble(this.target.__hovered, inputContext) != null;
			//mouse drag is only forwarded to the current dragging element; nothing else
			case MOUSE_DRAG: return this.target.__dragging != null && inputMainPhase(this.target.__dragging, inputContext);
			//mouse click and release bubble starting from hovered element,
			//but with some extra "dragged element" handling as well
			case MOUSE_PRESS:
			{
				//obtain the initially focused element at this point, so we can cross-check
				//it and find out if an event handler set the focus to another element
				final var initialFocused = this.target.__focused;
				
				//first bubble the event, and obtain the result
				final var result = inputMainPhaseBubble(this.target.__hovered, inputContext);
				final var resultEl = (result instanceof TElement) ? (TElement)result : null;
				
				//then if a TElement handled it, and there are no elements being dragged,
				//assign the result as the "dragging element"
				if(this.target.__dragging == null && resultEl != null)
				{
					this.target.__dragging = resultEl;
					this.target.__draggingButton = inputContext.getMouseButton();
				}
				
				//also, if the result element is null, or not focusable, clear all focus;
				//otherwise assign the result element as the focused element
				//(only if the focus didn't get changed by the event target handler)
				if(this.target.__focused == initialFocused)
				{
					if(resultEl == null || !resultEl.isFocusable())
						this.target.__focused = null;
					else this.target.__focused = resultEl;
				}
				
				//and finally, return the click handling result
				return (result != null);
			}
			case MOUSE_RELEASE:
			{
				//first bubble the event, and obtain the result
				final var result = inputMainPhaseBubble(this.target.__hovered, inputContext);
				//then, if the user released the dragging button, clear all dragging flags
				if(inputContext.getMouseButton() == this.target.__draggingButton)
				{
					//if an element was indeed being dragged,
					//let it know that it no longer will be
					if(this.target.__dragging != null)
						//note: even tho the return value here is ignored for now,
						//doen't mean it will be in the future; keep returning true if you handled it
						inputMainPhase(this.target.__dragging, ofMouseDragEnd(this.target.__draggingButton));
					//clear the dragging flags
					this.target.__dragging = null;
					this.target.__draggingButton = -1;
				}
				//and finally, return the mouse release handling result
				return (result != null);
			}
			//mouse scroll bubbles starting from hovered element, with no extra handling
			case MOUSE_SCROLL: return inputMainPhaseBubble(this.target.__hovered, inputContext) != null;
			
		// -------------------- KEYBOARD RELATED
		//for keyboard-related inputs, we "bubble" the event starting from the currently focused element
			//and for key press, we just handle some additional tab navigation logic
			case KEY_PRESS:
			{
				//first check if tab navigation is involved
				final var isTab = inputContext.getKeyboardKey().keyCode == GLFW_KEY_TAB;
				
				//if tab navigation is involved, and user has CTRL down, force the tab navigation
				/*@Deprecated -- don't do this for now; make the GUI elements responsible for this
				if(isTab && Screen.hasControlDown())
					return this.target.inputTabNavigation(Screen.hasShiftDown());*/
				
				//at this stage, the focused target may handle the input,
				//regardless of tab navigation's involvement
				if(inputMainPhaseBubble(this.target.__focused, inputContext) != null)
					return true;
				
				//and if tab navigation is involved, and isn't forced, and
				//the target element doesn't handle the input, do the tab navigation
				if(isTab) return this.target.inputTabNavigation(Screen.hasShiftDown());
				else return false; //IMPORTANT to break out of here
			}
			case KEY_RELEASE:
			case CHAR_TYPE:
				return inputMainPhaseBubble(this.target.__focused, inputContext) != null;
			
		// -------------------- EVERYTHING ELSE
			//by default, break, if the input type wasn't handled
			default: break;
		}
		
		// # ==================== PHASE: DONE ====================
		//return false by default
		return false;
	}
	
	/**
	 * Forwards an input to a target element, after which the input
	 * "bubbles" towards the "root" {@link TParentElement}.
	 * @param targetElement The initial target {@link TParentElement} that is supposed to handle the input
	 * @param inputContext The {@link TInputContext}
	 * @return The {@link TParentElement} that eventually ended up handing the input event
	 */
	protected final @Internal @Nullable TParentElement inputMainPhaseBubble
	(TParentElement targetElement, TInputContext inputContext)
	{
		//first, we require that there is a proper input to begin with
		Objects.requireNonNull(inputContext);
		
		//then, we make sure there is a target element;
		//if there isn't, then we forward the input to the target TScreen
		if(targetElement == null)
			return inputMainPhase(this.target, inputContext) ? this.target : null;
		
		//we then forward the input to the target element...
		else if(inputMainPhase(targetElement, inputContext))
			return targetElement;
		
		//...and if the target element doesn't handle it, forward the
		//input to its parent hierarchy, parent by parent
		else return (targetElement.findParent(p -> inputMainPhase(p, inputContext)));
	}
	
	/**
	 * Similar to {@link #inputMainPhaseBubble(TParentElement, TInputContext)},
	 * except the input is not "bubbled" to parent elements.
	 */
	protected final @Internal boolean inputMainPhase
	(TParentElement targetElement, TInputContext inputContext)
	{
		//first, we require that there is a proper input to begin with
		Objects.requireNonNull(inputContext);
		if(targetElement == null) return false;
		
		//forward
		if(targetElement.input(inputContext, TInputContext.InputDiscoveryPhase.MAIN))
			return true;
		else return targetElement.input(inputContext);
	}
	// ==================================================
	public final @Override void filesDragged(List<Path> paths)
	{
		//TScreen takes priority here...
		if(this.target.filesDragged(paths))
			return;
		//...followed by Screen if TScreen doesn't handle it.
		super.filesDragged(paths);
	}
	// --------------------------------------------------
	public final @Override void resize(MinecraftClient client, int width, int height)
	{
		//clear target's mouse position variable,
		//because it is unknown where the cursor is now, relative to the new screen size
		this.target.setMousePosition(0, 0);
		
		//clear the hovered element whenever a window resize takes place;
		//same reason as clearing the mouse position
		this.target.__hovered = null;
		//also clear focused element, as the window will re-init with
		//new gui element instances
		this.target.__focused = null;
		
		//call super
		super.resize(client, width, height);
	}
	// ==================================================
}
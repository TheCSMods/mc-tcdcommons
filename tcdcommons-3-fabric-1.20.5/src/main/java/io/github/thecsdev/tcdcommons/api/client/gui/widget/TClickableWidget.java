package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static net.minecraft.client.util.InputUtil.GLFW_KEY_ENTER;
import static net.minecraft.client.util.InputUtil.GLFW_KEY_KP_ENTER;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.interfaces.IEnableStateProviderSetter;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorPressableWidget;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.util.Identifier;

/**
 * An abstract class representing a clickable widget that extends {@link TElement}.
 * <p>
 * A {@link TClickableWidget} can be interacted with either by clicking it with the mouse or
 * by pressing the enter key when it is focused. This class provides methods to handle the
 * click action and to process input events.
 * <p>
 * Subclasses of TClickableWidget need to implement the {@link #onClick()} method to define
 * the behavior when the widget is clicked.
 * 
 * @see TElement
 */
public abstract class TClickableWidget extends TElement implements IEnableStateProviderSetter
{
	// ==================================================
	public static final int BUTTON_TEXTURE_SLICE_SIZE = 3;
	
	/**
	 * Vanilla-game button textures.
	 */
	public static final ButtonTextures BUTTON_TEXTURES = AccessorPressableWidget.getButtonTextures();
	
	/**
	 * {@link TCDCommons}'s widget textures image.
	 */
	public static final Identifier T_WIDGETS_TEXTURE = new Identifier(getModID(), "textures/gui/widgets.png");
	// --------------------------------------------------
	protected boolean enabled;
	// --------------------------------------------------
	public TEvent<TClickableWidgetEvent_Clicked> eClicked = TEventFactory.createLoop();
	// ==================================================
	public TClickableWidget(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.enabled = true;
	}
	// --------------------------------------------------
	public @Virtual @Override boolean isFocusable() { return this.enabled; }
	public final @Override boolean getEnabled() { return this.enabled; }
	public @Virtual @Override void setEnabled(boolean enabled) { this.enabled = enabled; }
	// ==================================================
	/**
	 * Clicks this {@link TClickableWidget}.
	 * @param playClickSound Whether or not to play the GUI button click sound.
	 */
	public final void click(boolean playClickSound)
	{
		//play the click-y sound
		if(playClickSound) GuiUtils.playClickSound();
		//call on-click
		onClick();
		//invoke post-click event
		this.eClicked.invoker().invoke(this);
	}
	
	/**
	 * Called when this {@link TClickableWidget} is clicked,
	 * either by a mouse or a keyboard input, or by {@link #click(boolean)}.
	 */
	protected abstract void onClick();
	// ==================================================
	public @Override boolean input(final TInputContext inputContext)
	{
		//handle super, just in case it ever has a use
		if(super.input(inputContext)) return true;
		//requires a parent screen
		else if(getParentTScreen() == null) return false;
		//don't handle input if disabled
		else if(!isEnabled()) return false;
		
		//check for input type
		switch(inputContext.getInputType())
		{
			case MOUSE_PRESS:
				//break if the user pressed any button other than LMB
				final int btn = inputContext.getMouseButton();
				//handle click mouse buttons
				if(btn == 0) { click(true); return true; }
				else if(btn == 1)
				{
					final var contextMenu = createContextMenu();
					if(contextMenu != null)
					{
						contextMenu.open();
						GuiUtils.playClickSound();
						return true;
					}
					return false;
				}
				//unsupported mouse buttons will return false
				return false;
			case KEY_RELEASE:
				//break if the user pressed any key other than enter
				//257 - ENTER; 335 - NUMPAD ENTER; 32 - SPACE;
				final int keyCode = inputContext.getKeyboardKey().keyCode;
				if(!(keyCode == GLFW_KEY_ENTER || keyCode == GLFW_KEY_KP_ENTER/* || keyCode == 32*/)) break;
				//click
				click(true);
				return true;
			//don't handle other input types
			default: break;
		}
		
		//if the input wasn't handled, return false
		return false;
	}
	// ==================================================
	public static interface TClickableWidgetEvent_Clicked { public void invoke(TClickableWidget element); }
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.util;

import java.awt.geom.Point2D;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

/**
 * Holds the information about a given user input.
 */
public final class TInputContext
{
	// ==================================================
	private final InputType inputType;
	private @Nullable InputKeyboardKey keyboardKey;
	private @Nullable Character typedChar;
	private @Nullable Integer mouseButton;
	private @Nullable Point2D.Double mousePos;
	private @Nullable Point2D.Double mouseDelta;
	private @Nullable Point2D.Double scrollAmount;
	//private @Deprecated @Nullable Collection<Path> draggedFiles;
	// ==================================================
	private TInputContext(InputType inputType) { this.inputType = Objects.requireNonNull(inputType); }
	// --------------------------------------------------
	public final InputType getInputType() { return this.inputType; }
	
	@UsedFor({InputType.KEY_PRESS, InputType.KEY_RELEASE})
	public final @Nullable InputKeyboardKey getKeyboardKey() { return this.keyboardKey; }
	
	@UsedFor(InputType.CHAR_TYPE)
	public final @Nullable Character getTypedChar() { return this.typedChar; }
	
	@UsedFor({InputType.MOUSE_PRESS, InputType.MOUSE_RELEASE, InputType.MOUSE_DRAG, InputType.MOUSE_DRAG_END})
	public final @Nullable Integer getMouseButton() { return this.mouseButton; }
	
	@UsedFor({InputType.MOUSE_MOVE, InputType.MOUSE_DRAG, InputType.MOUSE_SCROLL})
	public final @Nullable Point2D.Double getMousePosition() { return this.mousePos; }
	
	@UsedFor(InputType.MOUSE_DRAG)
	public final @Nullable Point2D.Double getMouseDelta() { return this.mouseDelta; }
	
	@UsedFor(InputType.MOUSE_SCROLL)
	public final @Nullable Point2D.Double getScrollAmount() { return this.scrollAmount; }
	
	/*@Deprecated @UsedFor(InputType.FILE_DRAG)
	public final @Nullable Collection<Path> getDraggedFiles(){ return this.draggedFiles; }*/
	// ==================================================
	/**
	 * "Of keyboard press/release".
	 * @param key The {@link InputKeyboardKey} that was pressed or released.
	 * @param isDown true = {@link InputType#KEY_PRESS}; false = {@link InputType#KEY_RELEASE}.
	 */
	public static TInputContext ofKeyboardPR(InputKeyboardKey key, boolean isDown)
	{
		final var result = new TInputContext(isDown ? InputType.KEY_PRESS : InputType.KEY_RELEASE);
		result.keyboardKey = Objects.requireNonNull(key);
		return result;
	}
	
	/**
	 * "Of character type"
	 * @param character The {@link Character} the user typed.
	 * @param modifiers The character modifiers.
	 */
	public static TInputContext ofCharType(char character, int modifiers)
	{
		final var result = new TInputContext(InputType.CHAR_TYPE);
		result.typedChar = character;
		result.keyboardKey = new InputKeyboardKey(0, 0, modifiers); //here? eh whatever...
		return result;
	}
	
	/**
	 * "Of mouse click/release".
	 * @param mouseButton The mouse button that was clicked or released.
	 * @param isDown true = {@link InputType#MOUSE_PRESS}; false = {@link InputType#MOUSE_RELEASE}
	 */
	public static TInputContext ofMouseCR(int mouseButton, boolean isDown)
	{
		final var result = new TInputContext(isDown ? InputType.MOUSE_PRESS : InputType.MOUSE_RELEASE);
		result.mouseButton = mouseButton;
		return result;
	}
	
	public static TInputContext ofMouseMove(double mouseX, double mouseY)
	{
		final var result = new TInputContext(InputType.MOUSE_MOVE);
		result.mousePos = new Point2D.Double(mouseX, mouseY);
		return result;
	}
	
	/**
	 * "Of mouse drag"
	 * @param mouseButton The mouse button that was clicked when the dragging first started.
	 */
	public static TInputContext ofMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int mouseButton)
	{
		final var result = new TInputContext(InputType.MOUSE_DRAG);
		result.mousePos = new Point2D.Double(mouseX, mouseY);
		result.mouseDelta = new Point2D.Double(deltaX, deltaY);
		result.mouseButton = mouseButton;
		return result;
	}
	
	/**
	 * "Of mouse drag end"<br/>
	 * Only invoked on elements that were previously being dragged and no longer are.
	 * @param mouseButton The mouse button that was clicked when the dragging first started.
	 */
	public static TInputContext ofMouseDragEnd(int mouseButton)
	{
		final var result = new TInputContext(InputType.MOUSE_DRAG_END);
		result.mouseButton = mouseButton;
		return result;
	}
	
	public static TInputContext ofMouseScroll(double mouseX, double mouseY, double vAmount) { return ofMouseScroll(mouseX, mouseY, 0, vAmount); }
	public static TInputContext ofMouseScroll(double mouseX, double mouseY, double hAmount, double vAmount)
	{
		final var result = new TInputContext(InputType.MOUSE_SCROLL);
		result.mousePos = new Point2D.Double(mouseX, mouseY);
		result.scrollAmount = new Point2D.Double(hAmount, vAmount);
		return result;
	}
	
	/*@Deprecated
	public static TInputContext ofFileDrag(Collection<Path> draggedFiles)
	{
		final var result = new TInputContext(InputType.FILE_DRAG);
		result.draggedFiles = Objects.requireNonNull(draggedFiles);
		return result;
	}*/
	// ==================================================
	/**
	 * Used to indicate what {@link InputType} a given getter
	 * method in {@link TInputContext} is supposed to be used for.
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.CLASS)
	public static @interface UsedFor { InputType[] value(); }
	// --------------------------------------------------
	/**
	 * Represents an input's type. This tells you whether or not
	 * an input is keyboard or mouse related, and what kind of input it is.
	 */
	public static enum InputType
	{
		KEY_PRESS,
		KEY_RELEASE,
		
		CHAR_TYPE,
		
		MOUSE_PRESS,
		MOUSE_RELEASE,
		
		MOUSE_MOVE,
		/** @apiNote You must handle {@link #MOUSE_PRESS} to get {@link #MOUSE_DRAG}. */
		MOUSE_DRAG,
		MOUSE_DRAG_END,
		MOUSE_SCROLL;
		
		//individual GUI elements shouldn't handle this, as it could
		//result in multiple of them performing IO operations simultaneously
		//@Deprecated FILE_DRAG
		
		public boolean isKeyboardRelated() { return this == KEY_PRESS || this == KEY_RELEASE || this == CHAR_TYPE; }
		public boolean isMouseRelated()
		{
			return this == MOUSE_PRESS || this == MOUSE_RELEASE || this == MOUSE_MOVE ||
					this == MOUSE_DRAG || this == MOUSE_SCROLL;
		}
	}
	// --------------------------------------------------
	/**
	 * This refers to the current "phase" at which an input is being handled.<br/>
	 * Each input phase has its own unique behaviors.
	 */
	public static enum InputDiscoveryPhase
	{
		/**
		 * During this input phase, the input is forwarded to all elements,
		 * sequentially, starting from the root parent.
		 * <p>
		 * This phase is solely for the purpose of elements being able to
		 * know an input took place. Any elements handling the input on
		 * this phase will have no effect on the input propagation, and
		 * will not be able to prevent the {@link InputDiscoveryPhase}s that follow.
		 */
		BROADCAST,
		
		/**
		 * This phase is similar to {@link #BROADCAST}, except elements are
		 * able to stop the input propagation by handling the input. Doing
		 * so will prevent {@link #MAIN} from being handled.
		 * @see #BROADCAST
		 * @see #MAIN
		 */
		PREEMPT,
		
		/**
		 * During this input phase, the input is forwarded to the
		 * currently focused or hovered element (depending on the input type),
		 * after which the input "bubbles" towards the root parent element,
		 * until the input gets handled by the target element or one of its parents.
		 */
		MAIN
	}
	// --------------------------------------------------
	/**
	 * Represents an input keyboard key. This tells you its
	 * key-code, scan-code, and modifiers.
	 */
	public final static class InputKeyboardKey
	{
		public final int keyCode, scanCode, modifiers;
		public InputKeyboardKey(int keyCode, int scanCode) { this(keyCode, scanCode, 0); }
		public InputKeyboardKey(int keyCode, int scanCode, int modifiers)
		{
			this.keyCode = keyCode;
			this.scanCode = scanCode;
			this.modifiers = modifiers;
		}
	    public final @Override boolean equals(Object other)
		{
			if (this == other) return true;
	        if (other == null || getClass() != other.getClass()) return false;
	        InputKeyboardKey key = (InputKeyboardKey) other;
	        if (keyCode != key.keyCode) return false;
	        else if (scanCode != key.scanCode) return false;
	        return modifiers == key.modifiers;
		}
	    public final @Override int hashCode()
	    {
	    	int result = keyCode;
	    	result = 31 * result + scanCode;
	    	result = 31 * result + modifiers;
	    	return result;
	    }
	}
	// ==================================================
}
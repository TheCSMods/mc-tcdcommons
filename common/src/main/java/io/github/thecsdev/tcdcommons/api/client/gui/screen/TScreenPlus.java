package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Direction2D;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * Same as {@link TScreen}, but with some extra features
 * such as arrow-key navigation. May cost extra performance.
 */
public abstract class TScreenPlus extends TScreen
{
	// ==================================================
	public TScreenPlus(Component title) { super(title); }
	// --------------------------------------------------
	private static final Predicate<TElement> DEFAULT_FCSE_PREDICATE = (e -> e.isFocusable() && e.isEnabledAndVisible());
	// ==================================================
	public @Virtual @Override boolean input(final TInputContext inputContext)
	{
		//always respect super when dealing with screens
		if(super.input(inputContext))
			return true;
		
		//handle input by type
		switch(inputContext.getInputType())
		{
			//key press for arrow key navigation
			case KEY_PRESS:
				//obtain the key-code that was pressed
				final int keyCode = inputContext.getKeyboardKey().keyCode;
				//obtain the direction based on the pressed key-code
				Direction2D direction = null;
				switch(keyCode)
				{
					case 265: direction = Direction2D.UP; break;
					case 264: direction = Direction2D.DOWN; break;
					case 263: direction = Direction2D.LEFT; break;
					case 262: direction = Direction2D.RIGHT; break;
					default: return false; //or return if an arrow key wasn't pressed
				}
				//find the "closest" element to the currently focused element
				final TElement closest = findClosestSideElement(this.__focused, direction);
				if(closest == null) return false; //return if not found
				//focus on the found "closest" element
				setFocusedElement(closest, false);
				__postTabNavigation();
				//return true to indicate the event was handled
				return true;
			//not handling any other input types
			default: break;
		}
		
		//return false by default
		return false;
	}
	// ==================================================
	/**
	 * Same as {@link #findClosestSideElement(TElement, Direction2D, Predicate)},
	 * except this method uses the {@link #DEFAULT_FCSE_PREDICATE} that requires the
	 * returned {@link TElement} to be enabled and focusable.
	 * 
	 * @param target See below...
	 * @param direction See below...
	 * @see #findClosestSideElement(TElement, Direction2D, Predicate)
	 */
	public @Nullable TElement findClosestSideElement
	(final TElement target, final Direction2D direction)
	{
		return findClosestSideElement(target, direction, DEFAULT_FCSE_PREDICATE);
	}
	
	/**
	 * Finds and returns the {@link TElement} nearest to the specified target
	 * {@link TElement}, in the given {@link Direction2D}.<br/>
	 * The method will iterate through all children elements on the {@link TScreen}
	 * and locate the element closest to the target in the specified direction.
	 * 
	 * <p>For example, if the direction is {@link Direction2D#RIGHT}, the method
	 * will find the element nearest to the target that is to the right of it.</p>
	 *
	 * @param target The target {@link TElement} for which the closest side element needs to be found.
	 * @param direction The {@link Direction2D} in which to search for the nearest element.
	 * @param predicate The {@link Predicate} the returned {@link TElement} must match.
	 * @return The nearest {@link TElement} to the target in the specified direction,
	 * or {@code null} if no such element is found.
	 */
	public @Nullable TElement findClosestSideElement
	(final TElement target, final Direction2D direction, final Predicate<TElement> predicate)
	{
		//null checks
		if(target == null || direction == null || target.getParent() == null)
			return null;
		
		//obtain target's coordinates
		final int x = target.getX() + (target.getWidth() / 2);
		final int y = target.getY() + (target.getHeight() / 2);
		
		//define the closest element
		final var closest = new AtomicReference<TElement>(null);
		final AtomicInteger dX = new AtomicInteger(0), dY = new AtomicInteger(0);
		
		//iterate elements
		final Predicate<TElement> finalPredicate = child ->
		{
			//skip these:
			if(child == target || !predicate.test(child))
				return false;
			final int cX = child.getX() + (child.getWidth() / 2);
			final int cY = child.getY() + (child.getHeight() / 2);
			
			//direction check
			switch(direction)
			{
				case UP: if(cY > y - 1) return false; break;
				case DOWN: if(cY < y + 1) return false; break;
				case LEFT: if(cX > x - 1) return false; break;
				case RIGHT: if(cX < x + 1) return false; break;
				default: return false;
			}
			
			//if no closest is found yet, assign the current one
			if(closest.get() == null)
			{
				closest.set(child);
				dX.set(Math.abs(cX - x));
				dY.set(Math.abs(cY - y));
				return false;
			}
			
			//distance check
			else if(Math.abs(cX - x) < dX.get() || Math.abs(cY - y) < dY.get())
			{
				closest.set(child);
				dX.set(Math.abs(cX - x));
				dY.set(Math.abs(cY - y));
				return false;
			}
			
			//continue the loop
			return false;
		};
		
		//find child
		target.getParent().findChild(finalPredicate, true);
		if(closest.get() == null) findChild(finalPredicate, true);
		
		//return
		return closest.get();
	}
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.Direction2D;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import io.github.thecsdev.tcdcommons.api.util.SubjectToChange;
import net.minecraft.network.chat.Component;

/**
 * Same as {@link TScreen}, but with some extra features
 * such as arrow navigation. May cost more performance.
 */
public abstract class TScreenPlus extends TScreen
{
	// ==================================================
	protected TScreenPlus(Component title) { super(title); }
	// ==================================================
	/**
	 * Note: Will not return click-thru elements, disabled elements,
	 * invisible elements, and non-focusable elements.
	 * @param target The central UI {@link TElement}.
	 * @param direction The direction in which to look for the closest {@link TElement} in.
	 */
	//TODO - Fix arrow navigation bugs
	@SubjectToChange("What if the user wants to find an element regardless of it's properties?")
	public @Nullable TElement findClosestSideElement(final TElement target, final Direction2D direction)
	{
		//null checks
		if(target == null || target.getTParent() == null || direction == null)
			return null;
		
		//obtain target's coordinates
		final int x = target.getTpeX() + (target.getTpeWidth() / 2);
		final int y = target.getTpeY() + (target.getTpeHeight() / 2);
		
		//define the closest element
		final var closest = new AtomicReference<TElement>(null);
		final AtomicInteger dX = new AtomicInteger(0), dY = new AtomicInteger(0);
		
		//iterate elements
		Function<TElement, Boolean> func = child ->
		{
			//skip these:
			if(child == target || child.isClickThrough() ||
					!child.isEnabledAndVisible() ||
					!child.canChangeFocus(FocusOrigin.TAB, true))
				return false;
			final int cX = child.getTpeX() + (child.getTpeWidth() / 2);
			final int cY = child.getTpeY() + (child.getTpeHeight() / 2);
			
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
		
		//TODO - possibly optimize?
		target.getTParent().forEachChild(func, true);
		if(closest.get() == null) forEachChild(func, true);
		
		//return
		return closest.get();
	}
	// ==================================================
	public @Override boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//handle super
		if(super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		
		// ---------- handle arrow key navigation
		//265 - up arrow
		//264 - down arrow
		//263 - left arrow
		//262 - right arrow
		TElement focused = getFocusedTChild();
		if(focused == null) return false;
		
		Direction2D direction;
		switch(keyCode)
		{
			case 265: direction = Direction2D.UP; break;
			case 264: direction = Direction2D.DOWN; break;
			case 263: direction = Direction2D.LEFT; break;
			case 262: direction = Direction2D.RIGHT; break;
			default: return false;
		}
		
		TElement closest = findClosestSideElement(focused, direction);
		if(closest == null) return false;
		
		setFocusedTChild(closest);
		__triggerScrollTo(closest);
		return true;
	}
	// ==================================================
}

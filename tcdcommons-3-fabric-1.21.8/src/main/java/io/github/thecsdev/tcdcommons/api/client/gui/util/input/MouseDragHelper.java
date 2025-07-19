package io.github.thecsdev.tcdcommons.api.client.gui.util.input;

import java.awt.geom.Point2D;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputDiscoveryPhase;

/**
 * A helper {@link Class} that helps {@link TElement}s implement
 * their own mouse-drag behaviors that require tracking mouse movement.
 * <p>
 * The main reason for this {@link Class} is that mouse movement is tracked
 * as a {@link Double}, whereas the Minecraft GUI coordinate system uses {@link Integer}s.
 * 
 * @see #onMouseDrag(double, double)
 * @see #clear()
 * @see #apply(int, int)
 */
public abstract class MouseDragHelper extends Object
{
	// ==================================================
	protected double mouseDragX, mouseDragY;
	// ==================================================
	/**
	 * Call this whenever the mouse drag event takes place.
	 * @apiNote Do this during the {@link InputDiscoveryPhase#MAIN} phase.
	 */
	public final boolean onMouseDrag(Point2D mouseDelta) { return onMouseDrag(mouseDelta.getX(), mouseDelta.getY()); }
	
	/**
	 * Call this whenever the mouse drag event takes place.
	 * @apiNote Do this during the {@link InputDiscoveryPhase#MAIN} phase.
	 */
	public final boolean onMouseDrag(double deltaX, double deltaY)
	{
		//add deltas
		this.mouseDragX += deltaX;
		this.mouseDragY += deltaY;
		
		//get the FLOOR value of the totals
		int sdX = (int)this.mouseDragX;
		int sdY = (int)this.mouseDragY;
		
		//this part is important.
		//continuing past this point would reset the values
		if(sdX == 0 && sdY == 0) return true;
		
		//subtract the FLOOR totals from the totals
		this.mouseDragX -= sdX;
		this.mouseDragY -= sdY;
		
		//apply and return
		apply(sdX, sdY);
		return true;
	}
	
	/**
	 * Invoke this when the mouse stops dragging.
	 */
	public final void clear() { this.mouseDragX = 0; this.mouseDragY = 0; }
	// ==================================================
	/**
	 * Invoked automatically by {@link #onMouseDrag(double, double)}
	 * when there's sufficient {@link #mouseDragX} or {@link #mouseDragY}.
	 */
	protected abstract void apply(int deltaX, int deltaY);
	// ==================================================
	/**
	 * Creates a {@link MouseDragHelper} instance for a given {@link TElement}.
	 * @param element The {@link TElement} which the {@link MouseDragHelper} is for.
	 */
	public static final MouseDragHelper forTElement(TElement element)
	{
		return new MouseDragHelper()
		{
			protected final @Override void apply(int deltaX, int deltaY) { element.move(deltaX, deltaY); }
		};
	}
	
	/**
	 * Checks if the given {@link TElement} is outside of its {@link TParentElement}
	 * bounds, and if it it, it will get snapped back in.
	 * @param element The target {@link TElement}.
	 * @return True if the given {@link TElement} was outside of its parent's bounds.
	 * @apiNote Does not support {@link TPanelElement}s being the {@link TElement}'s parent.
	 */
	public static boolean snapToParentBounds(TElement element) throws NullPointerException
	{
		//obtain and null-check the parent
		final var parent = element.getParent(); //throws NullPointerException
		if(parent == null) return false;
		
		//obtain parent bounds (p.t. = parent top | p.b. = parent bottom)
		final int ptX = parent.getX(), ptY = parent.getY();
		final int pbX = parent.getEndX(), pbY = parent.getEndY();
		//obtain child bounds (c.t. = child top | c.b. = child bottom)
		final int ctX = element.getX(), ctY = element.getY();
		final int cbX = element.getEndX(), cbY = element.getEndY();
		
		//perform calculations and offsets (0. = offset)
		int oX = 0, oY = 0;
		//ChatGPT, I need you to figure oX and oY out please:
		{
			// Calculate the offset for X-axis
			if (ctX < ptX) oX = ptX - ctX; // Move right if child is to the left of parent
			else if (cbX > pbX) oX = pbX - cbX; // Move left if child is to the right of parent
			
			// Calculate the offset for Y-axis
			if (ctY < ptY) oY = ptY - ctY; // Move down if child is above parent
			else if (cbY > pbY) oY = pbY - cbY; // Move up if child is below parent
		}
		
		//move and return
		if(oX != 0 || oY != 0)
		{
			element.move(oX, oY);
			return true;
		}
		else return false;
	}
	// ==================================================
}
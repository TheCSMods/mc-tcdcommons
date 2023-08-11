package io.github.thecsdev.tcdcommons.api.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.collections.IdealList;
import io.github.thecsdev.tcdcommons.api.util.math.Tuple4;

public final class TElementList implements Iterable<TElement>
{
	// ==================================================
	/**
	 * The {@link TParentElement} that the
	 * {@link TElement} children belong to.
	 */
	private final TParentElement parent;
	//warning: children list type may change at any time
	private final @Internal IdealList<TElement> __children = new IdealList<>();
	
	/**
	 * Contains the {@link TElement} children that are all the way
	 * on the sides of this element in the following order:<br/>
	 * TOP, BOTTOM, LEFT, RIGHT
	 */
	private final Tuple4<TElement, TElement, TElement, TElement> topmostElements = new Tuple4<>();
	// ==================================================
	public TElementList(TParentElement parent) { this.parent = Objects.requireNonNull(parent); }
	// --------------------------------------------------
	/**
	 * Returns the {@link TParentElement} that owns this {@link TElementList} of children.
	 */
	public final TParentElement getParent() { return this.parent; }
	
	/**
	 * Returns the {@link #topmostElements} for this {@link #parent} element.
	 */
	public Tuple4<TElement, TElement, TElement, TElement> getTopmostElements() { return this.topmostElements; }
	
	/**
	 * Returns an {@link Iterator} for iterating the {@link TElementList}.
	 */
	public final @Override Iterator<TElement> iterator() { return this.__children.iterator(); }
	// ==================================================
	/**
	 * Returns true if a given {@link TElement} is able to be
	 * added as a child to this {@link #parent} element.
	 * @param futureChild The {@link TElement} in question.
	 */
	public final boolean canAdd(TElement futureChild)
	{
		//null check, and
		//check if the child is this element, and
		//make sure the child isn't already added
		if(futureChild == null || this.parent == futureChild || contains(futureChild))
			return false;
		
		//check if the child is actually a parent
		TParentElement pe = this.parent;
		while(pe != null)
		{
			if(pe == futureChild) return false;
			pe = pe.getParent();
		}
		
		//true if all checks pass
		return futureChild.canBeAddedTo(this.parent);
	}
	// --------------------------------------------------
	/**
	 * Adds a {@link TElement} child to the list of {@link #__children},
	 * and repositions the child based on the {@link #parent}'s location.
	 * @param child The child to add.
	 * @see #add(TElement, boolean)
	 */
	public final boolean add(TElement child) { return add(child, true); }
	public final boolean add(TElement child, boolean reposition) { return add(this.__children.size(), child, reposition); }
	public final boolean add(int index, TElement child, boolean reposition) throws IndexOutOfBoundsException
	{
		//null-s and existing members are not allowed
		if(!canAdd(child)) return false;
		
		//(1) add
		this.__children.add(index, child); //throws IndexOutOfBoundsException
		__updateTopmostChild(child);
		
		//(2) update parent
		final var oldParent = child.__parent; //must be done here
		child.__updateParent(this.parent);
		child.forEachChild(null, true); //update child children's __parent
		
		//(3) reposition
		if(reposition) child.move(this.parent.getX(), this.parent.getY());
		
		//(4) invoke the TElement events
		child.eParentChanged.invoker().invoke(child, oldParent, child.__parent);
		if(this.parent instanceof TElement)
		{
			var teParent = (TElement)this.parent;
			teParent.eChildAdded.invoker().invoke(teParent, child, reposition);
		}
		
		//(5) return
		return true;
	}
	
	/**
	 * Removes a {@link TElement} child from the list of {@link #__children},
	 * and repositions the child based on the {@link #parent}'s location.
	 * @param child The child to add.
	 * @see #remove(TElement, boolean)
	 */
	public final boolean remove(TElement child) { return remove(child, true); }
	public final boolean remove(TElement child, boolean reposition)
	{
		//(1) remove
		final var result = this.__children.remove(child);
		//(do NOT update child/parent relations for null and non-children elements)
		if(child == null || !result)
			return result;
		
		//(2) update topmost children
		final boolean topmostsWereCleared = this.topmostElements.isCleared();
		if(!topmostsWereCleared) updateTopmostChildren();
		
		//(3) update parent
		final var oldParent = child.__parent; //must be done here
		if(child.__parent == this.parent) child.__updateParent(null);
		child.forEachChild(null, true); //update children's __parent
		
		//(4) reposition
		if(reposition) child.move(-this.parent.getX(), -this.parent.getY());
		
		//(5) invoke the TElement events
		if(!topmostsWereCleared)
		{
			child.eParentChanged.invoker().invoke(child, oldParent, child.__parent);
			if(this.parent instanceof TElement)
			{
				var teParent = (TElement)this.parent;
				teParent.eChildRemoved.invoker().invoke(teParent, child, reposition);
			}
		}
		
		//(6) return
		return result;
	}
	
	public final void clear() { this.clear(true); }
	public final void clear(boolean reposition)
	{
		//use #remove(TElement) so as to have the method
		//handle parent assignments and other things.
		//clear topmost elements to avoid #remove updating them
		this.topmostElements.clear();
		
		//"cheesy" workaround; create a new array list; no more concurrent modification
		final var iterator = new ArrayList<TElement>(this.__children).iterator();
		while(iterator.hasNext()) remove(iterator.next(), reposition);
	}
	// --------------------------------------------------
	public final boolean contains(TElement child) { return this.__children.contains(child); }
	public final int size() { return this.__children.size(); }
	// --------------------------------------------------
	public final @Nullable TElement getFirstChild() { return __getAt(0); }
	public final @Nullable TElement getLastChild() { return __getAt(size() - 1); }
	private final @Nullable TElement __getAt(int index)
	{
		// If the index is out of bounds, return null
		if(index >= size()) return null;
	    
	    // Otherwise, return the element at the specified index
	    return this.__children.get(index);
	}
	// ==================================================
	/**
	 * Iterates all of the {@link TElement} children in the list,
	 * and updates the {@link #topmostElements}.<br/>
	 * <br/>
	 * Automatically called when removing a child.
	 */
	public final void updateTopmostChildren()
	{
		//clear the topmost children
		this.topmostElements.clear();
		
		//iterate and update
		for(TElement child : this)
			__updateTopmostChild(child);
	}
	
	/**
	 * Checks if a given {@link TElement} child is a topmost child,
	 * and updates the {@link #topmostElements} if it is.
	 * @param childToCheck The child {@link TElement} in question.
	 * @return True if it is.
	 */
	private final boolean __updateTopmostChild(TElement childToCheck)
	{
		//check if the element is a child
		if(!contains(childToCheck) && childToCheck != null)
			return false;
		boolean flag = false;
		
		// Store childToCheck properties in local variables
		final int cX = childToCheck.getX(), cY = childToCheck.getY();
		final int cW = childToCheck.getWidth(), cH = childToCheck.getHeight();
		
		// Determine top/bottom
		if(this.topmostElements.Item1 == null || cY < this.topmostElements.Item1.getY())
			flag = (this.topmostElements.Item1 = childToCheck) != null;
		if(this.topmostElements.Item2 == null || (cY + cH) > (this.topmostElements.Item2.getY() + this.topmostElements.Item2.getHeight()))
			flag = (this.topmostElements.Item2 = childToCheck) != null;
		
		// Determine left/right
		if(this.topmostElements.Item3 == null || cX < this.topmostElements.Item3.getX())
			flag = (this.topmostElements.Item3 = childToCheck) != null;
		if(this.topmostElements.Item4 == null || (cX + cW) > (this.topmostElements.Item4.getX() + this.topmostElements.Item4.getWidth()))
			flag = (this.topmostElements.Item4 = childToCheck) != null;
		
		//return
		return flag;
	}
	// ==================================================
}
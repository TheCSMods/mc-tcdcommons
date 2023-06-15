package io.github.thecsdev.tcdcommons.api.client.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.util.WrappedList;
import io.github.thecsdev.tcdcommons.api.util.math.Tuple4;

public class TElementList extends WrappedList<TElement>
{
	// ==================================================
	/**
	 * The {@link TParentElement} that the
	 * {@link TElement} children belong to.
	 */
	protected final TParentElement parent;
	
	/**
	 * Contains the {@link TElement} children that are all the way
	 * on the sides of this element in the following order:<br/>
	 * TOP, BOTTOM, LEFT, RIGHT
	 */
	protected final Tuple4<TElement, TElement, TElement, TElement> topmostElements = new Tuple4<>();
	// ==================================================
	public TElementList(TParentElement parent)
	{
		super(TElement.class);
		this.parent = Objects.requireNonNull(parent, "parent must not be null.");
	}
	
	/**
	 * Returns the {@link #parent} this children list belongs to.
	 */
	public TParentElement getParent() { return this.parent; }
	
	/**
	 * Returns the {@link #topmostElements} for this {@link #parent} element.
	 */
	public Tuple4<TElement, TElement, TElement, TElement> getTopmostElements() { return this.topmostElements; }
	// --------------------------------------------------
	/**
	 * Iterates all of the {@link TElement} children in the list,
	 * and updates the {@link #topmostElements}.<br/>
	 * <br/>
	 * Automatically called when removing a child.
	 */
	public void updateTopmostChildren()
	{
		//clear the topmost children
		this.topmostElements.clear();
		
		//iterate and update
		for(TElement child : this.list)
			updateTopmostChild(child);
	}
	
	/**
	 * Checks if a given {@link TElement} child is a topmost child,
	 * and updates the {@link #topmostElements} if it is.
	 * @param childToCheck The child {@link TElement} in question.
	 * @return True if it is.
	 */
	public boolean updateTopmostChild(TElement childToCheck)
	{
		//check if the element is a child
		if(!list.contains(childToCheck) && childToCheck != null)
			return false;
		boolean flag = false;
		
		//determine top/bottom
		if(this.topmostElements.Item1 == null || childToCheck.getTpeY() < this.topmostElements.Item1.getTpeY())
			flag = (this.topmostElements.Item1 = childToCheck) != null;
		if(this.topmostElements.Item2 == null || (childToCheck.getTpeY() + childToCheck.getTpeHeight()) > (this.topmostElements.Item2.getTpeY() + this.topmostElements.Item2.getTpeHeight()))
			flag = (this.topmostElements.Item2 = childToCheck) != null;
		
		//determine left/right
		if(this.topmostElements.Item3 == null || childToCheck.getTpeX() < this.topmostElements.Item3.getTpeX())
			flag = (this.topmostElements.Item3 = childToCheck) != null;
		if(this.topmostElements.Item4 == null || (childToCheck.getTpeX() + childToCheck.getTpeWidth()) > (this.topmostElements.Item4.getTpeX() + this.topmostElements.Item4.getTpeWidth()))
			flag = (this.topmostElements.Item4 = childToCheck) != null;
		
		//return
		return flag;
	}
	// ==================================================
	public @Override boolean filter(TElement element)
	{
		//null check and
		//check if the child is this element
		if(element == null || this.parent == element)
			return false;
		
		//check if the child is actually a parent
		TParentElement pe = this.parent;
		while(pe != null)
		{
			if(pe == element) return false;
			pe = pe.getTParent();
		}
		
		//true if all checks pass
		return element.canBeAddedTo(this.parent);
	}
	// --------------------------------------------------
	/**
	 * Adds a {@link TElement} child to the list of children.<br/>
	 * Calls {@link #add(TElement, boolean)} and repositions the child.
	 * @param child The {@link TElement} child to add.
	 */
	public @Override boolean add(TElement child) { return add(child, true); }
	
	/**
	 * Adds a {@link TElement} child to the list of children, while
	 * also letting you choose if you wish to reposition the child.
	 * @param child The {@link TElement} child to add.
	 * @param reposition Whether or not to reposition the child based on the parent's position.
	 */
	public boolean add(TElement child, boolean reposition) { return add(this.list.size(), child, reposition); }
	
	/**
	 * Adds a {@link TElement} child to the list of children, while
	 * also letting you choose if you wish to reposition the child,
	 * and the index of where the child will be added.
	 * @param index The index at which the child {@link TElement} is to be inserted.
	 * @param child The {@link TElement} child to add.
	 * @param reposition Whether or not to reposition the child based on the parent's position.
	 * @throws IndexOutOfBoundsException See {@link List#add(int, Object)}.
	 */
	public boolean add(int index, TElement child, boolean reposition)
	{
		if(super.add(index, child))
		{
			//(1) assign parent of the child
			child.parent = (parent instanceof TElement) ? (TElement)parent : null;
			
			if(parent instanceof TScreen) //after assigning parent
				child.screen = (TScreen)parent;
			else child.updateScreen();
			
			//(2) reposition if needed
			if(reposition && child.parent != null)
				child.move(child.parent.getTpeX(), child.parent.getTpeY());
			
			//(3) update topmost child and call parent handler
			updateTopmostChild(child);
			
			//(4) don't forget about the bounding box
			child.updateRenderingBoundingBox();
			
			//(5) let the child know
			child.onParentChanged();
			
			//(6) invoke the TElement event
			if(getParent() instanceof TElement)
			{
				var teParent = (TElement)getParent();
				teParent.eChildAdded.invoker().invoke(teParent, child, reposition);
				//teParent.getEvents().CHILD_AR.p_invoke(handler -> handler.accept(child, true, reposition));
			}
			
			//return
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a {@link TElement} child from the list of children.<br/>
	 * Calls {@link #remove(TElement, boolean)} and repositions the child.
	 * @param child The {@link TElement} child to remove.
	 */
	public final @Override boolean remove(TElement child) { return remove(child, true); }
	
	/**
	 * Removes a {@link TElement} child from the list of children, while
	 * also letting you choose if you wish to reposition the child.
	 * @param child The {@link TElement} child to remove.
	 * @param reposition Whether or not to reposition the removed child based on the parent's position.
	 */
	public boolean remove(TElement child, boolean reposition)
	{
		TElement parent = child.parent;
		if(super.remove(child))
		{
			boolean c = this.topmostElements.isCleared();
			
			//(1) update topmost children
			if(!c) updateTopmostChildren(); //TODO - Optimize topmost update
			
			//(2) then reposition if needed
			if(reposition && parent != null)
				child.move(child.parent.getTpeX(), child.parent.getTpeY());
			
			//(3) and then clear parent assignment
			child.parent = null;
			child.screen = null;
			
			//(4) let the child know
			child.onParentChanged();
			
			//(5) invoke the TElement event
			if(!c && getParent() instanceof TElement)
			{
				var teParent = (TElement)getParent();
				teParent.eChildRemoved.invoker().invoke(teParent, child, reposition);
				//teParent.getEvents().CHILD_AR.p_invoke(handler -> handler.accept(child, false, reposition));
			}
			
			//return
			return true;
		}
		return false;
	}
	
	/**
	 * Removes all {@link TElement} children that match a given criteria.<br/>
	 * <br/>
	 * <b>Note:</b> This method does not handle nested children.
	 * @param predicate The predicate to test against each {@link TElement} child.
	 */
	public boolean removeIf(final Predicate<TElement> predicate) { return removeIf(predicate, true); }
	
	/**
	 * Removes all {@link TElement} children that
	 * match a given criteria.
	 * @param predicate The predicate to test against each {@link TElement} child.
	 * @param reposition Whether or not to reposition the removed children based on the parent's position.
	 */
	public boolean removeIf(final Predicate<TElement> predicate, boolean reposition)
	{
		final ArrayList<TElement> toRemove = Lists.newArrayList();
		forEach(child -> { if(predicate.test(child)) toRemove.add(child); });
		for(TElement child : toRemove) remove(child, reposition);
		return toRemove.size() > 0;
	}
	
	/**
	 * Removes all {@link TElement} children from the list of children,
	 * without repositioning any of them (for performance reasons).
	 */
	@Override
	public void clear() { this.clear(false); }
	
	/**
	 * Removes all {@link TElement} children from the list of children,
	 * while letting you choose if you want to reposition them.
	 * @param reposition Will the children get repositioned? See {@link #remove(TElement, boolean)}.
	 */
	public void clear(boolean reposition)
	{
		//use #remove(TElement) so as to have the method
		//handle parent assignments and other things.
		//clear topmost elements to avoid #remove updating them
		this.topmostElements.clear();
		
		//remove elements one by one
		for(TElement child : Lists.newArrayList(list))
			remove(child, true);
	}
	// ==================================================
}
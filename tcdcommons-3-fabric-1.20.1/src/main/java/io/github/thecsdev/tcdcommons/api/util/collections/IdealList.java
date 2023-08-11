package io.github.thecsdev.tcdcommons.api.util.collections;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * Tired of built-in list types that never have the one thing you need?<br/>
 * Well fear not, for this one has everything that <b>I</b> will ever need!<p>
 * Features:
 * <ul>
 *     <li>Resilient to {@link ConcurrentModificationException}</li>
 *     <li>{@link Iterator} capable of handling complex concurrent changes</li>
 * </ul>
 * @author TheCSDev
 */
public @Virtual class IdealList<E> extends AbstractList<E> implements Iterable<E>
{
	// ==================================================
	/**
	 * Oh noes, I am cheating by using an existing {@link Collection} implementation
	 * instead of storing internal entries (aka {@link Node}s) in an array on my own...
	 */
	private final @Internal ArrayList<Node> __nodes = new ArrayList<>();
	// ==================================================
	public @Virtual @Override int size() { return nodeCount(); }
	public final @Override boolean isEmpty() { return size() < 1; }
	public @Virtual @Override void clear() { clearNodes(); }
	public @Virtual @Override int indexOf(Object entry) { return findNode(n -> n.get() == entry).getKey(); }
	public @Virtual @Override boolean contains(Object entry) { return indexOf(entry) >= 0; }
	// --------------------------------------------------
	public @Virtual @Override E get(int index) throws IndexOutOfBoundsException { return getNode(index).get(); }
	public @Virtual @Override E set(int index, E element) throws IndexOutOfBoundsException
	{
		final var node = getNode(index);
		final var old = node.get();
		node.set(element);
		return old;
	}
	// --------------------------------------------------
	public final @Override boolean add(E element) { return addB(size(), element); }
	public final @Override void add(int index, E element) throws IndexOutOfBoundsException, UnsupportedOperationException
	{
		if(!addB(index, element))
			throw new UnsupportedOperationException();
	}
	public @Virtual boolean addB(int index, E element) throws IndexOutOfBoundsException
	{
		addNode(index, new Node(element));
		return true;
	}
	//
	public final @Override boolean remove(Object element)
	{
		final int index = indexOf(element);
		if(index < 0) return false;
		return removeB(index);
	}
	public final @Override E remove(int index) throws IndexOutOfBoundsException, UnsupportedOperationException
	{
		final var prev = getNode(index).get(); //throws IndexOutOfBoundsException
		if(!removeB(index))
			throw new UnsupportedOperationException();
		return prev;
	}
	public @Virtual boolean removeB(int index) throws IndexOutOfBoundsException { removeNode(index); return true; }
	// --------------------------------------------------
	/*public @Virtual @Override E[] toArray() - AbstractList already has this
	{
		@SuppressWarnings("unchecked")
		final E[] result = (E[]) new Object[size()];
		int index = -1;
		for(final var item : this.__nodes)
			result[index++] = item.get();
		return result;
	}*/
	// --------------------------------------------------
	public final @Override Iterator<E> iterator() { return listIterator(); }
	public final @Override ListIterator<E> listIterator() { return listIterator(0); }
	public @Virtual @Override ListIterator<E> listIterator(int index) throws IndexOutOfBoundsException
	{
		//node pointer correction (nothing should be wrong, but just in case...)
		forEachNode(null);
		//create and return the iterator
		return new IdealListIterator(index);
	}
	// ==================================================
	protected final int nodeCount() { return this.__nodes.size(); }
	protected final int indexOfNode(Node node) { return this.__nodes.indexOf(node); }
	protected final void clearNodes() { forEachNode(n -> n.setRemoved()); __nodes.clear(); this.__nodes.trimToSize(); }
	protected final Node getNode(int index) throws IndexOutOfBoundsException { return this.__nodes.get(index); }
	//
	/**
	 * Retrieves a {@link Node} at a given index, returning null if the index is out of bounds.
	 * @param index The {@link Node} index. 
	 */
	protected final @Nullable Node getNodeOrDefault(int index)
	{
		if(this.__nodes.isEmpty())
			return null;
		else if (index >= 0 && index < this.__nodes.size())
			return this.__nodes.get(index);
		return null;
	}
	//
	/**
	 * Inserts a {@link Node} at a given index.
	 * @param index The index at which to insert the {@link Node}
	 * @param node The {@link Node} to insert.
	 * @apiNote "Offensive programming" ahead. Will throw an exception at any chance it gets.
	 */
	protected final void addNode(int index, Node node) throws NullPointerException, IndexOutOfBoundsException
	{
		//collect info
		Objects.requireNonNull(node); //throws NullPointerException
		final Node prev = getNodeOrDefault(index - 1);
		final Node next = getNodeOrDefault(index); //at index
		
		//add
		this.__nodes.add(index, node); //throws IndexOutOfBoundsException
		
		//fix connections
		node.isRemoved = false;
		node.previous = prev;
		node.next = next;
		if(prev != null) prev.next = node;
		if(next != null) next.previous = node;
	}
	
	/**
	 * Removes a {@link Node} at a given index.
	 * @param index The {@link Node} index.
	 * @apiNote "Offensive programming" ahead. Will throw an exception at any chance it gets.
	 */
	protected final void removeNode(int index) throws IndexOutOfBoundsException
	{
		//collect info
		final Node node = this.__nodes.get(index); //throws IndexOutOfBoundsException
		final Node prev = getNodeOrDefault(index - 1);
		final Node next = getNodeOrDefault(index + 1); //at index + 1
		
		//remove the node
		this.__nodes.remove(index); //throws IndexOutOfBoundsException
		
		//fix node connections
		node.setRemoved();
		if(prev != null) prev.next = next;
		if(next != null) next.previous = prev;
	}
	// --------------------------------------------------
	/**
	 * Iterates over all {@link Node}s in the {@link #__nodes} list,
	 * and performs an action for each of them.
	 * @param action The action to perform.
	 */
	protected final void forEachNode(Consumer<Node> action)
	{
		findNode(n ->
		{
			if(action != null) action.accept(n);
			return false;
		});
	}
	
	/**
	 * Finds a {@link Node} in the {@link #__nodes} list, that matches a given predicate.
	 * @param action The predicate. Return true from the predicate to return the {@link Node}.
	 * @return A {@link Map.Entry} containing the {@link Node} and its index number.
	 * The returned index number will be -1 if a {@link Node} was not found.
	 */
	protected final Map.Entry<Integer, Node> findNode(Function<Node, Boolean> action)
	{
		//do nothing if empty
		if(this.__nodes.isEmpty())
			return new AbstractMap.SimpleEntry<Integer, Node>(-1, null);
		
		//iterate over all nodes
		getFirstNode().previous = null; //pointer correction
		Node lastNode = null;
		int index = -1;
		for(final var node : this.__nodes)
		{
			index++;
			//pointer correction
			if(lastNode != null) lastNode.next = node;
			//execute action
			final boolean accepted = action != null && action.apply(node);
			//assign last node
			lastNode = node;
			//return if needed
			if(accepted)
				return new AbstractMap.SimpleEntry<Integer, Node>(index, node);
		}
		lastNode.next = null; //pointer correction
		
		//return null if nothing was returned
		return new AbstractMap.SimpleEntry<Integer, Node>(-1, null);
	}
	
	/**
	 * Returns the first {@link Node} in the {@link #__nodes} list.
	 */
	protected final @Nullable Node getFirstNode() { return getNodeOrDefault(0); }
	
	/**
	 * Returns the last {@link Node} in the {@link #__nodes} list.
	 */
	protected final @Nullable Node getLastNode() { return getNodeOrDefault(this.__nodes.size() - 1); }
	// ==================================================
	/**
	 * An {@link IdealList} node that works similarly to how linked-list nodes work.<br/>
	 * Tracks the next and previous nodes for a given {@link IdealList} entry.
	 */
	protected final class Node extends Object
	{
		// ---------------------------------------------
		protected volatile @Nullable Node previous = null;
		protected volatile @Nullable Node next = null;
		protected volatile @Nullable E entry = null;
		//
		private volatile boolean isRemoved = false;
		// ---------------------------------------------
		public Node() {}
		public Node(E element) { this.entry = element; }
		// ---------------------------------------------
		public final @Nullable E get() { return this.entry; }
		public final void set(@Nullable E element) { this.entry = element; }
		//
		public final boolean isRemoved() { return this.isRemoved; }
		protected final void setRemoved() { this.isRemoved = true; }
		// ---------------------------------------------
		public final @Nullable Node getPrevious() { return this.previous; }
		public final @Nullable Node getNext() { return this.next; }
		// ---------------------------------------------
		public final @Nullable Node findPrevious()
		{
			//null check
			if(this.previous == null) return null;
			
			//go back by 1
			Node prev = this.previous;
			
			//if the previous node is removed,
			//navigate to the connected non-removed node
			while(prev.isRemoved())
			{
				Node i = prev.previous;
				if(i == null) break;
				prev = i;
			}
			//if at this point, the previous node is still removed...
			if(prev.isRemoved())
			{
				//if this node isn't removed, then the previous nodes are just removed from the list;
				//in that case, there's no "previous", so return null
				if(!this.isRemoved()) return null;
				
				//but if this node is removed too, then we are in a tough spot;
				//in this case, we are completely separated from the main list's chain;
				//to fix this, we need to try to find a way to navigate back to the main list's chain;
				//the plan is to go this time go in the opposite direction instead
				prev = this;
				while(prev.isRemoved())
				{
					Node i = prev.next;
					if(i == null) break;
					prev = i;
				}
				
				//moment of truth; did we recover?:
				if(prev.isRemoved()) return null; //we didn't; we're stuck in a removed chain
				else //we did; we found a way back to the main chain
				{
					//now, because we have successfully returned to the main chain,
					//we have reached a node we already iterated on before.
					//resolve this by going in the normal direction by 1 node
					return prev.findPrevious();
				}
			}
			
			//else if previous isn't removed, we can return it safely
			else return prev;
		}
		public final @Nullable Node findNext()
		{
			//null check
			if(this.next == null) return null;
			
			//go forward by 1
			Node next = this.next;
			
			//if the next node is removed,
			//navigate to the connected non-removed node
			while(next.isRemoved())
			{
				Node i = next.next;
				if(i == null) break;
				next = i;
			}
			//if at this point, the next node is still removed...
			if(next.isRemoved())
			{
				//if this node isn't removed, then the following ("next") nodes are just removed from the list;
				//in that case, there's no "next", so return null
				if(!this.isRemoved()) return null;
				
				//but if this node is removed too, then we are in a tough spot;
				//in this case, we are completely separated from the main list's chain;
				//to fix this, we need to try to find a way to navigate back to the main list's chain;
				//the plan is to go this time go in the opposite direction instead
				next = this;
				while(next.isRemoved())
				{
					Node i = next.previous;
					if(i == null) break;
					next = i;
				}
				//moment of truth; did we recover?:
				if(next.isRemoved()) return null; //we didn't; we're stuck in a removed chain
				else //we did; we found a way back to the main chain
				{
					//now, because we have successfully returned to the main chain,
					//we have reached a node we already iterated on before.
					//resolve this by going in the normal direction by 1 node
					return next.findNext();
				}
			}
			
			//else if previous isn't removed, we can return it safely
			else return next;
		}
		// ---------------------------------------------
	}
	// --------------------------------------------------
	/**
	 * A {@link ListIterator} implementation for {@link IdealList}.
	 */
	protected final class IdealListIterator implements ListIterator<E>
	{
		// ---------------------------------------------
		private volatile Node cursor;
		// ---------------------------------------------
		public IdealListIterator() { this(0); }
		public IdealListIterator(int startingNodeIndex) throws IndexOutOfBoundsException
		{
			//must start with a dummy node, so previous() and next() can work properly
			this.cursor = new Node();
			
			//handle empty list
			if(IdealList.this.nodeCount() < 1)
			{} //cursor will be an empty node with no forwards or backwards
			
			//handle starting at size()
			else if(startingNodeIndex == IdealList.this.nodeCount())
				this.cursor.previous = IdealList.this.getLastNode();
			
			//handle starting at any other position
			else
			{
				//throws IndexOutOfBoundsException \/
				final Node startingNode = IdealList.this.getNode(startingNodeIndex);
				this.cursor.next = startingNode;
				if(startingNode != null)
					this.cursor.previous = startingNode.previous;
			}
		}
		public final int findCursorIndex() { return IdealList.this.indexOfNode(this.cursor); }
		// ---------------------------------------------
		public @Override void set(E e) { this.cursor.set(e); }
		public @Override void add(E e)
		{
			final int index = this.findCursorIndex();
			final Node node = new Node(e);
			IdealList.this.addNode(index, node);
			this.cursor = node;
		}
		public @Override void remove() throws IndexOutOfBoundsException
		{
			IdealList.this.removeNode(this.findCursorIndex()); //throws IndexOutOfBoundsException
			if(!this.cursor.isRemoved())
				throw new IllegalStateException("The cursor node didn't get removed. This "
						+ "shouldn't happen, but since it did, something has gone seriously wrong.");
		}
		// ---------------------------------------------
		public @Override boolean hasNext() { return this.cursor.findNext() != null; }
		public @Override boolean hasPrevious() { return this.cursor.findPrevious() != null; }
		// ---------------------------------------------
		public @Override int previousIndex()
		{
			final int i = this.findCursorIndex();
			if(i < 0) return i; else return i-1;
		}
		public @Override int nextIndex()
		{
			final int i = this.findCursorIndex();
			if(i < 0) return i; else return i+1;
		}
		// ---------------------------------------------
		public @Override E previous() throws NoSuchElementException
		{
			final Node prev = this.cursor.findPrevious();
			if(prev == null) throw new NoSuchElementException();
			return (this.cursor = prev).get();
		}
		public @Override E next() throws NoSuchElementException
		{
			final Node next = this.cursor.findNext();
			if(next == null) throw new NoSuchElementException();
			return (this.cursor = next).get();
		}
		// ---------------------------------------------
	}
	// ==================================================
}
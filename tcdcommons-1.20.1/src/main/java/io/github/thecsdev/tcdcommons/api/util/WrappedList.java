package io.github.thecsdev.tcdcommons.api.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

/**
 * A custom {@link ArrayList}-like {@link Iterable} implementation
 * that gives you more control over what elements can and can not
 * be added to it.
 */
public class WrappedList<E> implements Iterable<E>
{
	// ==================================================
	/**
	 * Exposes the type {@link WrappedList} type.
	 */
	protected final Class<E> type;
	
	/**
	 * This is the main {@link List} that is being wrapped around.
	 */
	protected final List<E> list;
	// ==================================================
	public WrappedList(Class<E> type)
	{
		Objects.requireNonNull(type, "type must not be null.");
		this.type = type;
		this.list = Lists.newArrayList();
	}
	
	/**
	 * Returns the generic type of this {@link WrappedList}.
	 */
	public final Class<E> getType() { return this.type; }
	// ==================================================
	@Override
	public Iterator<E> iterator() { return list.iterator(); }
	// --------------------------------------------------
	/**
	 * Appends the given element to the end of the {@link #list}.
	 * @param element The element to add to the {@link #list}.
	 * @return True if {@link #filter(Object)} returns true and the
	 * element gets added to the {@link #list}. 
	 */
	public boolean add(E element) { return filter(element) && list.add(element); }
	
	/**
	 * Inserts the given element to the {@link #list} at the given index.
	 * @param index The index at which the specified element is to be inserted.
	 * @param element The element to add to the {@link #list}.
	 * @return True if {@link #filter(Object)} returns true and the
	 * element gets added to the {@link #list}.
	 * @throws IndexOutOfBoundsException See {@link List#add(int, Object)}.
	 */
	public boolean add(int index, E element)
	{
		if(!filter(element)) return false;
		list.add(index, element);
		return true;
	}
	
	/**
	 * Removes the first occurrence of the specified element from
	 * the {@link #list}, if it is present.
	 * @param element The element to remove from the {@link #list}.
	 * @return true if this list contained the specified element.
	 */
	public boolean remove(E element) { return list.remove(element); }
	
	/**
	 * Removes all elements from the {@link #list}.
	 */
	public void clear() { list.clear(); }
	// --------------------------------------------------
	/**
	 * Gets the element of the {@link #list} at a given index.
	 * @param index The index of the {@link #list} element.
	 * @return The element at the given index.
	 * @throws IndexOutOfBoundsException See {@link List#get(int)}.
	 */
	@Nullable
	public E get(int index) { return list.get(index); }
	
	/**
	 * Gets the element of the {@link #list} at a given index,
	 * without throwing an {@link IndexOutOfBoundsException}.
	 * @param index The index of the {@link #list} element.
	 * @param defaultValue The value that will be returned
	 * if the given index is out of bounds.
	 * @return The element at the given index, or the specified
	 * default value if the given index is out of bounds.
	 */
	public E get(int index, E defaultValue)
	{
		try { return list.get(index); }
		catch(IndexOutOfBoundsException ioobe) { return defaultValue; }
	}
	
	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * @param element The element to search for.
	 */
	public int indexOf(Object element) { return list.indexOf(element); }
	
	/**
	 * Returns true if the {@link #list} contains a given element.
	 * @param element The element to search for.
	 */
	public boolean contains(Object element) { return list.contains(element); }
	
	/**
	 * Returns the number of elements in this list. If this list
	 * contains more than {@link Integer#MAX_VALUE} elements,
	 * returns {@link Integer#MAX_VALUE}.<br/>
	 * <br/>
	 * See {@link List#size()}.
	 */
	public int size() { return list.size(); }
	// ==================================================
	/**
	 * Used to define what elements are allowed to be added
	 * to the wrapped {@link #list}. Called by {@link #add(Object)}
	 * to check if a given element is allowed to be added.
	 * @param element The element in question.
	 * @return True if a given element is allowed to be added
	 * to the {@link #list}. False otherwise.
	 */
	public boolean filter(E element) { return element != null; }
	// ==================================================
}
package io.github.thecsdev.tcdcommons.api.util.collections;

import com.google.common.annotations.Beta;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * A {@link Collection} that stores its elements in form of {@link WeakReference}s
 * @apiNote This {@link Collection} does not address the fact that entries could
 * get garbage collected as you're iterating over them, resulting in {@link Iterator#next()} returning null.
 */
@Beta //thing is, it's weak references. any object reference could just "poof" as you're iterating
public final class WeakCollection<E> extends AbstractCollection<E>
{
	// ==================================================
	protected final LinkedList<WeakReference<E>> list = new LinkedList<>();
	// ==================================================
	public WeakCollection() {}
	// --------------------------------------------------
	/**
	 * Cleans up "expired" {@link WeakReference}s whose
	 * {@link Object}s got garbage collected.
	 * @return The number of cleaned-up entries.
	 */
	public final int cleanUp()
	{
		int i = 0;
		final var listIter = this.list.listIterator();
		while(listIter.hasNext())
		{
			final var item = listIter.next();
			if(item.get() == null)
			{
				listIter.remove();
				i++;
			}
		}
		return i;
	}
	
	/**
	 * Returns the number of {@link WeakReference}s that
	 * no longer have a value assigned to them.
	 */
	public final int garbageSize()
	{
		int i = 0;
		for(final var item : this.list)
			if(item.get() == null)
				i++;
		return i;
	}
	// ==================================================
	public final @Override boolean contains(Object o)
	{
		for(final E item : this)
			if(Objects.equals(item, o))
				return true;
		return false;
	}
	public @Virtual @Override boolean add(E e) { cleanUp(); return this.list.add(new WeakReference<E>(e)); }
	public final @Override int size() { cleanUp(); return this.list.size(); }
	public final void clear() { this.list.clear(); }
	// --------------------------------------------------
	public final @Override Iterator<E> iterator() { return listIterator(); }
	public final ListIterator<E> listIterator() { return new WcIterator(); }
	// ==================================================
	private class WcIterator implements ListIterator<E>
	{
		protected final ListIterator<WeakReference<E>> listItr = WeakCollection.this.list.listIterator();
		protected WcIterator() {}
		
		public final @Override boolean hasNext() { return this.listItr.hasNext(); }
		public final @Override @Nullable E next() { return this.listItr.next().get(); }
		public final @Override boolean hasPrevious() { return this.listItr.hasPrevious(); }
		public final @Override @Nullable E previous() { return this.listItr.previous().get(); }
		public final @Override int nextIndex() { return this.listItr.nextIndex(); }
		public final @Override int previousIndex() { return this.listItr.previousIndex(); }
		public final @Override void remove() { this.listItr.remove(); }
		public final @Override void set(E e) { this.listItr.set(new WeakReference<E>(e)); }
		public final @Override void add(E e) { this.listItr.add(new WeakReference<E>(e)); }
	}
	// ==================================================
}
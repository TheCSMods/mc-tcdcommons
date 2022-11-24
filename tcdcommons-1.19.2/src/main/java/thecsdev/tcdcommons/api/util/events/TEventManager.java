package thecsdev.tcdcommons.api.util.events;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import thecsdev.tcdcommons.api.util.SubjectToChange;

/**
 * A {@link TEventManager} is the main object that holds {@link TEvent}s.
 */
@SubjectToChange(value = "Way too messy", when = "Likely next major release")
public class TEventManager extends Object
{
	// ==================================================
	protected final Class<?> owner;
	// ==================================================
	/**
	 * Creates a {@link TEventManager} instance.
	 * @param owner See {@link #getOwner()}.
	 * @exception NullPointerException When the argument is null.
	 */
	public TEventManager(Class<?> owner)
	{
		Objects.requireNonNull(owner, "owner must not be null.");
		this.owner = owner;
	}
	
	/**
	 * Returns the {@link Class} of the instance that owns this
	 * {@link TEventManager}. Only the owner class is allowed
	 * to invoke events present in this {@link TEventManager}.
	 */
	public Class<?> getOwner() { return this.owner; }
	// ==================================================
	/**
	 * A {@link TEvent} is an object that holds event handlers
	 * that handle a given event. {@link TEvent}s can only be
	 * invoked by the {@link TEventManager#getOwner()}.
	 */
	public class TEvent<E> extends Object
	{
		// ----------------------------------------------
		/**
		 * The event handlers are held in weak references
		 * so as to avoid memory leaks. In other words, it
		 * makes up for any reckless objects that might forget
		 * to unregister their handlers once they are done with them.
		 */
		protected final ArrayList<WeakReference<E>> eventHandlers = Lists.newArrayList();
		// ----------------------------------------------
		/**
		 * Unregisters all event handlers that are no longer in use.
		 */
		public final void garbageDump() { this.eventHandlers.removeIf(i -> i.get() == null); }
		// ----------------------------------------------
		/**
		 * Adds an event handler to this {@link TEvent}'s list of event handlers.<br/>
		 * <br/>
		 * <b>Important:</b> To avoid memory leaks, event handlers are stored in
		 * {@link WeakReference}s. This means that if you lose all references to
		 * the event handler, or if it goes out of scope, it will automatically be removed.
		 * @param handler The event handler that will be used to handle this event.
		 * @return The added event handler.
		 * @exception NullPointerException When the argument is null.
		 */
		public E addWeakEventHandler(E handler)
		{
			Objects.requireNonNull(handler, "handler must not be null.");
			this.eventHandlers.add(new WeakReference<E>(handler));
			return handler;
		}
		
		/**
		 * Removes an event handler from this {@link TEvent}'s list of event handlers.
		 * @param handler The event handler to remove.
		 * @return True if the event handler was present and was then removed.
		 * @exception NullPointerException When the argument is null.
		 */
		public boolean removeEventHandler(E handler)
		{
			Objects.requireNonNull(handler, "handler must not be null.");
			garbageDump();
			return this.eventHandlers.removeIf(i -> i.get() == handler);
		}
		// ----------------------------------------------
		/**
		 * Invokes all event handlers that belong to this {@link TEvent}.<br/>
		 * <b>Notice:</b> Only the {@link TEventManager#getOwner()} is allowed to call this method.
		 * @param handlerInvoker The lambda expression used to invoke an event handler.
		 * @exception NullPointerException When the argument is null.
		 * @exception IllegalCallerException Only the {@link TEventManager#getOwner()} may invoke events.
		 */
		@SubjectToChange
		public void p_invoke(Consumer<E> handlerInvoker)
		{
			//TODO - Enforce IllegalCallerException efficiently if that's even possible
			//invoke all event handlers
			Objects.requireNonNull(handlerInvoker, "handlerInvoker must not be null.");
			garbageDump();
			for(WeakReference<E> handlerRef : this.eventHandlers)
			{
				E handler = handlerRef.get();
				if(handler == null) continue; //in case it does happen
				handlerInvoker.accept(handler);
			}
		}
		// ----------------------------------------------
	}
	// ==================================================
}
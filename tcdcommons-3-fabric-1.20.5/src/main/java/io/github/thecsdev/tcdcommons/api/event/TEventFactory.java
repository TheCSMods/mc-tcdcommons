package io.github.thecsdev.tcdcommons.api.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.util.collections.IdealList;

/**
 * An event system implementation inspired by and similar to
 * <code>dev.architectury.event</code>'s implementation.<br/>
 * <br/>
 * The reason for this implementation is customization and avoiding dependencies.
 * @see TEvent
 * @see TKeyedEvent
 * @see io.github.thecsdev.tcdcommons.api.event
 */
public final class TEventFactory
{
	// ==================================================
	private TEventFactory() {}
	// ==================================================
	/**
	 * <p> Creates a {@link TEvent}.
	 * <p> This type of event stores its listeners in a {@link List}, and the
	 * listeners must be single-method interfaces that <b>return void</b> when executed.
	 * @param <T> The event listener interface type.
	 * @param eventTypeClassGetter Generic type getter. Leave this empty. Do not pass any arguments.
	 */
	//maintaining the same method naming conventions as 'dev.architectury.event'
	//for consistency and for making it easier to switch to this event system
	public static @SafeVarargs <T> TEvent<T> createLoop(T... eventTypeClassGetter)
	{
		Objects.requireNonNull(eventTypeClassGetter);
		
		//create list of listeners
		final List<T> listeners = Collections.synchronizedList(new IdealList<>());
		
		//create the invoker for event listeners
        @SuppressWarnings("unchecked")
		final T invoker = (T)Proxy.newProxyInstance(
				TEventFactory.class.getClassLoader(),
				new Class[] { eventTypeClassGetter.getClass().getComponentType() },
				(proxy, method, args) ->
				{
					try
					{
						//iterate over all listeners
				        for (T listener : listeners)
				        	method.invoke(listener, args);
				        return null;
					}
					catch(Exception listenerException)
					{ throw new RuntimeException("Failed to invoke event hanlder.", listenerException); }
			    }
			);
        
        //create and return event implementation
        return new TEventImpl<>(listeners, invoker);
	}
	
	/**
	 * Same as {@link #createLoop(Object...)}, but listeners must return a {@link TEventResult}.
	 * @param <T> The event listener interface type.
	 * @param eventTypeClassGetter Generic type getter. Leave this empty. Do not pass any arguments.
	 * @apiNote Listeners of this {@link TEvent} type must return {@link TEventResult}.
	 */
	public static @SafeVarargs <T> TEvent<T> createEventResult(T... eventTypeClassGetter)
	{
		Objects.requireNonNull(eventTypeClassGetter);
		
		//create list of listeners
		final List<T> listeners = Collections.synchronizedList(new IdealList<>());
		
		//create the invoker for event listeners
        @SuppressWarnings("unchecked")
		final T invoker = (T)Proxy.newProxyInstance(
				TEventFactory.class.getClassLoader(),
				new Class[] { eventTypeClassGetter.getClass().getComponentType() },
				(proxy, method, args) ->
				{
					try
					{
						//prepare to execute; start of with canceling none
						TEventResult result = TEventResult.CANCEL_NONE;
						
						//iterate over all listeners
				        for (T listener : listeners)
				        {
				        	//invoke and combine the results
				        	final Object listenerResult = method.invoke(listener, args);
				        	try { result = result.combine((TEventResult)listenerResult); }
				        	catch(ClassCastException cce) { __handleEventResultCCE(cce, method, listenerResult); }
				        	
				        	//check if evaluating further
				        	if(result.isPropagationCancelled())
				        		break;
				        }
				        
				        //return the result
				        return result;
					}
					catch(Exception listenerException)
					{ throw new RuntimeException("Failed to invoke event hanlder.", listenerException); }
			    }
			);
        
        //create and return event implementation
        return new TEventImpl<>(listeners, invoker);
	}
	// ==================================================
	/**
	 * Similar to {@link #createLoop(Object...)}, but the listeners
	 * are stored in a {@link List} of {@link WeakReference}s.
	 * @param <T> The event listener interface type.
	 * @param eventTypeClassGetter Generic type getter. Leave this empty. Do not pass any arguments.
	 */
	public static @SafeVarargs <T> TEvent<T> createWeakLoop(T... eventTypeClassGetter)
	{
		Objects.requireNonNull(eventTypeClassGetter);
		
		//create list of listeners
		final List<WeakReference<T>> listeners = Collections.synchronizedList(new IdealList<>());
		
		//create the invoker for event listeners
        @SuppressWarnings("unchecked")
		final T invoker = (T)Proxy.newProxyInstance(
				TEventFactory.class.getClassLoader(),
				new Class[] { eventTypeClassGetter.getClass().getComponentType() },
				(proxy, method, args) ->
				{
					try
					{
						//iterate over all listeners
						final var iter = listeners.listIterator();
						while(iter.hasNext())
						{
							//obtain the listener reference, and the listener from the reference
							final var listenerRef = iter.next();
							final var listener = listenerRef.get();
							//check if the listener was garbage-collected...
							if(method == null)
							{
								//...and if it was, remove the reference and continue
								iter.remove();
								continue;
							}
							//invoke the listener
							method.invoke(listener, args);
						}
				        return null;
					}
					catch(Exception listenerException)
					{ throw new RuntimeException("Failed to invoke event hanlder.", listenerException); }
			    }
			);
        
        //create and return event implementation
        return new TWeakEventImpl<>(listeners, invoker);
	}
	
	/**
	 * Same as {@link #createEventResult(Object...)}, but the listeners
	 * are stored in a {@link List} of {@link WeakReference}s.
	 * @param <T> The event listener interface type.
	 * @param eventTypeClassGetter Generic type getter. Leave this empty. Do not pass any arguments.
	 * @apiNote Listeners of this {@link TEvent} type must return {@link TEventResult}.
	 */
	public static @SafeVarargs <T> TEvent<T> createWeakEventResult(T... eventTypeClassGetter)
	{
		Objects.requireNonNull(eventTypeClassGetter);
		
		//create list of listeners
		final List<WeakReference<T>> listeners = Collections.synchronizedList(new IdealList<>());
		
		//create the invoker for event listeners
        @SuppressWarnings("unchecked")
		final T invoker = (T)Proxy.newProxyInstance(
				TEventFactory.class.getClassLoader(),
				new Class[] { eventTypeClassGetter.getClass().getComponentType() },
				(proxy, method, args) ->
				{
					try
					{
						//prepare to execute; start of with canceling none
						TEventResult result = TEventResult.CANCEL_NONE;
						
						//iterate over all listeners
						final var iter = listeners.listIterator();
						while(iter.hasNext())
						{
							//obtain the listener reference, and the listener from the reference
							final var listenerRef = iter.next();
							final var listener = listenerRef.get();
							//check if the listener was garbage-collected...
							if(method == null)
							{
								//...and if it was, remove the reference and continue
								iter.remove();
								continue;
							}
				        	//invoke and combine the results
				        	final Object listenerResult = method.invoke(listener, args);
				        	try { result = result.combine((TEventResult)listenerResult); }
				        	catch(ClassCastException cce) { __handleEventResultCCE(cce, method, listenerResult); }
				        	
				        	//check if evaluating further
				        	if(result.isPropagationCancelled())
				        		break;
						}
				        
				        //return the result
				        return result;
					}
					catch(Exception listenerException)
					{ throw new RuntimeException("Failed to invoke event hanlder.", listenerException); }
			    }
			);
        
        //create and return event implementation
        return new TWeakEventImpl<>(listeners, invoker);
	}
	// ==================================================
	/**
	 * <p> Creates a {@link TKeyedEvent}.
	 * <p> This type of event stores its listeners in a {@link Map}, and the
	 * listeners must be single-method interfaces that <b>return void</b> when executed.
	 * <p> In the {@link Map}, each event handler is associated with a "key" that serves as
	 * a unique "identifier" for the given event handler.
	 * @param <T> The event listener interface type.
	 * @param eventTypeClassGetter Generic type getter. Leave this empty. Do not pass any arguments.
	 */
	public static @SafeVarargs <T> TKeyedEvent<T> createKeyedLoop(T... eventTypeClassGetter)
	{
		Objects.requireNonNull(eventTypeClassGetter);
		
		//create list of listeners
		final LinkedHashMap<String, T> listeners = new LinkedHashMap<String, T>();
		
		//create the invoker for event listeners
        @SuppressWarnings("unchecked")
		final T invoker = (T)Proxy.newProxyInstance(
				TEventFactory.class.getClassLoader(),
				new Class[] { eventTypeClassGetter.getClass().getComponentType() },
				(proxy, method, args) ->
				{
					try
					{
						//iterate over all listeners
				        for (T listener : listeners.values())
				        	method.invoke(listener, args);
				        return null;
					}
					catch(Exception listenerException)
					{ throw new RuntimeException("Failed to invoke event hanlder.", listenerException); }
			    }
			);
        
        //create and return event implementation
        return new TKeyedEventImpl<T>(listeners, invoker);
	}
	
	/**
	 * Same as {@link #createKeyedLoop(Object...)}, but listeners must return a {@link TEventResult}.
	 * @param <T> The event listener interface type.
	 * @param eventTypeClassGetter Generic type getter. Leave this empty. Do not pass any arguments.
	 * @apiNote Listeners of this {@link TEvent} type must return {@link TEventResult}.
	 */
	public static @SafeVarargs <T> TKeyedEvent<T> createKeyedEventResult(T... eventTypeClassGetter)
	{
		Objects.requireNonNull(eventTypeClassGetter);
		
		//create list of listeners
		final LinkedHashMap<String, T> listeners = new LinkedHashMap<String, T>();
		
		//create the invoker for event listeners
        @SuppressWarnings("unchecked")
		final T invoker = (T)Proxy.newProxyInstance(
				TEventFactory.class.getClassLoader(),
				new Class[] { eventTypeClassGetter.getClass().getComponentType() },
				(proxy, method, args) ->
				{
					try
					{
						//prepare to execute; start of with cancelling none
						TEventResult result = TEventResult.CANCEL_NONE;
						
						//iterate over all listeners
				        for (T listener : listeners.values())
				        {
				        	//invoke and combine the results
				        	final Object listenerResult = method.invoke(listener, args);
				        	try { result = result.combine((TEventResult)listenerResult); }
				        	catch(ClassCastException cce) { __handleEventResultCCE(cce, method, listenerResult); }
				        	
				        	//check if evaluating further
				        	if(result.isPropagationCancelled())
				        		break;
				        }
				        
				        //return the result
				        return result;
					}
					catch(Exception listenerException)
					{ throw new RuntimeException("Failed to invoke event hanlder.", listenerException); }
			    }
			);
        
        //create and return event implementation
        return new TKeyedEventImpl<T>(listeners, invoker);
	}
	// ==================================================
	/**
	 * {@link TEvent}s whose listeners are supposed to return {@link TEventResult}
	 * must return {@link TEventResult}. If they don't, a {@link ClassCastException}
	 * will take place. Report that here.
	 * @param cce The {@link ClassCastException} that took place.
	 * @param listenerMethod The event handler {@link Method} that was invoked.
	 * @param listenerResult The event handler's returned value.
	 */
	private static @Internal void __handleEventResultCCE
	(ClassCastException cce, Method listenerMethod, Object listenerResult)
	{
		//obtain the name of the type of the listener's result
		final var lrTypeName = Optional.ofNullable(listenerResult)
				.map(o -> o.getClass().getName())
				.orElse(listenerMethod.getReturnType().equals(Void.TYPE) ? "void" : "null");
		//construct the exception message
		final var msg = "Failed to handle an event handler; "
				+ "Expected '" + TEventResult.class.getName() + "', got "
						+ "'" + lrTypeName + "'.";
		//throw the runtime exception
		throw new RuntimeException(msg, cce);
	}
	// ==================================================
}
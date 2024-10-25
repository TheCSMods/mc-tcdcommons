package io.github.thecsdev.tcdcommons.api.event;

/**
 * An event system implementation inspired by and similar to
 * <code>dev.architectury.event</code>'s implementation.<br/>
 * <br/>
 * The reason for this implementation is customization and avoiding dependencies.
 * @see io.github.thecsdev.tcdcommons.api.event
 */
public interface TEvent<T>
{
	//maintaining the same method naming conventions as 'dev.architectury.event'
	//for consistency and for making it easier to switch to this event system
	T invoker();
	boolean register(T listener);
	boolean unregister(T listener);
	boolean isRegistered(T listener);
    void clearListeners();
}
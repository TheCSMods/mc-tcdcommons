package io.github.thecsdev.tcdcommons.api.client.gui.events;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.util.TriConsumer;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.util.events.TEventManager;

/**
 * This container for {@link TEvent}s related to {@link TElement}s.
 */
public class TElementEvents extends TEventManager
{
	// ==================================================
	protected final TElement telement;
	// --------------------------------------------------
	/**
	 * This event handler is automatically registered and it
	 * automatically calls {@link TElement#updateRenderingBoundingBox()}
	 * whenever the element is moved.
	 */
	public final BiConsumer<Integer, Integer> EHANDLER_MOVED_URBB = (dX,dY) -> getTElement().updateRenderingBoundingBox();
	// --------------------------------------------------
	/**
	 * This event is invoked whenever
	 * {@link TElement#setPosition(int, int, int)} is called.<br/>
	 * The first parameter is deltaX, and the second parameter is deltaY.
	 */
	public final TEvent<BiConsumer<Integer, Integer>> MOVED = new TEvent<>();
	
	/**
	 * This event is invoked whenever a child is added to
	 * or removed from the given {@link TElement}.<br/>
	 * <b>Parameter 1:</b> The child {@link TElement} in question.<br/>
	 * <b>Parameter 2:</b> A {@link Boolean} that is true when the child
	 * is added and false when the child is removed.<br/>
	 * <b>Parameter 3:</b> Whether or not the child was repositioned while
	 * it was being added/removed.<br/>
	 * <br/>
	 * <b>Note:</b> When removing, the event will be called after the element was removed.
	 */
	/* TODO - Danger: Event invoked outside of {@link TElement}.
	 * This may result in {@link IllegalCallerException} in the future. Fix this.
	 */
	public final TEvent<TriConsumer<TElement, Boolean, Boolean>> CHILD_AR = new TEvent<>();
	// ==================================================
	public TElementEvents(TElement owner)
	{
		//construct
		super(Objects.requireNonNull(owner, "owner must not be null.").getClass());
		this.telement = owner;
		//register bounding box updater
		this.MOVED.addWeakEventHandler(EHANDLER_MOVED_URBB);
	}
	
	/**
	 * Returns the {@link TElement} that owns this {@link TEventManager}.
	 */
	public final TElement getTElement() { return this.telement; }
	// ==================================================
}
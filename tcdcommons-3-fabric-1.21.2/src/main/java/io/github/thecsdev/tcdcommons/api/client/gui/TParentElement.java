package io.github.thecsdev.tcdcommons.api.client.gui;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenWrapper;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.util.interfaces.IEnableStateProvider;

/**
 * The {@code TParentElement} interface represents a GUI element in the {@code TCDCommons} framework.
 * Implementations of this interface can act as parent elements for other GUI elements, providing
 * a structural hierarchy for GUI representation and interaction.
 * <p>
 * Each {@code TParentElement} has an X and Y coordinate, as well as a width and height. These
 * attributes define the element's position and size within the GUI layout.
 * <p>
 * Implementing classes should ensure these properties are correctly maintained to ensure
 * accurate representation and interaction within the GUI.
 *
 * @see TScreen
 * @see TElement
 * 
 * @apiNote Default methods featured in this interface aren't intended to be overridden,
 * and may be set to "final" at any time.
 */
public interface TParentElement extends IEnableStateProvider
{
	// ==================================================
	/**
	 * The maximum depth of nested children that can be iterated over by
	 * the {@link #__findChild(TParentElement, Predicate, boolean, int)} method.
	 * <p>
	 * This limit is enforced to prevent several potential issues:
	 * <ul>
	 *	 <li>Stack overflow due to excessive recursion</li>
	 *	 <li>Performance degradation from handling an excessive number of nested children</li>
	 *	 <li>Infinite recursion resulting from a child element containing itself as one of its children</li>
	 * </ul>
	 * If the iteration depth exceeds this limit, the
	 * {@link #__findChild(TParentElement, Predicate, boolean, int)} method will return {@code null}.
	 */
	public static final int MAX_CHILD_NESTING_DEPTH = 0x10;
	// ==================================================
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	//
	default int getEndX() { return getX() + getWidth(); }
	default int getEndY() { return getY() + getHeight(); }
	// --------------------------------------------------
	/**
	 * Renders this GUI element on the screen.
	 * @param pencil The {@link TDrawContext}.
	 */
	public void render(TDrawContext pencil);
	

	/**
	 * Returns the Z-index for this {@link TParentElement}.<br/>
	 * Should be between 0 (inclusive) and 100 (also inclusive),
	 * as any other values may cause this element to not render
	 * properly or to not render at all.
	 */
	default float getZIndex() { return 0; }
	
	/**
	 * An input handler that handles inputs on the
	 * {@link TInputContext.InputDiscoveryPhase#MAIN} input phase.
	 * @param inputContext The {@link TInputContext} containing information about a given input.
	 * @see TParentElement#input(TInputContext, TInputContext.InputDiscoveryPhase)
	 * @apiNote Automatically called by {@link TScreenWrapper}. Do not call this yourself.
	 */
	default boolean input(TInputContext inputContext) { return false; }
	
	/**
	 * An input handler that handles inputs on all
	 * possible {@link TInputContext.InputDiscoveryPhase}s.
	 * @param inputContext The {@link TInputContext} containing information about a given input.
	 * @param inputPhase The current {@link TInputContext.InputDiscoveryPhase}.
	 * @see TInputContext.InputDiscoveryPhase
	 * @apiNote Automatically called by {@link TScreenWrapper}. Do not call this yourself.
	 */
	default boolean input(TInputContext inputContext, TInputContext.InputDiscoveryPhase inputPhase) { return false; }
	// ==================================================
	public @Nullable TParentElement getParent();
	public TElementList getChildren();
	//
	default boolean addChild(TElement child) { return getChildren().add(child); }
	default boolean addChild(TElement child, boolean reposition) { return getChildren().add(child, reposition); }
	default boolean removeChild(TElement child) { return getChildren().remove(child); }
	default boolean removeChild(TElement child, boolean reposition) { return getChildren().remove(child, reposition); }
	default void clearChildren() { getChildren().clear(); }
	// --------------------------------------------------
	/**
	 * Returns {@code true} if {@link #getEnabled()} returns {@code true},
	 * and {@link #getParent()}'s {@link #isEnabled()} also returns {@code true}.<p>
	 * In other words, this will return {@code true} if both this element and all
	 * of its {@link TParentElement}s are enabled.
	 */
	default boolean isEnabled() { return getEnabled() && (getParent() == null || getParent().isEnabled()); }
	default @Override boolean getEnabled() { return true; }
	// ==================================================
	/**
	 * Finds and returns the first {@link TParentElement} for which the provided action returns true.
	 *
	 * @param predicate A predicate that takes a {@link TParentElement} and returns a {@link Boolean}.
	 * This predicate is applied to each parent element in the hierarchy until it returns true or
	 * no more parent elements are found.
	 * @return The first {@link TParentElement} for which the action returns true, or null if no
	 * such element is found.
	 */
	default @Nullable TParentElement findParent(Predicate<TParentElement> predicate)
	{
		var parent = getParent();
		while(parent != null)
		{
			if(predicate.test(parent)) return parent;
			parent = parent.getParent();
		}
		return null;
	}
	
	/**
	 * Finds and returns the first parent {@link TElement} for which the provided action returns true.
	 *
	 * @param predicate A predicate that takes a {@link TElement} and returns a {@link Boolean}.
	 * This predicate is applied to each parent element in the hierarchy that is an instance
	 * of {@link TElement} until it returns true or no more parent elements are found.
	 * @return The first parent {@link TElement} for which the action returns true, or null if
	 * no such element is found.
	 */
	default @Nullable TElement findParentTElement(Predicate<TElement> predicate)
	{
		var parent = getParent();
		while(parent != null)
		{
			if(parent instanceof TElement && predicate.test((TElement) parent)) return (TElement) parent;
			parent = parent.getParent();
		}
		return null;
	}
	// --------------------------------------------------
	/**
	 * Iterates over every child in {@link #getChildren()}, and applies a {@link Consumer} to it.
	 * <p>
	 * This is similar to {@link #findChild(Predicate, boolean)}, except the {@link Consumer}
	 * does not return 'true' or 'false', and therefore there's no way to end the loop here.
	 * @param action The {@link Consumer} to apply for each child element.
	 * @param nested Will this {@link Consumer} be applied to the children of children?
	 */
	default void forEachChild(Consumer<TElement> action, boolean nested)
	{
		findChild(child ->
		{
			if(action != null) action.accept(child);
			return false;
		}, nested);
	}
	
	/**
	 * Iterates over every child in {@link #getChildren()}, and applies a {@link Predicate} to it.
	 * <p>
	 * When the {@link Predicate} returns true for child {@link TElement}, the "for-each"
	 * operation will terminate, and the child {@link TElement} will be returned.
	 * @param predicate The {@link Predicate} to apply for each child element.
	 * @param nested Will this {@link Predicate} be applied to the children of children?
	 * @return The first {@link TElement} for which the {@link Predicate} returns true.
	 */
	default @Nullable TElement findChild(Predicate<TElement> predicate, boolean nested)
	{
		return __findChild(this, predicate, nested, 0);
	}
	
	/**
	 * Same as {@link #findChild(Predicate, boolean)}, but with some extra fancy parameters.
	 * @param parent The parent {@link TParentElement} of all children that will be iterated.
	 * @param predicate The {@link Predicate} to apply for each child element.
	 * @param nested Will this {@link Predicate} be applied to the children of children?
	 * @param iteration The nesting iteration number. Pass 0 if this is the first call.
	 * @return The first {@link TElement} for which the {@link Predicate} returns true.
	 */
	private static @Internal @Nullable TElement __findChild(
			final TParentElement parent,
			final Predicate<TElement> predicate,
			final boolean nested,
			final int iteration)
	{
		//enforce a nesting iteration limit to prevent nesting-related issues
		if(iteration > MAX_CHILD_NESTING_DEPTH) return null;
		
		//iterate all parent's children
		//(MUST NOT RETURN NULL FROM THE FOR LOOP)
		for(var child : parent.getChildren())
		{
			//maintenance stuff - error correction fallback
			child.__updateParent(parent);
			
			//apply the predicate
			if(predicate != null && predicate.test(child))
				return child;
			
			//if nested, then apply the predicate to child's children
			if(nested)
			{
				//MUST NOT RETURN NULL FROM THE FOR LOOP:
				//The reason for that is that returning the result of the nested call
				//will terminate the iteration over the rest of the children, resulting
				//in all children other than "1st child" being ignored, which is bad.
				final var nestedFind = __findChild(child, predicate, true, iteration + 1);
				if(nestedFind != null) return nestedFind; // Only return if a matching child was found
			}
		}
		
		//if nothing gets returned while iterating, return null
		return null;
	}
	
	/**
	 * Similar to {@link #findChild(Predicate, boolean)}, but it returns the
	 * last instance that matches a given predicate.
	 * @param predicate The {@link Predicate} to apply for each child element.
	 * @param nested Will this {@link Predicate} be applied to the children of children?
	 */
	default @Nullable TElement findLastChild(Predicate<TElement> predicate, boolean nested)
	{
		final AtomicReference<TElement> result = new AtomicReference<TElement>(null);
		forEachChild(child ->
		{
			if(predicate.test(child))
				result.set(child);
		},
		nested);
		return result.get();
	}
	// ==================================================
}
package thecsdev.tcdcommons.api.client.gui;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.DrawableHelper;
import thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import thecsdev.tcdcommons.api.client.gui.util.TElementList;

/**
 * <i>Note: Any methods in here may not use any names Minecraft uses, as Minecraft's methods are obfuscated.<br/>
 * Examples include methods such as 'render', and so on...</i>
 */
public interface TParentElement
{
	// ==================================================
	/**
	 * Returns the X coordinate of this {@link TParentElement}.<br/>
	 * Names such as `getX()` are avoided because of obfuscation handling issues.<br/>
	 * See {@link TScreen} for more info.
	 */
	public int getTpeX();
	
	/**
	 * Returns the Y coordinate of this {@link TParentElement}.<br/>
	 * Names such as `getY()` are avoided because of obfuscation handling issues.<br/>
	 * See {@link TScreen} for more info.
	 */
	public int getTpeY();
	
	/**
	 * Returns the width of this {@link TParentElement}.<br/>
	 * Names such as `getWidth()` are avoided because of obfuscation handling issues.<br/>
	 * See {@link TScreen} for more info.
	 */
	public int getTpeWidth();
	
	/**
	 * Returns the height of this {@link TParentElement}.<br/>
	 * Names such as `getHeight()` are avoided because of obfuscation handling issues.<br/>
	 * See {@link TScreen} for more info.
	 */
	public int getTpeHeight();
	// --------------------------------------------------
	/** Returns {@link #getTpeX()} + {@link #getTpeWidth()}. */
	public default int getTpeEndX() { return getTpeX() + getTpeWidth(); }
	
	/** Returns {@link #getTpeY()} + {@link #getTpeHeight()}. */
	public default int getTpeEndY() { return getTpeY() + getTpeHeight(); }
	// ==================================================
	/**
	 * Returns the Z-offset aka the Z-index of this element.<br/>
	 * Must be between 0 (inclusive) and 100 (also inclusive).
	 * Any other values may cause this element to render improperly
	 * or not to render at all.<br/>
	 * <br/>
	 * Please use {@link DrawableHelper#getZOffset()} and
	 * {@link DrawableHelper#setZOffset(int)} if you wish to
	 * override the Z-index behavior.
	 */
	default double getZIndex() { return 0; }
	
	/**
	 * Returns the alpha (opacity) of this {@link TElement}.
	 */
	public default float getAlpha() { return 1; }
	// ==================================================
	/**
	 * Returns the {@link TParentElement} for this {@link TParentElement}.
	 */
	@Nullable
	public TParentElement getTParent();
	
	/**
	 * Returns the children {@link TElement}s that belong to this object.<br/>
	 * The child nesting level can not exceed seven (7).
	 * @return The list of children. <b>This function must not return null.<b/>
	 */
	public TElementList getTChildren();
	
	/**
	 * Clears all of the child {@link TElement}s from the
	 * {@link #getTChildren()} list.
	 */
	default void clearTChildren() { getTChildren().clear(); }
	// --------------------------------------------------
	/**
	 * Returns the last child in the list of children.
	 * Child nesting can be accounted for.
	 * @param nested Retrieve the last nested child?
	 * (the last child of the last child of the last child...)
	 */
	@Nullable
	default TElement getLastTChild(boolean nested)
	{
		//check children list size
		TElementList children = getTChildren();
		if(children == null || children.size() == 0)
			return null;
		
		//look for last
		TElement last = children.get(children.size() - 1);
		while(nested && last.getTChildren().size() > 0)
			last = last.getLastTChild(nested);
		
		//return the last one
		return last;
	}
	// --------------------------------------------------
	/**
	 * Adds a {@link TElement} child to the list of children.<br/>
	 * See also {@link #getTChildren()}.
	 * @param <T> The type of {@link TElement} child to add.
	 * @param child The {@link TElement} child to add.
	 * @return The added {@link TElement} child.
	 */
	default <T extends TElement> boolean addTChild(T child) { return getTChildren().add(child); }
	
	/**
	 * Adds a {@link TElement} child to the list of children.<br/>
	 * See also {@link #getTChildren()}.
	 * @param <T> The type of {@link TElement} child to add.
	 * @param child The {@link TElement} child to add.
	 * @param reposition Whether or not to reposition the child based on the parent's position.
	 * @return The added {@link TElement} child.
	 */
	default <T extends TElement> boolean addTChild(T child, boolean reposition) { return getTChildren().add(child, reposition); }
	// --------------------------------------------------
	/**
	 * Removes a {@link TElement} child from the list of children.<br>
	 * See also {@link #getTChildren()}.
	 * @param <T> The type of {@link TElement} child to remove.
	 * @param child The {@link TElement} child to remove.
	 * @return Same as {@link List#remove(Object)}, or false if
	 * {@link #getTChildren()} returned null.
	 */
	default <T extends TElement> boolean removeTChild(T child) { return getTChildren().remove(child); }
	
	/**
	 * Removes a TElement child from the list of children, while
	 * also letting you choose if you wish to reposition the child.
	 * @param <T> The type of {@link TElement} child to remove.
	 * @param child The {@link TElement} child to remove.
	 * @param reposition Whether or not to reposition the child based on the parent's position.
	 */
	default <T extends TElement> boolean removeTChild(T child, boolean reposition) { return getTChildren().remove(child, reposition); }
	// ==================================================
	/**
	 * Returns the bounding box used by the {@link TScreen}
	 * when rendering this {@link TParentElement}.<br/>
	 * A {@link TElement} will only render within this bounding box.<br/>
	 * <br/>
	 * When <b>null</b> is returned, that means that this
	 * element is out of it's parent's bounds and can not be rendered.<br/>
	 * <br/>
	 * <b>Important notice:</b><br/>
	 * Unless you want massive lags and fps drops, DO NOT create new
	 * {@link Rectangle} instances inside of this method. Cache them instead.
	 */
	@Nullable
	public Rectangle getRenderingBoundingBox();
	
	/**
	 * Updates to the rendering bounding box should be handled
	 * in here, and not in {@link #getRenderingBoundingBox()}.
	 * This is for performance reasons. To make the box null,
	 * set it's width or height to 0.
	 * @param box This is where the data about the bounding box is stored.
	 */
	public void updateRenderingBoundingBox();
	// ==================================================
	/**
	 * Iterates over every child in {@link #getTChildren()}, and applies a given
	 * {@link Function} to it. To cancel iterating over children, the given
	 * {@link Function} needs to return true.<br/>
	 * <br/>
	 * The first {@link Function} parameter is the child, while the second
	 * {@link Function} parameter is the child's parent if the child has a parent.<br/>
	 * <br/>
	 * <b>Example:</b><br/>
	 * <code>myTElement.forEachChild((child, parent) -> { ... return false; }, true);</code>
	 * @param action The {@link Function} to apply for each child. Have the
	 * function return true to cancel the iteration.
	 * @param nested Whether or not to iterate over each child's children as well.
	 * @return The {@link TElement} that cancelled the iteration, if any.
	 */
	@Nullable
	default TElement forEachChild(@Nullable Function<TElement, Boolean> action, boolean nested)
	{
		return forEachChild(action, null, nested);
	}
	
	/**
	 * Please see {@link #forEachChild(Function, boolean)}.
	 * @param action The {@link Function} to apply for each child. Have the
	 * function return true to cancel the iteration.
	 * @param postAction Same as <b>action</b>, except it is executed after
	 * <b>action</b> and after nesting is taken care of.
	 * @param nested Whether or not to iterate over each child's children as well.
	 * @return The {@link TElement} that cancelled the iteration, if any.
	 */
	@Nullable
	default TElement forEachChild(
			@Nullable Function<TElement, Boolean> action,
			@Nullable Function<TElement, Boolean> postAction,
			boolean nested)
	{
		return internal_forEachChild(this, action, postAction, nested, 0);
	}
	// --------------------------------------------------
	/**
	 * For handling {@link #forEachChild(BiFunction, BiFunction, boolean)} internally.
	 * @param iteration A fail-safe to avoid stack overflows, as someone
	 * might set a parent to also be a child of it's child.
	 */
	@Nullable
	private static TElement internal_forEachChild(
		TParentElement parent,
		@Nullable Function<TElement, Boolean> action,
		@Nullable Function<TElement, Boolean> postAction,
		boolean nested,
		int iteration)
	{
		//null check
		TElementList children = parent.getTChildren();
		if(children == null || iteration > 7) return null;
		
		//obtain parent TElement and TScreen - no longer needed. tracked by TElementList
		TElement parentTEl = (parent instanceof TElement) ? (TElement) parent : null;
		TScreen  parentTSc = (parent instanceof TScreen) ?
				(TScreen) parent :
				((parentTEl != null) ? parentTEl.screen : null);
		
		//iterate children
		for(TElement child : parent.getTChildren())
		{
			//null check just in case a null slipped in
			if(child == null) continue;
			
			//parent assignments
			child.parent = parentTEl;
			child.screen = parentTSc;
			
			//apply the action to each child
			if(action != null && action.apply(child))
				return child;
			
			//handle nesting
			if(nested)
			{
				var canceller = internal_forEachChild(child, action, postAction, nested, iteration + 1);
				if(canceller != null) return canceller;
			}
			
			//apply the post-action to each child
			if(postAction != null && postAction.apply(child))
				return child;
		}
		
		//if not cancelled, return null
		return null;
	}
	// ==================================================
}
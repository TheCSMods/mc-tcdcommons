package io.github.thecsdev.tcdcommons.api.client.gui.util.exceptions;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

import java.util.Objects;

/**
 * A {@link RuntimeException} that is thrown when attempting to add a
 * child {@link TElement} to a {@link TParentElement}, but the said
 * {@link TElement} does not support being added to said {@link TParentElement}.
 */
public @Virtual class IllegalParentException extends RuntimeException
{
	private static final long serialVersionUID = 3424486469930508229L;
	protected final TParentElement parent;
	protected final TElement attemptedChild;
	public IllegalParentException(TParentElement parent, TElement attemptedChild) throws NullPointerException
	{
		super(constructMessage(parent, attemptedChild));
		this.parent = Objects.requireNonNull(parent);
		this.attemptedChild = Objects.requireNonNull(attemptedChild);
	}
	
	public final TParentElement getParent() { return this.parent; }
	public final TElement getAttemptedChild() { return this.attemptedChild; }
	
	private static final String M = "Attempted to add a child element to a parent the child does not support. "
			+ "Parent: %s; Child: %s;";
	public static String constructMessage(TParentElement parent, TElement attemptedChild)
	{
		return String.format(M, Objects.toString(parent), Objects.toString(attemptedChild));
	}
}
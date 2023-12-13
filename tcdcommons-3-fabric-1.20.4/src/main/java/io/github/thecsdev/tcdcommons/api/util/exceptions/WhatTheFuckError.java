package io.github.thecsdev.tcdcommons.api.util.exceptions;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * For when the behavior of something else messes with your code.
 * <p>
 * Example 1:
 * <blockquote>
 * Everything looks normal, and should work as expected,
 * but for some reason it either doesn't work or it's behaving weird.
 * <p>
 * You try to debug the issue for ages, but despite everything,
 * the code still looks fine, and the issue is nowhere to be found
 * <p>
 * You then find out about some obscure feature or behavior of the
 * code that executes your code, that completely shatters your expectations.
 * </blockquote>
 * Example 2:
 * <blockquote>
 * Something has injected its code into your code.
 * </blockquote>
 */
public @Virtual class WhatTheFuckError extends Error
{
	private static final long serialVersionUID = 5291684741840835617L;

	public WhatTheFuckError() { super(); }
	public WhatTheFuckError(String message) { super(message); }
	public WhatTheFuckError(Throwable cause) { super(cause); }
	public WhatTheFuckError(String message, Throwable cause) { super(message, cause); }
	
	public final @Override String getMessage() { return super.getMessage(); }
	public final synchronized @Override Throwable getCause() { return super.getCause(); }
}
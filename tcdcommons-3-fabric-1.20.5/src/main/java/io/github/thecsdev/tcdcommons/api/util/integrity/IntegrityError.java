package io.github.thecsdev.tcdcommons.api.util.integrity;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.exceptions.WhatTheFuckError;

/**
 * Thrown to indicate that a {@link Class} or a set of {@link Class}es have been tampered with.
 * This is a custom {@link Error} type that extends the standard {@link java.lang.Error} class.
 * <p>
 * An {@link IntegrityError} includes that one or more {@link Class}es that were affected by an integrity violation.
 * The most common cause of integrity violations are things like unwanted code injections.
 * <p>
 * The array of affected {@link Class}es can be retrieved using the {@link #getAffectedClasses()} method.
 *
 * @author TheCSDev
 */
public @Virtual class IntegrityError extends WhatTheFuckError
{
	// ==================================================
	private static final long serialVersionUID = -8335931379266442615L;
	protected final Class<?>[] affectedClasses;
	// ==================================================
	public IntegrityError() { this(null, new Class<?>[0]); }
	public IntegrityError(Class<?>... affectedClasses) { this(null, affectedClasses); }
	public IntegrityError(String message, Class<?>... affectedClasses)
	{
		super(message);
		this.affectedClasses = affectedClasses;
	}
	// ==================================================
	/**
     * Returns the {@link Class}es that were affected by the integrity violation.
     *
     * @return An array of {@link Class}es that were affected, or null if no {@link Class}es were listed.
     */
	public final @Nullable Class<?>[] getAffectedClasses() { return this.affectedClasses; }
	// ==================================================
}
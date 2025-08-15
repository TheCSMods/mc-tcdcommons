package io.github.thecsdev.tcdcommons.api.util.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a method is "caller-sensitive".<br/>
 * A caller-sensitive method varies its behavior according to the {@link Class} of its immediate caller.
 * @apiNote The internal "CallerSensitive" {@link Annotation} is not accessible in the public JVM API,
 * and as such, this {@link Annotation} serves as a replacement.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface CallerSensitive {}
package io.github.thecsdev.tcdcommons.api.util.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation indicates that a member is intended to be overridable.
 * <p>
 * It is used to explicitly declare that a method or type is designed to
 * be overridden by subclasses.
 * <p>
 * This can be particularly useful in large
 * codebases or libraries where the intent of the designer needs to be
 * clearly communicated. It can also be useful in debugging scenarios where
 * it's important to understand the intended use of a method or type.
 *
 * @see Override
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Virtual {}
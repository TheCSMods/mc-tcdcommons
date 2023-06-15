package io.github.thecsdev.tcdcommons.api.config.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;

/**
 * Used to tell {@link AutoConfig} to use a specific
 * name to serialize certain properties.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface SerializedAs
{
	/**
	 * The name of the property that will be used
	 * to serialize the property.
	 */
	String value();
}
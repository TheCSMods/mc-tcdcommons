package io.github.thecsdev.tcdcommons.api.config.annotation;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to tell {@link AutoConfig} to use a specific
 * name to serialize certain properties.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface SerializedAs
{
	/**
	 * The name of the property that will be used
	 * to serialize the property.
	 */
	String value();
}
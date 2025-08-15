package io.github.thecsdev.tcdcommons.api.config.annotation;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to tell {@link AutoConfig} not to serialize a certain property.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface NonSerialized {}
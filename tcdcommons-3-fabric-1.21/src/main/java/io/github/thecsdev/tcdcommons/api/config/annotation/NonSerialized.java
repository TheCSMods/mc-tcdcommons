package io.github.thecsdev.tcdcommons.api.config.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;

/**
 * Used to tell {@link AutoConfig} not to serialize a certain property.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface NonSerialized {}
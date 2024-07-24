package io.github.thecsdev.tcdcommons.api.util.integrity;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for mods looking to test their own codebase's integrity.
 */
public final class IntegrityUtils
{
	// ==================================================
	private IntegrityUtils() {}
	// ==================================================
	/**
	 * Retrieves the name of the JAR {@link File} that contains a given {@link Class}.
	 * Will return {@code null} if a JAR file is not present or applicable in a given
	 * context, like for example if a {@link Class} is dynamically loaded from a
	 * directory or a non-JAR source.
	 * @param containingClass The {@link Class} whose JAR file name is to be retrieved.
	 */
	public static final @Nullable String getJarFileName(Class<?> containingClass)
		throws NullPointerException, SecurityException
	{
		//validate arguments
		Objects.requireNonNull(containingClass);
		
		//obtain the jar file path and ensure
		//it contains forward slashes and ends with ".jar"
		final var path = containingClass.getProtectionDomain()
				.getCodeSource().getLocation()
				.getPath().replace('\\', '/');
		if(!path.contains("/") || !path.toLowerCase(Locale.ENGLISH).endsWith(".jar"))
			return null;
		
		//get the last part after the last '/' instance, and return it
		return path.substring(path.lastIndexOf('/'));
	}
	// ==================================================
}
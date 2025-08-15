package io.github.thecsdev.tcdcommons.api.util.integrity;

import org.spongepowered.asm.mixin.Mixin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * <i>For when the mod has had enough, draws its weapons,
 * and starts defending its own integrity.</i>
 * 
 * @author TheCSDev
 * @apiNote Depends on {@link Mixin}x-s.
 */
public final class SelfDefense
{
	// ==================================================
	private SelfDefense() {}
	// ==================================================
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String MSG_INTEGRITY_VIOLATION_FOUND = "An integrity violation was found";
	//^ not allowed to reference any outside variables from here, for security reasons; not even the mod name;
	// ==================================================
	/**
	 * A static initializer appeared where it shouldn't be? Report it here.<p>
	 * <i>Careful not to unintentionally trigger unwanted code in the initializer.</i>
	 * @param issueSource The faulty {@link Class}.
	 * @throws ExceptionInInitializerError Always.
	 */
	public static void reportClassInitializer(Class<?> issueSource) throws ExceptionInInitializerError
	{
		//construct the integrity error message
		final var fullName = issueSource.getName();
		final var message = MSG_INTEGRITY_VIOLATION_FOUND + LINE_SEPARATOR +
				"The class '" + fullName + "' has a static constructor, which isn't allowed!" + LINE_SEPARATOR +
				"This could be a programming mistake, or it could be a malicious code injected by a virus." + LINE_SEPARATOR +
				"For security reasons, the program will now terminate. Please run a virus scan in the meantime." + LINE_SEPARATOR +
				LINE_SEPARATOR +
				"[BEGIN INTEGRITY DUMP]" + LINE_SEPARATOR +
				dumpClassInfo(issueSource) + LINE_SEPARATOR +
				"[END INTEGRITY DUMP]" + //because the stack trace continues; so as to not confuse the user;
				LINE_SEPARATOR;
		
		/* terminate the program
		 * ExceptionInInitializerError is thrown to:
		 * - report the full stack trace, including what invoked this method.
		 * - indicate this method was most likely invoked by a static initializer (Mixin-s)
		 * IntegrityError is used as the "cause" to:
		 * - indicate the cause was an integrity violation
		 * - report which classes were affected
		 */
		throw new ExceptionInInitializerError(new IntegrityError(message, issueSource));
	}
	// ==================================================
	/**
	 * Dumps a {@link Class}'s information in form of {@link String}
	 * that is then used for logging purposes. The {@link String} is
	 * intended to be "parseable" and "human-readable".
	 * @param clazz The target {@link Class}.
	 */
	public static String dumpClassInfo(Class<?> clazz)
	{
		//prepare
		final StringBuilder sb = new StringBuilder();

		//append class name
		sb.append("c: " + clazz.getName());

		// append superclass name
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null)
			sb.append(" extends " + superClass.getName());

		// append interfaces
		Class<?>[] interfaces = clazz.getInterfaces();
		Arrays.sort(interfaces, Comparator.comparing(Class::getName));
		if (interfaces.length > 0) {
			sb.append(" implements " + Arrays.stream(interfaces)
					.map(Class::getName)
					.collect(Collectors.joining(", ")));
		}
		
		//THERE MUST BE AT LEAST ONE LINE SEPARATOR INVOLVED
		sb.append(LINE_SEPARATOR);
		
		//append fields
		Field[] fields = clazz.getDeclaredFields();
		Arrays.sort(fields, Comparator.comparing(Field::getName));
		for (Field field : fields)
			sb.append("\tf: " + Modifier.toString(field.getModifiers()) + " " +
					field.getType().getName() + " " + field.getName() + LINE_SEPARATOR);
		
		//append methods
		Method[] methods = clazz.getDeclaredMethods();
		Arrays.sort(methods, Comparator.comparing(Method::getName));
		for (Method method : methods)
			sb.append("\tm: " + Modifier.toString(method.getModifiers()) +
					" " + method.getReturnType().getName() + " " +
					method.getName() + __dumpMethodParameters(method) + LINE_SEPARATOR);
		
		//trim the string builder because there
		//always will be at lease one line separator involved
		sb.setLength(sb.length() - LINE_SEPARATOR.length());
		
		//return result
		return sb.toString();
	}
	// --------------------------------------------------
	/**
	 * Internally used by {@link #dumpClassInfo(Class)} to
	 * obtain a {@link Method}'s list of parameters as a {@link String}.
	 * @param method The target {@link Method}.
	 */
	private static String __dumpMethodParameters(Method method)
	{
		Class<?>[] parameterTypes = method.getParameterTypes();
		return Arrays.stream(parameterTypes)
				.map(Class::getName)
				.collect(Collectors.joining(", ", "(", ")"));
	}
	// ==================================================
}
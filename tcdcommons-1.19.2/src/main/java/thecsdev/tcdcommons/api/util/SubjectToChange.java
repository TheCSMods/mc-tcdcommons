package thecsdev.tcdcommons.api.util;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation <b>indicates that a certain feature is subject
 * to change</b> in the future. You should be careful when interacting
 * with such features, as the future changes of their behavior
 * may end up breaking something. <b>Things annotated with this
 * may also be subject for removal.</b>
 */
@Inherited
@Retention(RetentionPolicy.SOURCE) //This policy may also be subject to change? Or maybe not...
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE, TYPE_PARAMETER, TYPE_USE, MODULE, RECORD_COMPONENT })
public @interface SubjectToChange
{
	/**
	 * Why is a given feature subject to change?
	 */
	public String value() default "Undefined";
	
	/**
	 * When will a given feature be changed?
	 */
	public String when() default "Undefined";
}
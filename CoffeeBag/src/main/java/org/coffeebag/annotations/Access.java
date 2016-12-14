/**
 * 
 */
package org.coffeebag.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks the accessibility of an item
 * @author Sam Crow
 */
@Documented
@Retention(SOURCE)
@Target({ TYPE, FIELD })
public @interface Access {
	/**
	 * The visibility of this item
	 */
	Visibility level();
	/**
	 * The scope in which this item should be visible, for Visibility
	 * values that require scopes
	 */
	String scope() default "";
}

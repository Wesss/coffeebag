package org.coffeebag.annotations;

/**
 * Available visibility levels for items
 * @author Sam Crow
 */
public enum Visibility {
	/**
	 * Visible to all code
	 */
	PUBLIC,
	/**
	 * Visible to code in a scope
	 */
	SCOPED,
	/**
	 * Visible to subclasses (applies only to fields, constructors, and methods)
	 */
	SUBCLASS,
	/**
	 * Least visibility
	 */
	PRIVATE
}

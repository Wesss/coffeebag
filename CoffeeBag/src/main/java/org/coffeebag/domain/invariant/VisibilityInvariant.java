package org.coffeebag.domain.invariant;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public interface VisibilityInvariant {

	/**
	 * @param fullPackageName a full package name (ex. "org.coffeebag.processor")
	 * @return true iff usage is permitted in the given package
	 */
	boolean isAllowedInPackage(String fullPackageName);

	/**
	 * @param qualifiedClassName a fully qualified class name (ex. "org.coffeebag.processor.CheckVisibility")
	 * @return true iff usage is permitted in the given package
	 */
	boolean isAllowedInClass(String qualifiedClassName);
}

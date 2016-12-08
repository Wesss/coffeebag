package org.coffeebag.domain;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class VisibilityInvariant {

	public VisibilityInvariant() {

	}

	/**
	 * @param fullPackageName a fully qualified package name (ex. "org.coffeebag.processor")
	 * @return true iff usage is permitted in the given package
	 */
	public boolean isAllowedInPackage(String fullPackageName) {
		return true; //TODO
	}

	/**
	 * @param fullClassName a fully qualified package name (ex. "org.coffeebag.processor.CheckVisibility")
	 * @return true iff usage is permitted in the given package
	 */
	public boolean isAllowedInClass(String fullClassName) {
		return true; //TODO
	}
}

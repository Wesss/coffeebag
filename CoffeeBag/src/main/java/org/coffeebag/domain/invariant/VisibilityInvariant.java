package org.coffeebag.domain.invariant;

import javax.lang.model.element.TypeElement;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public interface VisibilityInvariant {

	/**
	 * @param element the TypeElement representing the class that is using
	 *                the member with this invariant
	 * @return true iff the given class is allowed usage of the member associated
	 * with this invariant
	 */
	boolean isUsageAllowedIn(TypeElement element);
}

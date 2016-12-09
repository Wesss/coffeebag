package org.coffeebag.domain.invariant;

import org.coffeebag.annotations.Visibility;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class PublicVisibilityInvariant implements VisibilityInvariant {

	/**
	 * all packages allowed
	 *
	 * @inheritDoc
	 */
	@Override
	public boolean isAllowedInPackage(String fullPackageName) {
		return true;
	}

	/**
	 * all classes allowed
	 *
	 * @inheritDoc
	 */
	@Override
	public boolean isAllowedInClass(String qualifiedClassName) {
		return true;
	}
}

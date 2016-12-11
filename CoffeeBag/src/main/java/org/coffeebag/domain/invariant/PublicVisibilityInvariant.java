package org.coffeebag.domain.invariant;

import javax.lang.model.element.TypeElement;

/**
 * Allows an element to be accessed from everywhere
 */
class PublicVisibilityInvariant implements VisibilityInvariant {

	/**
	 * Allow everything
	 * @inheritdoc
	 */
	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		return true;
	}
	
	@Override
	public String toString() {
		return "Allowed everywhere";
	}
}

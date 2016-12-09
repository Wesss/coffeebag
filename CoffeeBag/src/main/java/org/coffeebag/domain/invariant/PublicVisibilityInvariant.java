package org.coffeebag.domain.invariant;

import javax.lang.model.element.TypeElement;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class PublicVisibilityInvariant implements VisibilityInvariant {

	/**
	 * Allow everything
	 * @inheritdoc
	 */
	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		return true;
	}
}

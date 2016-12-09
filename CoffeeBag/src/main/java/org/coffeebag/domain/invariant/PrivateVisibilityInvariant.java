package org.coffeebag.domain.invariant;

import javax.lang.model.element.TypeElement;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class PrivateVisibilityInvariant implements VisibilityInvariant {

	private String qualifiedClassName;

	public PrivateVisibilityInvariant(String qualifiedClassName) {
		this.qualifiedClassName = qualifiedClassName;
	}

	/**
	 * Allow only usage if member is using itself
	 * @inheritdoc
	 */
	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		return element.getQualifiedName().toString().equals(qualifiedClassName);
	}
}

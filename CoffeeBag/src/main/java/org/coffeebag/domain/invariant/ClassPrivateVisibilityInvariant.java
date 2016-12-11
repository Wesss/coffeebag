package org.coffeebag.domain.invariant;

import javax.lang.model.element.TypeElement;

/**
 * Restricts an element to use in one class
 */
class ClassPrivateVisibilityInvariant implements VisibilityInvariant {

	/**
	 * The canonical name where this element may be used
	 */
	private String qualifiedClassName;

	public ClassPrivateVisibilityInvariant(String qualifiedClassName) {
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

	@Override
	public String toString() {
		return "Allowed in class " + qualifiedClassName;
	}
}

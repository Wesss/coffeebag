package org.coffeebag.domain.invariant;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class SubclassVisibilityInvariant implements VisibilityInvariant {

	private Types typeUtils;
	private TypeElement classElement;

	public SubclassVisibilityInvariant(Types typeUtils, TypeElement classElement) {
		this.typeUtils = typeUtils;
		this.classElement = classElement;
	}

	/**
	 * Allow only usage if member is using itself
	 * @inheritdoc
	 */
	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		return typeUtils.isSubtype(element.asType(), classElement.asType());
	}
}

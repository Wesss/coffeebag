package org.coffeebag.domain.invariant;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class PrivateVisibilityInvariant implements VisibilityInvariant {

	private CharSequence fullPackageName, qualifiedClassName;

	public PrivateVisibilityInvariant(String fullPackageName, String qualifiedClassName) {
		this.fullPackageName = fullPackageName;
		this.qualifiedClassName = qualifiedClassName;
	}

	/**
	 * only the package declared in allowed
	 *
	 * @inheritDoc
	 */
	@Override
	public boolean isAllowedInPackage(String fullPackageName) {
		return this.fullPackageName.equals(fullPackageName);
	}

	/**
	 * only the class declared in allowed
	 *
	 * @inheritDoc
	 */
	@Override
	public boolean isAllowedInClass(String qualifiedClassName) {
		return this.qualifiedClassName.equals(qualifiedClassName);
	}
}

package org.coffeebag.domain.invariant;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Allows access from a package and its subpackages
 */
class SubpackageVisibilityInvariant extends PackageVisibilityInvariant {

	public SubpackageVisibilityInvariant(String packageName, Elements elements) {
		super(packageName, elements);
	}

	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		final PackageElement elementPackage = elements.getPackageOf(element);
		return elementPackage.getQualifiedName().toString().startsWith(packageName);
	}
	
	@Override
	public String toString() {
		return "Allowed in package " + packageName + " and subpackages";
	}

}

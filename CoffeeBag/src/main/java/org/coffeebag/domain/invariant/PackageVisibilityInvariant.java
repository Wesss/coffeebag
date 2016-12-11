package org.coffeebag.domain.invariant;

import java.util.Objects;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * A visibility invariant that allows access from one package (not including its subpackages)
 */
public class PackageVisibilityInvariant implements VisibilityInvariant {
	
	/**
	 * The name of the package from which elements can be accessed
	 */
	protected final String packageName;
	
	/**
	 * The element utility
	 */
	protected final Elements elements;
	
	
	/**
	 * Creates a new package visibility invariant
	 * @param packageName the package in which the element should be accessible
	 */
	public PackageVisibilityInvariant(String packageName, Elements elements) {
		Objects.requireNonNull(packageName);
		Objects.requireNonNull(elements);
		this.packageName = packageName;
		this.elements = elements;
	}



	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		final PackageElement elementPackage = elements.getPackageOf(element);
		return packageName.equals(elementPackage.getQualifiedName().toString());
	}
	

}

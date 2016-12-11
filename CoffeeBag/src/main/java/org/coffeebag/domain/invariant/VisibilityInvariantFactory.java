package org.coffeebag.domain.invariant;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.coffeebag.annotations.Access;
import org.coffeebag.log.Log;

public class VisibilityInvariantFactory {

	private static String TAG = VisibilityInvariantFactory.class.getSimpleName();

	// TODO add testmode to this class to enable generating mock subclasses?

	/**
	 * @requires element has an Access annotation
	 */
	public static VisibilityInvariant getInvariant(TypeElement element, ProcessingEnvironment env) {
		
		final ElementKind kind = element.getKind();
		Access annotation = element.getAnnotation(Access.class);
		if (annotation == null) {
			throw new IllegalArgumentException("The provided element does not have an Access annotation");
		}
		switch (annotation.level()) {
			case PUBLIC:
				return new PublicVisibilityInvariant();
			case PRIVATE:
				if (kind == ElementKind.CLASS || kind == ElementKind.ENUM || kind == ElementKind.INTERFACE) {
					// Private classes are accessible from their package only
					final PackageElement elementPackage = env.getElementUtils().getPackageOf(element);
					return new PackageVisibilityInvariant(elementPackage.getQualifiedName().toString(), env.getElementUtils());
				} else {
					// Other elements are only accessible from the class in which they are declared
					return new ClassPrivateVisibilityInvariant(element.getQualifiedName().toString());
				}
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());
				return null;
		}
	}
}

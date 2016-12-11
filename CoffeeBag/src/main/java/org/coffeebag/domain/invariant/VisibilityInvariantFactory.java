package org.coffeebag.domain.invariant;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.AccessElement;
import org.coffeebag.log.Log;

public class VisibilityInvariantFactory {

	private static String TAG = VisibilityInvariantFactory.class.getSimpleName();

	/**
	 * Creates a visibility invariant for an annotated type, executable, or field
	 * @requires element has an Access annotation, and element.getKind() is ElementKind.CLASS, ElementKind.ENUM,
	 * 		ElementKind.INTERFACE, ElementKind.CONSTRUCTOR, ElementKind.METHOD, or ElementKind.FIELD
	 */
	public static VisibilityInvariant getInvariant(AccessElement element, ProcessingEnvironment env) {
		
		final AccessElement.Kind kind = element.getKind();
		final Access annotation = element.getAccessAnnotation();
		if (annotation == null) {
			throw new IllegalArgumentException("The provided element does not have an Access annotation");
		}
		switch (annotation.level()) {
			case PUBLIC:
				return new PublicVisibilityInvariant();
			case PRIVATE:
				if (kind == AccessElement.Kind.TYPE) {
					// Private classes are accessible from their package only
					final PackageElement elementPackage = env.getElementUtils().getPackageOf(element.getElement());
					return new PackageVisibilityInvariant(elementPackage.getQualifiedName().toString(), env.getElementUtils());
				} else {
					// Other elements are only accessible from the class in which they are declared
					final TypeElement enclosing = element.getEnclosingType();
					if (enclosing == null) {
						throw new IllegalStateException("Failed to find an enclosing type for " + element.getElement().getSimpleName());
					}
					return new ClassPrivateVisibilityInvariant(enclosing.getQualifiedName().toString());
				}
			case SCOPED:
				final String scope = annotation.scope();
				final Messager messager = env.getMessager();
				// Check not empty
				if (scope.isEmpty()) {
					messager.printMessage(Kind.ERROR, "An element with SCOPED visibility must specify a non-empty scope", element.getElement());
					return null;
				}
				// Check that scope is a valid package
				final PackageElement scopePackage = env.getElementUtils().getPackageElement(scope);
				if (scopePackage == null) {
					messager.printMessage(Kind.ERROR, "The package \"" + scope + "\" could not be resolved", element.getElement());
					return null;
				}
				
				return new SubpackageVisibilityInvariant(scopePackage.getQualifiedName().toString(), env.getElementUtils());
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());
				return null;
		}
	}
	
	
}

package org.coffeebag.domain.invariant;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.coffeebag.annotations.Access;
import org.coffeebag.log.Log;

public class VisibilityInvariantFactory {

	private static String TAG = VisibilityInvariantFactory.class.getSimpleName();

	/**
	 * Creates a visibility invariant for an annotated type or field
	 */
	public static VisibilityInvariant getInvariant(Element element, ProcessingEnvironment env) {
		
		final Access annotation = element.getAnnotation(Access.class);
		if (annotation == null) {
			throw new IllegalArgumentException("The provided element does not have an Access annotation");
		}
		switch (annotation.level()) {
			case PUBLIC:
				return new PublicVisibilityInvariant();
			case PRIVATE:
				// Elements (including classes) are only visible from the classes where they are declared
				final TypeElement enclosing = getEnclosingType(element);
				return new ClassPrivateVisibilityInvariant(enclosing.getQualifiedName().toString());
			case SCOPED:
				final String scope = annotation.scope();
				final Messager messager = env.getMessager();
				// Check not empty
				if (scope.isEmpty()) {
					messager.printMessage(Kind.ERROR, "An element with SCOPED visibility must specify a non-empty scope", element);
					return null;
				}
				// Check that scope is a valid package
				final PackageElement scopePackage = env.getElementUtils().getPackageElement(scope);
				if (scopePackage == null) {
					messager.printMessage(Kind.ERROR, "The package \"" + scope + "\" could not be resolved", element);
					return null;
				}
				
				return new SubpackageVisibilityInvariant(scopePackage.getQualifiedName().toString(), env.getElementUtils());
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());
				return null;
		}
	}
	
	private static TypeElement getEnclosingType(Element element) {
		if (element.getKind().isClass() || element.getKind().isInterface()) {
			return (TypeElement) element;
		} else {
			return (TypeElement) element.getEnclosingElement();
		}
	}
}
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

	// TODO add testmode to this class to enable generating mock subclasses?

	/**
	 * Creates a visibility invariant for an annotated type, executable, or field
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
				// Private classes can only be used by themselves
				// Not very useful, but consistent
				final TypeElement typeOrEnclosing = element.asTypeElement();
				if (typeOrEnclosing == null) {
					throw new IllegalStateException("Failed to find an enclosing type or type for " + element.getElement().getSimpleName());
				}
				return new ClassPrivateVisibilityInvariant(typeOrEnclosing.getQualifiedName().toString());
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
			case SUBCLASS:
				final TypeElement enclosing = element.asTypeElement();
				if (enclosing == null) {
					throw new IllegalStateException("Failed to find an enclosing type or type for " + element.getElement().getSimpleName());
				}
				return new SubclassVisibilityInvariant(env.getTypeUtils(), enclosing);
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());
				return null;
		}
	}
	
	
}

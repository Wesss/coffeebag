package org.coffeebag.domain.invariant;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.AccessElement;
import org.coffeebag.log.Log;

public class VisibilityInvariantFactory {

	private static String TAG = VisibilityInvariantFactory.class.getSimpleName();

	/**
	 * Creates a visibility invariant for an annotated type, executable, or field
	 */
	public static VisibilityInvariant getInvariant(AccessElement element, ProcessingEnvironment env) {
		// TODO: Locations of errors
		final Access annotation = getAccessAnnotation(element, env);
		if (annotation == null) {
			throw new IllegalArgumentException("The provided element does not have an Access annotation");
		}
		switch (annotation.level()) {
			case PUBLIC:
				return new PublicVisibilityInvariant();
			case PRIVATE:
				// Private classes can only be used by themselves
				// Not very useful, but consistent
				final TypeElement typeOrEnclosing = env.getElementUtils().getTypeElement(element.getEnclosingType());
				if (typeOrEnclosing == null) {
					throw new IllegalStateException("Failed to find an enclosing type or type for " + element);
				}
				return new ClassPrivateVisibilityInvariant(typeOrEnclosing.getQualifiedName().toString());
			case SCOPED:
				final String scope = annotation.scope();
				final Messager messager = env.getMessager();
				// Check not empty
				if (scope.isEmpty()) {
					messager.printMessage(Kind.ERROR, "An element with SCOPED visibility must specify a non-empty scope");
					return null;
				}
				// Check that scope is a valid package
				final PackageElement scopePackage = env.getElementUtils().getPackageElement(scope);
				if (scopePackage == null) {
					messager.printMessage(Kind.ERROR, "The package \"" + scope + "\" could not be resolved");
					return null;
				}
				
				return new SubpackageVisibilityInvariant(scopePackage.getQualifiedName().toString(), env.getElementUtils());
			case SUBCLASS:
				final TypeElement enclosing = env.getElementUtils().getTypeElement(element.getEnclosingType());
				if (enclosing == null) {
					throw new IllegalStateException("Failed to find an enclosing type or type for " + element);
				}
				return new SubclassVisibilityInvariant(env.getTypeUtils(), enclosing);
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());
				return null;
		}
	}
	
	/**
	 * Gets the {@link Access} annotation of an element
	 * @param element the element to get an annotation from
	 * @return the Access annotation, or null if none is found
	 */
	private static Access getAccessAnnotation(AccessElement element, ProcessingEnvironment env) {
		final TypeElement type = env.getElementUtils().getTypeElement(element.getEnclosingType());
		if (type == null) {
			throw new NullPointerException("No TypeElement found for type " + element);
		}
		if (element.getKind() == AccessElement.Kind.TYPE) {
			return type.getAnnotation(Access.class);
		} else {
			// Field
			for (Element innerElement : type.getEnclosedElements()) {
				if (innerElement.getKind() == ElementKind.FIELD && innerElement.getSimpleName().equals(element.getFieldName())) {
					return ((VariableElement) innerElement).getAnnotation(Access.class);
				}
			}
			throw new IllegalStateException("Field " + element.getFieldName() + " not found in type " + element.getEnclosingType());
		}
	}
}

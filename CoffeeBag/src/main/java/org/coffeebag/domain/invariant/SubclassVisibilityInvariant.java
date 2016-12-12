package org.coffeebag.domain.invariant;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Represents the packages/classes a member is allowed to be accessed from
 */
public class SubclassVisibilityInvariant implements VisibilityInvariant {
	@SuppressWarnings("unused")
	private static final String TAG = SubclassVisibilityInvariant.class.getSimpleName();

	private ProcessingEnvironment env;
	/**
	 * The class whose subclasses may access this element
	 * 
	 * Access allowed if element is a subtype of classElement
	 */
	private TypeElement classElement;

	public SubclassVisibilityInvariant(ProcessingEnvironment env, TypeElement classElement) {
		this.env = env;
		this.classElement = classElement;
	}

	/**
	 * Allow usage if member a subclass of annotated member's class
	 * @inheritdoc
	 */
	@Override
	public boolean isUsageAllowedIn(TypeElement element) {
		// Check subclassing manually
		if (env.getTypeUtils().isSameType(element.asType(), classElement.asType())) {
			return true;
		}
		TypeMirror superclass = (DeclaredType) element.getSuperclass();
		while (superclass.getKind() == TypeKind.DECLARED) {
			if (superclass.toString().equals(classElement.getQualifiedName().toString())) {
				return true;
			}
			final List<? extends TypeMirror> supertypes = env.getTypeUtils().directSupertypes(superclass);
			if (!supertypes.isEmpty()) {
				// Interfaces are last, so first should be a class
				superclass = supertypes.get(0);
			} else {
				break;
			}
		}
		// Nothing found
		return false;
	}
	
	@Override
	public String toString() {
		return "Allowed in class " + classElement + " and subclasses";
	}
}

package org.coffeebag.domain;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This represents all of CoffeeBag's visibility invariants
 */
public class VisibilityInvariants {
	// classes and interfaces (including enums)
	private Set<TypeElement> accessAnnotatedTypeElements;

	// fields TODO find more specific type that represents fields?
	private Set<VariableElement> accessAnnotatedFieldElements;

	// methods, constructors
	private Set<ExecutableElement> accessAnnotatedExecutableElements;

	public VisibilityInvariants() {
		accessAnnotatedTypeElements = new HashSet<>();
		accessAnnotatedFieldElements = new HashSet<>();
		accessAnnotatedExecutableElements = new HashSet<>();
	}

	public Set<TypeElement> getTypeElements() {
		return Collections.unmodifiableSet(accessAnnotatedTypeElements);
	}

	public Set<VariableElement> getFieldElements() {
		return Collections.unmodifiableSet(accessAnnotatedFieldElements);
	}

	public Set<ExecutableElement> getExcecutableElements() {
		return Collections.unmodifiableSet(accessAnnotatedExecutableElements);
	}
}

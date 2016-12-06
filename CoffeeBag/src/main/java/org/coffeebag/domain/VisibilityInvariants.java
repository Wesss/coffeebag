package org.coffeebag.domain;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a building set of all of CoffeeBag's visibility invariants
 */
public class VisibilityInvariants {
	// classes and interfaces (including enums)
	private Set<TypeElement> typeElements;

	// fields
	private Set<VariableElement> fieldElements;

	// methods, constructors
	private Set<ExecutableElement> executableElements;

	public VisibilityInvariants() {
		typeElements = new HashSet<>();
		fieldElements = new HashSet<>();
		executableElements = new HashSet<>();
	}

	public void addTypeElement(TypeElement element) {
		typeElements.add(element);
	}

	public void addFieldElement(VariableElement element) {
		fieldElements.add(element);
	}

	public void addExecutableElement(ExecutableElement element) {
		executableElements.add(element);
	}

	public void addAll(VisibilityInvariants other) {
		typeElements.addAll(other.getTypeElements());
		fieldElements.addAll(other.getFieldElements());
		executableElements.addAll(other.getExcecutableElements());
	}

	public Set<TypeElement> getTypeElements() {
		return Collections.unmodifiableSet(typeElements);
	}

	public Set<VariableElement> getFieldElements() {
		return Collections.unmodifiableSet(fieldElements);
	}

	public Set<ExecutableElement> getExcecutableElements() {
		return Collections.unmodifiableSet(executableElements);
	}

	public VisibilityInvariants unmodifiable() {
		VisibilityInvariants unmodifiable = new VisibilityInvariants();
		unmodifiable.addAll(this);
		unmodifiable.typeElements = Collections.unmodifiableSet(typeElements);
		unmodifiable.fieldElements = Collections.unmodifiableSet(fieldElements);
		unmodifiable.executableElements = Collections.unmodifiableSet(executableElements);
		return unmodifiable;
	}
}

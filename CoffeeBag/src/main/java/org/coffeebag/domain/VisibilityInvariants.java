package org.coffeebag.domain;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a building set of all of CoffeeBag's visibility invariants
 */
public class VisibilityInvariants {
	// classes
	private Set<TreePath> classTreePaths;

	// fields
	private Set<TreePath> fieldTreePaths;

	// methods
	private Set<TreePath> methodTreePaths;

	//TODO enums, interfaces, constructors, ??

	public VisibilityInvariants() {
		classTreePaths = new HashSet<>();
		fieldTreePaths = new HashSet<>();
		methodTreePaths = new HashSet<>();
	}

	public void addClassInvariant(TreePath element) {
		classTreePaths.add(element);
	}

	public void addFieldInvariant(TreePath element) {
		fieldTreePaths.add(element);
	}

	public void addExecutableInvariant(TreePath element) {
		methodTreePaths.add(element);
	}

	public void addAll(VisibilityInvariants other) {
		classTreePaths.addAll(other.getClassTreePaths());
		fieldTreePaths.addAll(other.getFieldTreePaths());
		methodTreePaths.addAll(other.getExcecutableElements());
	}

	public Set<TreePath> getClassTreePaths() {
		return Collections.unmodifiableSet(classTreePaths);
	}

	public Set<TreePath> getFieldTreePaths() {
		return Collections.unmodifiableSet(fieldTreePaths);
	}

	public Set<TreePath> getExcecutableElements() {
		return Collections.unmodifiableSet(methodTreePaths);
	}

	public VisibilityInvariants unmodifiable() {
		VisibilityInvariants unmodifiable = new VisibilityInvariants();
		unmodifiable.addAll(this);
		unmodifiable.classTreePaths = Collections.unmodifiableSet(classTreePaths);
		unmodifiable.fieldTreePaths = Collections.unmodifiableSet(fieldTreePaths);
		unmodifiable.methodTreePaths = Collections.unmodifiableSet(methodTreePaths);
		return unmodifiable;
	}
}

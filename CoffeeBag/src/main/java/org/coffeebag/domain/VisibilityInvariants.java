package org.coffeebag.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a building set of all of CoffeeBag's visibility invariants
 */
public class VisibilityInvariants {
	// classes
	private Set<VisibilityInvariant> classTreePaths;

	// fields
//	private Set<TreePath> fieldTreePaths;
//
//	// methods
//	private Set<TreePath> methodTreePaths;

	//TODO enums, interfaces, constructors, ??

	public VisibilityInvariants() {
		classTreePaths = new HashSet<>();
//		fieldTreePaths = new HashSet<>();
//		methodTreePaths = new HashSet<>();
	}

	public void addClassInvariant(VisibilityInvariant classTree) {
		classTreePaths.add(classTree);
	}

//	public void addFieldInvariant(TreePath fieldTree) {
//		fieldTreePaths.add(fieldTree);
//	}
//
//	public void addMethodInvariant(TreePath methodTree) {
//		methodTreePaths.add(methodTree);
//	}

	public void addAll(VisibilityInvariants other) {
		classTreePaths.addAll(other.getClassInvariants());
//		fieldTreePaths.addAll(other.getFieldTreePaths());
//		methodTreePaths.addAll(other.getExcecutableElements());
	}

	public Set<VisibilityInvariant> getClassInvariants() {
		return Collections.unmodifiableSet(classTreePaths);
	}

//	public Set<TreePath> getFieldTreePaths() {
//		return Collections.unmodifiableSet(fieldTreePaths);
//	}
//
//	public Set<TreePath> getExcecutableElements() {
//		return Collections.unmodifiableSet(methodTreePaths);
//	}

	public VisibilityInvariants unmodifiable() {
		VisibilityInvariants unmodifiable = new VisibilityInvariants();
		unmodifiable.addAll(this);
		unmodifiable.classTreePaths = Collections.unmodifiableSet(classTreePaths);
//		unmodifiable.fieldTreePaths = Collections.unmodifiableSet(fieldTreePaths);
//		unmodifiable.methodTreePaths = Collections.unmodifiableSet(methodTreePaths);
		return unmodifiable;
	}
}

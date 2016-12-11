package org.coffeebag.domain;

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.coffeebag.annotations.Access;
import org.coffeebag.log.Log;

/**
 * An element that has an {@link Access} annotation
 * <p/>
 * Three types of elements can have access annotations:
 * <ul>
 * <li>Types, with element kind {@link ElementKind#CLASS}, {@link ElementKind#INTERFACE}, or {@link ElementKind#ENUM}</li>
 * <li>Executable elements (including methods, class initializer blocks, and static initializer blocks) with element
 * kind {@link ElementKind#METHOD}, {@link ElementKind#CONSTRUCTOR}, {@link ElementKind#INSTANCE_INIT}, or {@link ElementKind#STATIC_INIT}</li>
 * <li>Fields, with element kind {@link ElementKind#FIELD}</li>
 * </ul>
 * 
 * This class has {@link #equals(Object)} and {@link #hashCode()} implementations that allow objects from different
 * sources to be compared correctly, unlike the implementations on the {@link Element} class.
 */
public class AccessElement {
	private static final String TAG = AccessElement.class.getSimpleName();
	
	/**
	 * Simplified element kinds
	 */
	public enum Kind {
		/**
		 * A class, interface, or enumeration
		 */
		TYPE,
		/**
		 * A method, instance initializer, or static initializer
		 */
		EXECUTABLE,
		/**
		 * A field
		 */
		FIELD,
	}
	
	/**
	 * The inner element
	 * 
	 * Invariant: This is not null
	 */
	private final Element element;
	
	
	

	/**
	 * Creates a new access element
	 * @param element the element to wrap
	 * @throws NullPointerException if element is null
	 * @throws IllegalArgumentException if element does not have one of the supported kinds or does not have an Access
	 * annotation
	 */
	public AccessElement(Element element) {
		Objects.requireNonNull(element);
		// Check kind
		simplifyKind(element.getKind());
		if (element.getAnnotation(Access.class) == null) {
			throw new IllegalArgumentException("Element has no Access annotation");
		}
		this.element = element;
	}


	/**
	 * Returns the simplified kind of this element
	 * @return the kind of this element
	 */
	public Kind getKind() {
		return simplifyKind(element.getKind());
	}
	
	/**
	 * Returns the wrapped element
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}
	
	/**
	 * If this element is a type, returns the element. Otherwise, returns the innermost enclosing type of this element
	 * @return the type, or null if none could be foun
	 */
	public TypeElement asTypeElement() {
		if (getKind() == Kind.TYPE) {
			return (TypeElement) element;
		} else {
			return getEnclosingType();
		}
	}

	/**
	 * Returns the access annotation of this element
	 * @return the access annotation
	 */
	public Access getAccessAnnotation() {
		return element.getAnnotation(Access.class);
	}
	
	/**
	 * Finds the innermost type that contains this element
	 * @return the innermost enclosing type, or null if this element has kind {@link Kind#TYPE}
	 */
	public TypeElement getEnclosingType() {
		Log.d(TAG, "Getting enclosing type for " + element);
		if (getKind() == Kind.TYPE) {
			return null;
		}
		Element enclosing;
		do {
			enclosing = element.getEnclosingElement();
			Log.d(TAG, "One enclosing type: " + enclosing);
			if (enclosing == null) {
				// Not found
				return null;
			}
		} while (enclosing.getKind() != ElementKind.CLASS
				&& enclosing.getKind() != ElementKind.INTERFACE
				&& enclosing.getKind() != ElementKind.ENUM);
		Log.d(TAG, "Found enclosing element " + enclosing + " with kind " + enclosing.getKind());
		final Class<? extends Element> enclosingClass = enclosing.getClass();
		for (Class<?> implInterface : enclosingClass.getInterfaces()) {
			Log.d(TAG, "Implements interface " + implInterface.getCanonicalName());
		}
		return (TypeElement) enclosing;
	}

	@Override
	public String toString() {
		return element.toString();
	}
	
	@Override
	public int hashCode() {
		System.out.println("[AE] " + element + " compared as " + getNameForComparision());
		return getNameForComparision().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessElement other = (AccessElement) obj;

		System.out.println("[AE] " + element + " compared as " + getNameForComparision());
		return getNameForComparision().equals(other.getNameForComparision());
	}
	
	private static Kind simplifyKind(ElementKind kind) {
		switch (kind) {
		// Intentional fallthrough
		case CLASS:
		case ENUM:
		case INTERFACE:
			return Kind.TYPE;
		case METHOD:
		case CONSTRUCTOR:
		case INSTANCE_INIT:
		case STATIC_INIT:
			return Kind.EXECUTABLE;
		case FIELD:
			return Kind.FIELD;
		default:
			throw new IllegalArgumentException("Invalid inner element kind " + kind);
		}
	}
	
	private String getNameForComparision() {
		if (getKind() == Kind.TYPE) {
			// Canonical type name
			return ((TypeElement) element).getQualifiedName().toString();
		} else {
			// Canonical type name . field or method name
			final TypeElement enclosing = getEnclosingType();
			if (enclosing != null) {
				return enclosing.getQualifiedName() + "." + element.getSimpleName();
			} else {
				return "<unknown>." + element.getSimpleName();
			}
		}
	}
	
}

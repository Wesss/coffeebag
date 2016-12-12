package org.coffeebag.domain;

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.coffeebag.annotations.Access;

/**
 * An element that has an {@link Access} annotation
 * <p/>
 * Three types of elements can have access annotations:
 * <ul>
 * <li>Types, with element kind {@link ElementKind#CLASS}, {@link ElementKind#INTERFACE}, or {@link ElementKind#ENUM}</li>
 * <li>Fields, with element kind {@link ElementKind#FIELD}</li>
 * </ul>
 * 
 * This class has {@link #equals(Object)} and {@link #hashCode()} implementations that allow objects from different
 * sources to be compared correctly, unlike the implementations on the {@link Element} class.
 */
public final class AccessElement {
	@SuppressWarnings("unused")
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
		 * A field
		 */
		FIELD,
	}
	
	/**
	 * The canonical name of this type (class/enum/interface), or the type containing this field
	 */
	private final String canonicalTypeName;
	
	/**
	 * The name of this field, or null if this is a type
	 */
	private final String fieldName;
	

	/**
	 * Creates a new access element
	 * @param element the element to wrap
	 * @throws NullPointerException if element is null
	 * @throws IllegalArgumentException if element does not have one of the supported kinds
	 */
	public AccessElement(Element element) {
		Objects.requireNonNull(element);
		// Check kind
		switch (simplifyKind(element.getKind())) {
		case FIELD:
			final TypeElement enclosing = getEnclosingType(element);
			canonicalTypeName = enclosing.getQualifiedName().toString();
			fieldName = element.getSimpleName().toString();
			break;
		case TYPE:
			canonicalTypeName = ((TypeElement) element).getQualifiedName().toString();
			fieldName = null;
			break;
		default:
			throw new UnsupportedOperationException("Invalid access element kind");
		}
		
	}


	/**
	 * Returns the simplified kind of this element
	 * @return the kind of this element
	 */
	public Kind getKind() {
		if (fieldName == null) {
			return Kind.TYPE;
		} else {
			return Kind.FIELD;
		}
	}
	
	
	/**
	 * Finds the innermost type that contains this element
	 * @return the canonical name of the innermost enclosing type, or null if this element has kind {@link Kind#TYPE}
	 */
	public String getEnclosingType() {
		return canonicalTypeName;
	}
	
	public String getFieldName() {
		return fieldName;
	}


	private static TypeElement getEnclosingType(Element element) {
		Element enclosing;
		do {
			enclosing = element.getEnclosingElement();
			if (enclosing == null) {
				// Not found
				return null;
			}
		} while (enclosing.getKind() != ElementKind.CLASS
				&& enclosing.getKind() != ElementKind.INTERFACE
				&& enclosing.getKind() != ElementKind.ENUM);
		return (TypeElement) enclosing;
	}

	@Override
	public String toString() {
		if (fieldName != null) {
			return canonicalTypeName + "." + fieldName;
		} else {
			return canonicalTypeName;
		}
	}
	
	private static Kind simplifyKind(ElementKind kind) {
		switch (kind) {
		// Intentional fallthrough
		case CLASS:
		case ENUM:
		case INTERFACE:
		case ANNOTATION_TYPE:
			return Kind.TYPE;
		case FIELD:
			return Kind.FIELD;
		default:
			throw new IllegalArgumentException("Invalid inner element kind " + kind);
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((canonicalTypeName == null) ? 0 : canonicalTypeName.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AccessElement other = (AccessElement) obj;
		if (canonicalTypeName == null) {
			if (other.canonicalTypeName != null) {
				return false;
			}
		} else if (!canonicalTypeName.equals(other.canonicalTypeName)) {
			return false;
		}
		if (fieldName == null) {
			if (other.fieldName != null) {
				return false;
			}
		} else if (!fieldName.equals(other.fieldName)) {
			return false;
		}
		return true;
	}
	
	
}

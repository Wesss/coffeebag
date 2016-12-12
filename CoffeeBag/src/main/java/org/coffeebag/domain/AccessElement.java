package org.coffeebag.domain;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.coffeebag.annotations.Access;

/**
 * An element that has an {@link Access} annotation. This may be a class or a field.
 */
public class AccessElement {
	@SuppressWarnings("unused")
	private static final String TAG = AccessElement.class.getSimpleName();
	
	/**
	 * The canonical name of the type of this element, or the class that encloses this element if this is a field
	 */
	private final String typeName;
	
	/**
	 * The field name if this is a field, or null if this is a type
	 * 
	 * This is not used in {@link #equals(Object)} and {@link #hashCode()}
	 */
	private final String fieldName;
	
	/**
	 * A TypeElement that represents this element or its enclosing type, or null if none is available
	 */
	private final TypeElement typeElement;
	/**
	 * A FieldElement that represents this field, or null if none is available or if this is a type
	 * 
	 * This is not used in {@link #equals(Object)} and {@link #hashCode()}
	 */
	private final VariableElement fieldElement;
	
	private AccessElement(String typeName, TypeElement typeElement, String fieldName, VariableElement fieldElement) {
		this.typeName = typeName;
		this.fieldName = fieldName;
		this.typeElement = typeElement;
		this.fieldElement = fieldElement;
	}

	/**
	 * Creates an element from a TypeElement
	 * @param typeElement the type element
	 * @return an AccessElement representing the provided type
	 */
	public static AccessElement type(TypeElement typeElement) {
		return new AccessElement(typeElement.getQualifiedName().toString(), typeElement, null, null);
	}
	public static AccessElement type(String canonicalName) {
		return new AccessElement(canonicalName, null, null, null);
	}
	
	public static AccessElement field(VariableElement field) {
		if (field.getKind() != ElementKind.FIELD) {
			throw new IllegalArgumentException("Field VariableElement does not have kind FIELD");
		}
		final TypeElement enclosing = (TypeElement) field.getEnclosingElement();
		return new AccessElement(enclosing.getQualifiedName().toString(), enclosing, field.getSimpleName().toString(), field);
	}
	
	/**
	 * Creates an element from a class name and a field name
	 * @param enclosingCanonicalName the canonical name of the class that contains this field
	 * @param fieldName the name of the field
	 * @return an AccessType
	 */
	public static AccessElement field(String enclosingCanonicalName, String fieldName) {
		return new AccessElement(enclosingCanonicalName, null, fieldName, null);
	}

	public String getTypeName() {
		return typeName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public TypeElement getTypeElement() {
		return typeElement;
	}

	public VariableElement getFieldElement() {
		return fieldElement;
	}
	
	
	@Override
	public String toString() {
		if (fieldName != null) {
			return typeName + '.' + fieldName;
		} else {
			return typeName;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
		if (fieldName == null) {
			if (other.fieldName != null) {
				return false;
			}
		} else if (!fieldName.equals(other.fieldName)) {
			return false;
		}
		if (typeName == null) {
			if (other.typeName != null) {
				return false;
			}
		} else if (!typeName.equals(other.typeName)) {
			return false;
		}
		return true;
	}
}

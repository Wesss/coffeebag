package org.coffeebag.processor.references;

/**
 * Imports that may be found in a source file
 */
public class Import {
	
	/**
	 * Import types
	 */
	public enum ImportType {
		/**
		 * An import containing the fully-qualified name of a type
		 */
		Type,
		/**
		 * A glob import of all classes within a package
		 */
		Package,
		// TODO: Static imports
	}
	
	/**
	 * The type of this import
	 */
	private final ImportType type;
	
	/**
	 * The scope of this import
	 * 
	 * For type imports, this is the fully qualified name of the imported type. For package (glob) imports, this is
	 * the name of the package imported.
	 */
	private final String scope;

	public Import(ImportType type, String scope) {
		this.type = type;
		this.scope = scope;
	}

	public ImportType getType() {
		return type;
	}

	public String getScope() {
		return scope;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Import other = (Import) obj;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}

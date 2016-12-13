package org.coffeebag.domain;

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
		TYPE,
		/**
		 * A glob import of all classes within a package, or all inner classes within a class
		 */
		GLOB
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
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Import other = (Import) obj;
		return scope.equals(other.scope)&&
				type == other.type;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("import ");
		builder.append(scope);
		switch (type) {
		case TYPE:
			builder.append(';');
			break;
		case GLOB:
			builder.append(".*;");
			break;
		}
		return builder.toString();
	}
}

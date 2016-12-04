package org.coffeebag.processor.references;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

/**
 * Visits AST nodes and scans for used types
 */
class ReferenceVisitor extends TreeScanner<Void, Void> {

	/**
	 * The processing environment
	 */
	private final ProcessingEnvironment mEnv;
	
	/**
	 * The packages that have all their classes imported using glob imports
	 */
	private final Set<Import> mImports;
	/**
	 * The types referenced in the code
	 */
	private final Set<String> mTypes;

	/**
	 * Creates a reference visitor
	 * 
	 * @param globImports
	 *            the packages that have been imported. This set will not be
	 *            modified
	 */
	ReferenceVisitor(ProcessingEnvironment env, Set<Import> imports) {
		mEnv = env;
		mImports = imports;
		mTypes = new HashSet<>();
	}

	/**
	 * Returns the types that have been referenced in the processed code
	 * 
	 * If this visitor has not yet been accepted by any code, the returned set
	 * will be empty.
	 * 
	 * @return an unmodifiable set of referenced type names
	 */
	public Set<String> getTypes() {
		return Collections.unmodifiableSet(mTypes);
	}

	@Override
	public Void visitVariable(VariableTree tree, Void arg1) {
		System.out.println("[Tree] Visiting variable " + tree);
		System.out.println("[Tree] Type kind: " + tree.getType().getKind());
		System.out.println("[Tree] Type string: " + tree.getType());
		Tree varType = tree.getType();
		switch (varType.getKind()) {
		case MEMBER_SELECT:
			// Type name is a fully-qualified type
			mTypes.add(varType.toString());
			break;
		case IDENTIFIER:
			// Type name is an unqualified ID
			final String qualified = resolveUnqualifiedType(varType.toString());
			if (qualified != null) {
				mTypes.add(qualified);
			}
			break;
		default:
			System.out.println("[Tree] Variable type has unexpected kind " + varType.getKind());
			break;
		}
		return super.visitVariable(tree, arg1);
	}

	/**
	 * Attempts to resolve an unqualified type name based on the imports in the
	 * file
	 * 
	 * @param unqualifiedName
	 *            the unqualified name
	 * @return the fully-qualified name of the type, or null if the name could
	 *         not be resolved
	 */
	private String resolveUnqualifiedType(String unqualifiedName) {
		for (Import anImport : mImports) {
			switch (anImport.getType()) {
			case Type:
				if (anImport.getScope().endsWith("." + unqualifiedName)) {
					return anImport.getScope();
				}
				break;
			case Package:
				final String qualified = resolveClassInPackage(anImport.getScope(), unqualifiedName);
				if (qualified != null) {
					return qualified;
				}
				break;
			}
		}
		return null;
	}
	
	/**
	 * Attempts to resolve an unqualified class name in a package
	 * @param packageName the package in which to look for classes
	 * @param className the class name to match
	 * @return the fully-qualified name of the class, or null if it could not be resolved in this package
	 */
	private String resolveClassInPackage(String packageName, String className) {
		// TODO: Handle imports of nested classes
		final PackageElement packageElement = mEnv.getElementUtils().getPackageElement(packageName);
		if (packageElement == null) {
			return null;
		}
		for (Element innerType : packageElement.getEnclosedElements()) {
			switch (innerType.getKind()) {
			// Intentional fallthrough
			case CLASS:
			case ENUM:
			case INTERFACE:
				if (innerType.getSimpleName().contentEquals(className)) {
					// Found
					return innerType.asType().toString();
				}
			default:
				break;
			}
		}
		return null;
	}
}
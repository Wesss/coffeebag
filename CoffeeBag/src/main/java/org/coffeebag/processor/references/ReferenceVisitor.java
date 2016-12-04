package org.coffeebag.processor.references;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import com.sun.source.tree.ParameterizedTypeTree;
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
		handleTypeTree(varType);
		return super.visitVariable(tree, arg1);
	}
	
	/**
	 * Interprets a tree that represents the type of a variable
	 * @param varType the type of a variable
	 */
	private void handleTypeTree(Tree varType) {
		System.out.println("[Tree] handleTypeTree(" + varType + ")");
		switch (varType.getKind()) {
		case MEMBER_SELECT:
			// Type name is a fully-qualified type
			mTypes.add(varType.toString());
			break;
		case IDENTIFIER:
			// Type name is an unqualified ID
			final String qualified = resolveUnqualifiedType(varType.toString());
			System.out.println("[Tree] Resolved unqualified \"" + varType + "\" as \"" + qualified + "\"");
			if (qualified != null) {
				mTypes.add(qualified);
			}
			break;
		case PARAMETERIZED_TYPE:
			final ParameterizedTypeTree parameterized = (ParameterizedTypeTree) varType;
			// The type of the parameterized type should be resolved like any other type
			handleTypeTree(parameterized.getType());
			break;
		default:
			System.out.println("[Tree] Variable type has unexpected kind " + varType.getKind());
			break;
		}
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
			System.out.printf("[Tree] Looking for \"%s\" in import \"%s\"\n", unqualifiedName, anImport);
			switch (anImport.getType()) {
			case TYPE:
				if (anImport.getScope().endsWith("." + unqualifiedName)) {
					return anImport.getScope();
				}
				break;
			case GLOB:
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
		final PackageElement packageElement = mEnv.getElementUtils().getPackageElement(packageName);
		if (packageElement != null) {
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
		}
		final TypeElement typeElement = mEnv.getElementUtils().getTypeElement(packageName);
		if (typeElement != null) {
			for (Element inner : typeElement.getEnclosedElements()) {
				switch (inner.getKind()) {
				// Intentional fallthrough
				case CLASS:
				case ENUM:
				case INTERFACE:
					if (inner.getSimpleName().contentEquals(className)) {
						// Found
						// Erase type to remove template arguments
						final Types types = mEnv.getTypeUtils();
						return types.erasure(inner.asType()).toString();
					}
				default:
					break;
				}
			}
		}
		return null;
	}
}
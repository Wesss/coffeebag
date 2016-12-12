package org.coffeebag.processor.references;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import org.coffeebag.domain.AccessElement;
import org.coffeebag.domain.Import;
import org.coffeebag.log.Log;

import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.TreeScanner;

/**
 * Visits AST nodes and scans for used types
 */
class ReferenceVisitor extends TreeScanner<Void, Void> {
	private static final String TAG = ReferenceVisitor.class.getSimpleName();
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
	private final Set<AccessElement> mTypes;

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
	public Set<AccessElement> getTypes() {
		return Collections.unmodifiableSet(mTypes);
	}

	@Override
	public Void visitVariable(VariableTree tree, Void arg1) {
		Log.d(TAG, "Visiting variable " + tree);
		Log.d(TAG, "Type kind: " + tree.getType().getKind());
		Log.d(TAG, "Type string: " + tree.getType());
		Tree varType = tree.getType();
		handleTypeTree(varType);
		return super.visitVariable(tree, arg1);
	}

	@Override
	public Void visitClass(ClassTree ct, Void arg1) {
		// Check superclass and interfaces
		final Tree extendsClause = ct.getExtendsClause();
		if (extendsClause != null) {
			handleTypeTree(extendsClause);
		}
		for (Tree superinterface : ct.getImplementsClause()) {
			handleTypeTree(superinterface);
		}
		return super.visitClass(ct, arg1);
	}

	@Override
	public Void visitTypeCast(TypeCastTree arg0, Void arg1) {
		// Check target type
		handleTypeTree(arg0.getType());
		return super.visitTypeCast(arg0, arg1);
	}

	@Override
	public Void visitTypeParameter(TypeParameterTree arg0, Void arg1) {
		for (Tree bound : arg0.getBounds()) {
			handleTypeTree(bound);
		}
		return super.visitTypeParameter(arg0, arg1);
	}

	/**
	 * Interprets a tree that represents the type of a variable
	 * 
	 * @param varType
	 *            the type of a variable
	 */
	private void handleTypeTree(Tree varType) {

		Log.d(TAG, "handleTypeTree(" + varType + ")");
		switch (varType.getKind()) {
		case MEMBER_SELECT:
			// Type name is a fully-qualified type
			// (Look for the type to see if it is an inner class instead of a
			// fully qualified name)
			final TypeElement typeElement = mEnv.getElementUtils().getTypeElement(varType.toString());
			if (typeElement != null) {
				Log.d(TAG, "Resolved fully-qualified type " + varType.toString());
				mTypes.add(new AccessElement(typeElement));
			}
			break;
		case IDENTIFIER:
			// Type name is an unqualified ID
			final String qualified = resolveUnqualifiedType(varType.toString());
			if (qualified != null) {
				Log.d(TAG, "Resolved unqualified \"" + varType + "\" as \"" + qualified + "\"");
				final TypeElement qualifiedElement = mEnv.getElementUtils().getTypeElement(qualified);
				if (qualifiedElement == null) {
					throw new IllegalStateException("Type element not found for " + qualified);
				}
				mTypes.add(new AccessElement(qualifiedElement));
			}
			break;
		case PARAMETERIZED_TYPE:
			final ParameterizedTypeTree parameterized = (ParameterizedTypeTree) varType;
			// The type of the parameterized type should be resolved like any
			// other type
			handleTypeTree(parameterized.getType());
			// Also do all the type parameters
			for (Tree typeParam : parameterized.getTypeArguments()) {
				handleTypeTree(typeParam);
			}
			break;
		case SUPER_WILDCARD:
			// ? super Class
			handleTypeTree(((WildcardTree) varType).getBound());
			break;
		case EXTENDS_WILDCARD:
			// ? extends Class
			handleTypeTree(((WildcardTree) varType).getBound());
			break;
		case ARRAY_TYPE:
			// type[]
			handleTypeTree(((ArrayTypeTree) varType).getType());
		case PRIMITIVE_TYPE:
		case UNBOUNDED_WILDCARD:
			// Ignore
			break;
		default:
			Log.d(TAG, "Variable type has unexpected kind " + varType.getKind());
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
		Log.v(TAG, "resolveUnqualifiedType(" + unqualifiedName + ")");
		for (Import anImport : mImports) {
			Log.d(TAG, String.format("Looking for \"%s\" in import \"%s\"", unqualifiedName, anImport));
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
	 * 
	 * @param packageName
	 *            the package in which to look for classes
	 * @param className
	 *            the class name to match
	 * @return the fully-qualified name of the class, or null if it could not be
	 *         resolved in this package
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
					break;
				default:
					break;
				}
			}
		}
		return null;
	}
}
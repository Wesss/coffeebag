package org.coffeebag.processor.references;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.coffeebag.domain.AccessElement;
import org.coffeebag.log.Log;

import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
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
	 * The type resolver
	 */
	private final TypeResolver typeResolver;
	
	/**
	 * The types referenced in the code
	 */
	private final Set<AccessElement> mTypes;
	
	/**
	 * The package of the class, or empty for the default package
	 */
	private String currentPackage;

	/**
	 * Creates a reference visitor
	 */
	ReferenceVisitor(ProcessingEnvironment env, TypeResolver resolver) {
		mEnv = env;
		this.typeResolver = resolver;
		mTypes = new HashSet<>();
		currentPackage = "";
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
	public Void visitCompilationUnit(CompilationUnitTree arg0, Void arg1) {
		// Store the package
		final ExpressionTree packageName = arg0.getPackageName();
		if (packageName != null) {
			currentPackage = packageName.toString();
		} else {
			currentPackage = "";
		}
		return super.visitCompilationUnit(arg0, arg1);
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

	@Override
	public Void visitNewClass(NewClassTree arg0, Void arg1) {
		// Resolve constructor type
		final ExpressionTree typeNameTree = arg0.getIdentifier();
		handleTypeTree(typeNameTree);
		return super.visitNewClass(arg0, arg1);
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
				mTypes.add(AccessElement.type(varType.toString()));
			}
			break;
		case IDENTIFIER:
			// Type name is an unqualified ID
			final String qualified = typeResolver.resolveUnqualifiedType(varType.toString(), currentPackage);
			Log.d(TAG, "Resolved unqualified \"" + varType + "\" as \"" + qualified + "\"");
			if (qualified != null) {
				mTypes.add(AccessElement.type(qualified));
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
}
package org.coffeebag.processor.references;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.Types;

import org.coffeebag.domain.Import;
import org.coffeebag.domain.Import.ImportType;
import org.coffeebag.log.Log;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * Finds the classes that a language element refers to
 * @author samcrow
 *
 */
public class ReferenceFinder {
	private static final String TAG = ReferenceFinder.class.getSimpleName();
	
	/**
	 * The processing environment
	 */
	private final ProcessingEnvironment mEnv;
	
	/**
	 * The names of the types that the code refers to
	 */
	private final Set<String> mTypes;
	
	/**
	 * The imports
	 */
	private final Set<Import> mImports;
	
	/**
	 * The trees object associated with the environment
	 */
	private final Trees mTrees;
	
	/**
	 * Creates a reference finder that will analyze the provided source
	 * @param source the source to analyze
	 */
	public ReferenceFinder(ProcessingEnvironment env, Element source) {
		Log.d(TAG, "-------- ReferenceFinder running on " + source.getSimpleName() + " --------");
		mEnv = env;
		mTrees = Trees.instance(env);
		mTypes = new HashSet<>();
		mImports = new HashSet<>();
		
		final TreePath elementPath = mTrees.getPath(source);
		final CompilationUnitTree compilationUnit = elementPath.getCompilationUnit();
		
		// Inspect imports
		final List<? extends ImportTree> imports = compilationUnit.getImports();
		
		for (ImportTree importTree : imports) {
			final Tree qualifiedId = importTree.getQualifiedIdentifier();
			Log.d(TAG, "Import tree kind: " + importTree.getKind());
			Log.d(TAG, "Import tree qualified ID kind: " + qualifiedId.getKind());
			if (qualifiedId.getKind().equals(Tree.Kind.MEMBER_SELECT)) {
				final MemberSelectTree idReference = (MemberSelectTree) qualifiedId;
				if (idReference.getIdentifier().contentEquals("*")) {
					// Glob import
					Log.d(TAG, "Glob import of package " + idReference.getExpression());
					mImports.add(new Import(ImportType.GLOB, idReference.getExpression().toString()));
				} else {
					// Single-class import
					Log.d(TAG, "Single-class import of " + qualifiedId);
					mImports.add(new Import(ImportType.TYPE, qualifiedId.toString()));
					mTypes.add(qualifiedId.toString());
				}
			}
		}
		
		Log.i(TAG, "-------- Starting ReferenceScanner --------");
		final ReferenceScanner scanner = new ReferenceScanner();
		scanner.scan(source);
		Log.i(TAG, "-------- ReferenceScanner done --------");
		
		Log.i(TAG, "-------- Starting ReferenceVisitor --------");
		final ReferenceVisitor visitor = new ReferenceVisitor(env, mImports);
		compilationUnit.accept(visitor, null);
		mTypes.addAll(visitor.getTypes());
		Log.i(TAG, "-------- ReferenceVisitor done --------");
		
		// Post-process
		// Remove java.lang
		for (final Iterator<String> iter = mTypes.iterator(); iter.hasNext() ; ) {
			final String typeName = iter.next();
			if (typeName.startsWith("java.lang.")) {
				iter.remove();
			}
		}
	}
	
	/**
	 * Returns an immutable set containing the types that the provided code refers to
	 * @return the referenced types
	 */
	public Set<String> getTypesUsed() {
		return Collections.unmodifiableSet(mTypes);
	}
	
	/**
	 * Scans items for used types
	 *
	 * Works at the annotation processor level using standard interfaces
	 */
	private class ReferenceScanner extends ElementScanner8<Void, Void> {

		@Override
		public Void visitVariable(VariableElement e, Void p) {
			Log.d(TAG, "Visiting variable " + e);
			Log.d(TAG, "Variable type is " + e.asType());
			Log.d(TAG, "Variable type kind: " + e.asType().getKind());
			
			final Types types = mEnv.getTypeUtils();
			
			final TypeMirror varType = e.asType();
			if (varType.getKind().equals(TypeKind.DECLARED)) {
				// Erase type to remove generic type parameters
				mTypes.add(types.erasure(varType).toString());
			}
			super.visitVariable(e, p);
			return null;
		}

		@Override
		public Void visitType(TypeElement e, Void p) {
			Log.d(TAG, "Visiting type " + e);
			final Types types = mEnv.getTypeUtils();
			mTypes.add(e.getSuperclass().toString());
			for (TypeMirror implInterface : e.getInterfaces()) {
				final TypeMirror erased = types.erasure(implInterface);
				Log.v(TAG, "Adding implemented interface " + erased);
				mTypes.add(erased.toString());
			}
			for (TypeParameterElement typeParam : e.getTypeParameters()) {
				visitTypeParameter(typeParam, p);
			}
			super.visitType(e, p);
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Void p) {
			Log.d(TAG, "Visiting executable " + e);
			final TypeMirror returnType = e.getReturnType();
			handleTypeMirror(returnType);
			for (TypeMirror exceptionType : e.getThrownTypes()) {
				handleTypeMirror(exceptionType);
			}
			for (TypeParameterElement typeParam : e.getTypeParameters()) {
				for (TypeMirror bound : typeParam.getBounds()) {
					handleTypeMirror(bound);
				}
			}
			super.visitExecutable(e, p);
			return null;
		}
		
		/**
		 * Processes a type mirror and adds relevant types
		 * @param type the type to process
		 */
		private void handleTypeMirror(TypeMirror type) {
			if (type == null) {
				return;
			}
			switch (type.getKind()) {
			case ARRAY:
				handleArrayType((ArrayType) type);
				break;
			case DECLARED:
				handleDeclaredType((DeclaredType) type);
				break;
			case INTERSECTION:
				handleIntersectionType((IntersectionType) type);
				break;
			case TYPEVAR:
				handleTypeVar((TypeVariable) type);
				break;
			case UNION:
				handleUnionType((UnionType) type);
				break;
			case WILDCARD:
				handleWildcardType((WildcardType) type);
				break;
			default:
				Log.v(TAG, "Ignoring type " + type + " with kind " + type.getKind());
				break;
			
			}
		}
		
		private void handleArrayType(ArrayType type) {
			handleTypeMirror(type.getComponentType());
		}
		
		private void handleDeclaredType(DeclaredType type) {
			// Handle arguments
			for (TypeMirror typeArg : type.getTypeArguments()) {
				handleTypeMirror(typeArg);
			}
			// Add the erasure of this type
			final Types types = mEnv.getTypeUtils();
			final TypeMirror erased = types.erasure(type);
			mTypes.add(erased.toString());
		}
		
		private void handleIntersectionType(IntersectionType type) {
			for (TypeMirror bound : type.getBounds()) {
				handleTypeMirror(bound);
			}
		}
		
		private void handleTypeVar(TypeVariable type) {
			handleTypeMirror(type.getLowerBound());
			handleTypeMirror(type.getUpperBound());
		}
		
		private void handleUnionType(UnionType type) {
			for (TypeMirror option : type.getAlternatives()) {
				handleTypeMirror(option);
			}
		}
		
		private void handleWildcardType(WildcardType type) {
			handleTypeMirror(type.getExtendsBound());
			handleTypeMirror(type.getSuperBound());
		}

		@Override
		public Void visitTypeParameter(TypeParameterElement e, Void p) {
			Log.d(TAG, "Visiting type parameter " + e);
			for (TypeMirror typeBound : e.getBounds()) {
				Log.v(TAG, "Adding type generic type bound " + typeBound);
				mTypes.add(typeBound.toString());
			}
			return super.visitTypeParameter(e, p);
		}
	}
}

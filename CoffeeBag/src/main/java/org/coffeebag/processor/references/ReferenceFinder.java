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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.Types;

import org.coffeebag.domain.Import;
import org.coffeebag.domain.Import.ImportType;

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
			System.out.println("Import tree kind: " + importTree.getKind());
			System.out.println("Import tree qualified ID kind: " + qualifiedId.getKind());
			if (qualifiedId.getKind().equals(Tree.Kind.MEMBER_SELECT)) {
				final MemberSelectTree idReference = (MemberSelectTree) qualifiedId;
				if (idReference.getIdentifier().contentEquals("*")) {
					// Glob import
					System.out.println("Glob import of package " + idReference.getExpression());
					mImports.add(new Import(ImportType.GLOB, idReference.getExpression().toString()));
				} else {
					// Single-class import
					mTypes.add(qualifiedId.toString());
				}
			}
		}
		
		final ReferenceScanner scanner = new ReferenceScanner();
		scanner.scan(source);
		
		final ReferenceVisitor visitor = new ReferenceVisitor(env, mImports);
		compilationUnit.accept(visitor, null);
		mTypes.addAll(visitor.getTypes());
		
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
			System.out.println("Visiting variable " + e);
			System.out.println("Variable type is " + e.asType());
			System.out.println("Variable type kind: " + e.asType().getKind());
			
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
			System.out.println("Visiting type " + e);
			mTypes.add(e.getSuperclass().toString());
			for (TypeMirror implInterface : e.getInterfaces()) {
				mTypes.add(implInterface.toString());
			}
			super.visitType(e, p);
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Void p) {
			System.out.println("Visiting executable " + e);
			if (e.getReturnType().getKind().equals(TypeKind.DECLARED)) {
				mTypes.add(e.getReturnType().toString());
			}
			for (TypeMirror exceptionType : e.getThrownTypes()) {
				mTypes.add(exceptionType.toString());
			}
			for (TypeParameterElement typeParam : e.getTypeParameters()) {
				for (TypeMirror bound : typeParam.getBounds()) {
					mTypes.add(bound.toString());
				}
			}
			super.visitExecutable(e, p);
			return null;
		}

		@Override
		public Void visitTypeParameter(TypeParameterElement e, Void p) {
			System.out.println("Visiting type parameter " + e);
			mTypes.add(e.asType().toString());
			super.visitTypeParameter(e, p);
			return null;
		}
	}
}

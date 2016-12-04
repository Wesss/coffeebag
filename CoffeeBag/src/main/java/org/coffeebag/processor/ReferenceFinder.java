package org.coffeebag.processor;

import java.util.ArrayList;
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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * Finds the classes that a language element refers to
 * @author samcrow
 *
 */
public class ReferenceFinder {
	
	/**
	 * The names of the types that the code refers to
	 */
	private final Set<String> types = new HashSet<>();
	
	/**
	 * The trees object associated with the environment
	 */
	private final Trees trees;
	
	/**
	 * Creates a reference finder that will analyze the provided source
	 * @param source the source to analyze
	 */
	public ReferenceFinder(ProcessingEnvironment env, Element source) {
		this.trees = Trees.instance(env);
		
		final TreePath elementPath = trees.getPath(source);
		final CompilationUnitTree compilationUnit = elementPath.getCompilationUnit();
		
		// Inspect imports
		final List<? extends ImportTree> imports = compilationUnit.getImports();
		// Track the packages imported using glob imports
		final List<String> globPackages = new ArrayList();
		
		for (ImportTree importTree : imports) {
			final Tree qualifiedId = importTree.getQualifiedIdentifier();
			System.out.println("Import tree kind: " + importTree.getKind());
			System.out.println("Import tree qualified ID kind: " + qualifiedId.getKind());
			if (qualifiedId.getKind().equals(Tree.Kind.MEMBER_SELECT)) {
				final MemberSelectTree idReference = (MemberSelectTree) qualifiedId;
				if (idReference.getIdentifier().contentEquals("*")) {
					// Glob import
					System.out.println("Glob import of package " + idReference.getExpression());
					globPackages.add(idReference.getExpression().toString());
				} else {
					// Single-class import
					types.add(qualifiedId.toString());
				}
			}
		}
		
		final ReferenceScanner scanner = new ReferenceScanner();
		scanner.scan(source);
		
		final ReferenceVisitor visitor = new ReferenceVisitor();
		compilationUnit.accept(visitor, null);
		
		// Post-process
		// Remove java.lang
		for (final Iterator<String> iter = types.iterator(); iter.hasNext() ; ) {
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
		return Collections.unmodifiableSet(types);
	}
	
	/**
	 * Visits AST nodes and scans for used types
	 */
	private class ReferenceVisitor extends TreeScanner<Void, Void> {

		@Override
		public Void visitVariable(VariableTree tree, Void arg1) {
			System.out.println("[Tree] Visiting variable " + tree);
			System.out.println("[Tree] Type kind: " + tree.getType().getKind());
			System.out.println("[Tree] Type string: " + tree.getType());
			Tree varType = tree.getType();
			if (varType.getKind().equals(Tree.Kind.MEMBER_SELECT)) {
				// Type name is a fully-qualified type
				types.add(varType.toString());
			}
			return super.visitVariable(tree, arg1);
		}
		
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
			final TypeMirror varType = e.asType();
			if (varType.getKind().equals(TypeKind.DECLARED)) {
				types.add(e.asType().toString());
			}
			super.visitVariable(e, p);
			return null;
		}

		@Override
		public Void visitType(TypeElement e, Void p) {
			System.out.println("Visiting type " + e);
			types.add(e.getSuperclass().toString());
			for (TypeMirror implInterface : e.getInterfaces()) {
				types.add(implInterface.toString());
			}
			super.visitType(e, p);
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Void p) {
			System.out.println("Visiting executable " + e);
			if (e.getReturnType().getKind().equals(TypeKind.DECLARED)) {
				types.add(e.getReturnType().toString());
			}
			for (TypeMirror exceptionType : e.getThrownTypes()) {
				types.add(exceptionType.toString());
			}
			for (TypeParameterElement typeParam : e.getTypeParameters()) {
				for (TypeMirror bound : typeParam.getBounds()) {
					types.add(bound.toString());
				}
			}
			super.visitExecutable(e, p);
			return null;
		}

		@Override
		public Void visitTypeParameter(TypeParameterElement e, Void p) {
			System.out.println("Visiting type parameter " + e);
			types.add(e.asType().toString());
			super.visitTypeParameter(e, p);
			return null;
		}
	}
}

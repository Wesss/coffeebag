package org.coffeebag.processor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner8;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.TreePath;
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
		final List<? extends ImportTree> imports = compilationUnit.getImports();
		for (ImportTree importTree : imports) {
			types.add(importTree.getQualifiedIdentifier().toString());
		}
		
		final ReferenceScanner scanner = new ReferenceScanner();
		scanner.scan(source);
		
		final ReferenceVisitor visitor = new ReferenceVisitor();
		compilationUnit.accept(visitor, null);
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
	private class ReferenceVisitor extends SimpleTreeVisitor<Void, Void> {
		
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
			types.add(e.asType().toString());
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
			types.add(e.getReturnType().toString());
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

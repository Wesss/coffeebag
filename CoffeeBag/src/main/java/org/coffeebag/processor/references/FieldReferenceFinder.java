package org.coffeebag.processor.references;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.coffeebag.domain.AccessElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;

public class FieldReferenceFinder {
	
	private final FieldReferenceVisitor visitor;
	
	public FieldReferenceFinder(ProcessingEnvironment env, TypeResolver resolver, Element element) {
		visitor = new FieldReferenceVisitor(resolver);
		final Trees trees = Trees.instance(env);
		final CompilationUnitTree tree = trees.getPath(element).getCompilationUnit();
		
		visitor.scan(tree, null);
	}
	
	public Set<AccessElement> getReferencedFields() {
		return visitor.getReferencedFields();
	}
}

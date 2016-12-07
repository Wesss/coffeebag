package org.coffeebag.processor.invariants;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import org.coffeebag.domain.VisibilityInvariants;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class InvariantFinder {
	private static final String TAG = InvariantVisitor.class.getSimpleName();

	Trees trees;
	InvariantVisitor visitor;

	public InvariantFinder(ProcessingEnvironment env, Element elementRoot) {
		trees = Trees.instance(env);
		visitor = new InvariantVisitor(trees, trees.getPath(elementRoot).getCompilationUnit());

		visitor.scanFromRoot();
	}

	/**
	 *
	 */
	public VisibilityInvariants getVisibilityInvariants() {
		return visitor.getInvariants();
	}
}

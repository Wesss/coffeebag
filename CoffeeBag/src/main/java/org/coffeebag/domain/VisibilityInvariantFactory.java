package org.coffeebag.domain;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import org.coffeebag.annotations.Visibility;

public class VisibilityInvariantFactory {

	Trees trees;
	CompilationUnitTree root;

	public VisibilityInvariantFactory(Trees trees, CompilationUnitTree root) {
		this.trees = trees;
		this.root = root;
	}

	public VisibilityInvariant createInvariant(ClassTree tree, Visibility visibility) {
		return new VisibilityInvariant(trees.getPath(root, tree), tree.getSimpleName(), visibility);
	}
}

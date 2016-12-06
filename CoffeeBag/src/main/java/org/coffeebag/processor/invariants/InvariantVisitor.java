package org.coffeebag.processor.invariants;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import org.coffeebag.domain.VisibilityInvariants;
import org.coffeebag.log.Log;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class InvariantVisitor extends TreeScanner<Void, Void> {
	private static final String TAG = InvariantVisitor.class.getSimpleName();

	private Trees trees; // utility object
	private CompilationUnitTree root;
	private VisibilityInvariants invariants;

	public InvariantVisitor(Trees trees, CompilationUnitTree root) {
		this.root = root;
		this.trees = trees;
		invariants = new VisibilityInvariants();
	}

	public VisibilityInvariants getInvariants() {
		return invariants;
	}

	@Override
	public Void visitClass(ClassTree tree, Void v) {
		Log.d(TAG, "Class " + tree.getSimpleName() + ":\n" +
				tree.getModifiers().getAnnotations().toString());
		invariants.addTypeElement((TypeElement)getElement(tree)); // TODO fix horrible cast
		return super.visitClass(tree, v);
	}

//	@Override
//	public Void visitMethod(MethodTree tree, Void v) {
//		Log.d(TAG, "Method " + tree.getName() + ":\n" +
//				tree.getModifiers().getAnnotations().toString());
//		return super.visitMethod(tree, v);
//	}

	// TODO enum?, field, interface

	private Element getElement(Tree tree) {
		return trees.getElement(trees.getPath(root, tree));
	}
}

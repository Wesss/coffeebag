package org.coffeebag.processor.invariants;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import org.coffeebag.annotations.Visibility;
import org.coffeebag.domain.VisibilityInvariant;
import org.coffeebag.domain.VisibilityInvariantFactory;
import org.coffeebag.domain.VisibilityInvariants;
import org.coffeebag.log.Log;

import javax.lang.model.util.Types;

public class InvariantVisitor extends TreePathScanner<Void, Void> {
	private static final String TAG = InvariantVisitor.class.getSimpleName();

	private Trees trees; // utility object
	private VisibilityInvariantFactory invariantFactory;

	private CompilationUnitTree root;
	private VisibilityInvariants invariants;

	public InvariantVisitor(Trees trees, CompilationUnitTree root) {
		this.trees = trees;
		invariantFactory = new VisibilityInvariantFactory(trees, root);

		this.root = root;
		invariants = new VisibilityInvariants();
	}

	public void scanFromRoot() {
		scan(root, null);
	}

	public VisibilityInvariants getInvariants() {
		return invariants;
	}

	@Override
	public Void visitClass(ClassTree tree, Void v) {
		Log.d(TAG, "Class " + tree.getSimpleName() + ":\n" +
				tree.getModifiers().getAnnotations().toString());
		Visibility visibility = null;
		for (AnnotationTree annotation : tree.getModifiers().getAnnotations()) {
			//TODO see if annotation is an Access notification. Probably need Types
			Log.d(TAG, "0 " + annotation);
			Log.d(TAG, "1 " + annotation.getAnnotationType());
			Log.d(TAG, "2 " + annotation.getAnnotationType().getKind());
			Log.d(TAG, "3 " + annotation.getArguments());
			Log.d(TAG, "4 " + annotation.getArguments().get(0));
		}
		invariants.addClassInvariant(invariantFactory.createInvariant(tree, visibility));
		return super.visitClass(tree, v);
	}

//	@Override
//	public Void visitMethod(MethodTree tree, Void v) {
//		Log.d(TAG, "Method " + tree.getName() + ":\n" +
//				tree.getModifiers().getAnnotations().toString());
//		return super.visitMethod(tree, v);
//	}
}

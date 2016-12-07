package org.coffeebag.processor.domain;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import org.coffeebag.annotations.Visibility;
import org.coffeebag.domain.VisibilityInvariants;

/**
 * Represents an invariant expected by a test
 */
public class ExpectedInvariant {

	private String className;
	private Visibility visibility;

	public ExpectedInvariant(String className, String visibility) {
		this.className = className;
		this.visibility = Visibility.valueOf(visibility);
	}

	public String getClassName() {
		return className;
	}

	public Visibility getVisibility() {
		return visibility;
	}
}

package org.coffeebag.domain;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import org.coffeebag.annotations.Visibility;

import javax.lang.model.element.Name;

/**
 * Represents an invariant notated by a coffeebag annotation
 */
public class VisibilityInvariant {

	TreePath path;
	Name simpleClassName;
	Visibility visibility;

	public VisibilityInvariant(TreePath path, Name simpleClassName, Visibility visibility) {
		this.path = path;
		this.simpleClassName = simpleClassName;
		this.visibility = visibility;
	}

	public TreePath getPath() {
		return path;
	}

	public Name getSimpleClassName() {
		return simpleClassName;
	}

	public Visibility getVisibility() {
		return visibility;
	}
}

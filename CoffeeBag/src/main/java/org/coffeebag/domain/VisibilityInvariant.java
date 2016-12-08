package org.coffeebag.domain;

import javax.lang.model.element.Name;

import org.coffeebag.annotations.Visibility;

import com.sun.source.util.TreePath;

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

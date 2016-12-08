package org.coffeebag.processor.domain;

import org.coffeebag.annotations.Visibility;

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

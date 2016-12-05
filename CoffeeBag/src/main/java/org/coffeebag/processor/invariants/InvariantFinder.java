package org.coffeebag.processor.invariants;

import org.coffeebag.domain.VisibilityInvariants;

import java.util.Collections;
import java.util.Set;

public class InvariantFinder {

	public InvariantFinder() {
		//TODO
	}

	/**
	 * Returns an immutable set containing the types that the provided code refers to
	 * @return the referenced types
	 */
	public VisibilityInvariants getVisibilityInvariants() {
		return new VisibilityInvariants();
	}
}

package org.coffeebag.processor.invariants;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.coffeebag.domain.VisibilityInvariant;

public class InvariantFinder {
	private static final String TAG = InvariantFinder.class.getSimpleName();

	public InvariantFinder(ProcessingEnvironment env, Element elementRoot) {
	}

	/**
	 * returns the elements annotated with @Access and their corresponding visibility invariants
	 * @return a map such that map.keyset() is the set of all anotated elements and map.get(element)
	 *      gives the information on where specified element can be used.
	 */
	public Map<Element, VisibilityInvariant> getVisibilityInvariants() {
		return new HashMap<>();// TODO
	}
}

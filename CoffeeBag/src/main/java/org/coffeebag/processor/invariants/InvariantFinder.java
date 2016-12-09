package org.coffeebag.processor.invariants;

import org.coffeebag.annotations.Visibility;
import org.coffeebag.domain.VisibilityInvariantFactory;
import org.coffeebag.domain.VisibilityInvariant;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InvariantFinder {

	private static final String TAG = InvariantFinder.class.getSimpleName();

	private HashMap<String, VisibilityInvariant> invariants;

	public InvariantFinder() {
		invariants = new HashMap<>();
	}

	/**
	 * returns the elements annotated with @Access and their corresponding visibility invariants
	 * @return a map such that map.keyset() is the set of all anotated elements' fully qualified names
	 *      and map.get(elements gives the information on where specified element can be used.
	 */
	public Map<String, VisibilityInvariant> getVisibilityInvariants(ProcessingEnvironment env, Element elementRoot) {
		InvariantScanner elementWalker = new InvariantScanner(this);
		elementWalker.scan(elementRoot);

		return Collections.unmodifiableMap(invariants);
	}

	protected void storeInvariant(TypeElement element, Visibility visibility) {
		invariants.put(
				element.getQualifiedName().toString(),
				VisibilityInvariantFactory.getPublicInvariant()
		);
	}
}

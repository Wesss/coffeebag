package org.coffeebag.processor.invariants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.invariant.VisibilityInvariant;
import org.coffeebag.domain.invariant.VisibilityInvariantFactory;

public class InvariantFinder {

	@SuppressWarnings("unused")
	private static final String TAG = InvariantFinder.class.getSimpleName();

	/**
	 * returns the elements annotated with @Access and their corresponding visibility invariants
	 * @return a map such that map.keyset() is the set of all anotated elements' fully qualified names
	 *      and map.get(elements gives the information on where specified element can be used.
	 */
	public Map<String, VisibilityInvariant> getVisibilityInvariants(RoundEnvironment roundEnv) {
		HashMap<String, VisibilityInvariant> invariants = new HashMap<>();

		for (Element element : roundEnv.getElementsAnnotatedWith(Access.class)) {
			if (element instanceof TypeElement) {
				TypeElement typeElement = ((TypeElement) element);
				invariants.put(
						typeElement.getQualifiedName().toString(),
						VisibilityInvariantFactory.getInvariant(typeElement));
			}
		}

		return Collections.unmodifiableMap(invariants);
	}
}

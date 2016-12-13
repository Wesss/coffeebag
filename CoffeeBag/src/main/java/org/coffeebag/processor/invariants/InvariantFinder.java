package org.coffeebag.processor.invariants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.AccessElement;
import org.coffeebag.domain.invariant.VisibilityInvariant;
import org.coffeebag.domain.invariant.VisibilityInvariantFactory;
import org.coffeebag.log.Log;

public class InvariantFinder {

	@SuppressWarnings("unused")
	private static final String TAG = InvariantFinder.class.getSimpleName();

	/**
	 * The processing environment
	 */
	private final ProcessingEnvironment env;

	/**
	 * Creates a new invariant finder
	 * 
	 * @param env
	 *            the processing environment
	 */
	public InvariantFinder(ProcessingEnvironment env) {
		Objects.requireNonNull(env);
		this.env = env;
	}

	/**
	 * returns the elements annotated with @Access and their corresponding
	 * visibility invariants
	 * 
	 * @return a map such that map.keyset() is the set of all anotated elements'
	 *         fully qualified names and map.get(elements gives the information
	 *         on where specified element can be used.
	 */
	public Map<AccessElement, VisibilityInvariant> getVisibilityInvariants(RoundEnvironment roundEnv) {
		final HashMap<AccessElement, VisibilityInvariant> invariants = new HashMap<>();

		for (Element element : roundEnv.getElementsAnnotatedWith(Access.class)) {
			AccessElement accessElement;
			if (element.getKind().isClass() || element.getKind().isInterface()) {
				accessElement = AccessElement.type((TypeElement) element);
			} else if (element.getKind().isField()) {
				accessElement = AccessElement.field((VariableElement) element);
			} else {
				// Unsupported type
				Log.i(TAG, "Ignoring element " + element.getSimpleName() + " with unsupported kind " + element.getKind());
				continue;
			}
			final VisibilityInvariant invariant = VisibilityInvariantFactory.getInvariant(element, env);
			if (invariant != null) {
				invariants.put(accessElement, invariant);
			}
		}
		return Collections.unmodifiableMap(invariants);
	}
}

package org.coffeebag.processor.invariants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.invariant.VisibilityInvariant;
import org.coffeebag.domain.invariant.VisibilityInvariantFactory;

public class InvariantFinder {

	@SuppressWarnings("unused")
	private static final String TAG = InvariantFinder.class.getSimpleName();
	
	/**
	 * The processing environment
	 */
	private final ProcessingEnvironment env;

	/**
	 * Creates a new invariant finder
	 * @param env the processing environment
	 */
	public InvariantFinder(ProcessingEnvironment env) {
		Objects.requireNonNull(env);
		this.env = env;
	}



	/**
	 * returns the elements annotated with @Access and their corresponding visibility invariants
	 * @return a map such that map.keyset() is the set of all anotated elements' fully qualified names
	 *      and map.get(elements gives the information on where specified element can be used.
	 */
	public Map<String, VisibilityInvariant> getVisibilityInvariants(RoundEnvironment roundEnv) {
		HashMap<String, VisibilityInvariant> invariants = new HashMap<>();

		for (Element element : roundEnv.getElementsAnnotatedWith(Access.class)) {
			/*
			 * From the Element documentation: Using {@code
			 * instanceof} is <em>not</em> necessarily a reliable idiom for
			 * determining the effective class of an object in this modeling
			 * hierarchy since an implementation may choose to have a single object
			 * implement multiple {@code Element} subinterfaces.
			 */
			final ElementKind kind = element.getKind();
			if (kind ==  ElementKind.CLASS || kind == ElementKind.INTERFACE || kind == ElementKind.ENUM) {
				TypeElement typeElement = ((TypeElement) element);
				final VisibilityInvariant invariant = VisibilityInvariantFactory.getInvariant(typeElement, env);
				if (invariant != null) {
					invariants.put(typeElement.getQualifiedName().toString(), invariant);
				}
			}
		}

		return Collections.unmodifiableMap(invariants);
	}
}

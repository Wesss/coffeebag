package org.coffeebag.processor.invariants;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.invariant.VisibilityInvariant;
import org.coffeebag.domain.invariant.VisibilityInvariantFactory;
import org.coffeebag.log.Log;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InvariantFinder {

	private static final String TAG = InvariantFinder.class.getSimpleName();

	/**
	 * returns the elements annotated with @Access and their corresponding visibility invariants
	 * @return a map such that map.keyset() is the set of all anotated elements' fully qualified names
	 *      and map.get(elements gives the information on where specified element can be used.
	 */
	public Map<String, VisibilityInvariant> getVisibilityInvariants(ProcessingEnvironment processingEnv,
	                                                                RoundEnvironment roundEnv) {
		HashMap<String, VisibilityInvariant> invariants = new HashMap<>();

		for (Element element : roundEnv.getElementsAnnotatedWith(Access.class)) {
			if (element instanceof TypeElement) {
				TypeElement typeElement = ((TypeElement) element);
				processTypeElement(invariants, typeElement);
			}
		}

		return Collections.unmodifiableMap(invariants);
	}

	private void processTypeElement(HashMap<String, VisibilityInvariant> invariants, TypeElement typeElement) {
		Access annotation = typeElement.getAnnotation(Access.class);
		String qualifiedName = typeElement.getSimpleName().toString();
		switch (annotation.level()) {
			case PUBLIC:
				invariants.put(
						qualifiedName,
						VisibilityInvariantFactory.getPublicInvariant());
				break;
			case PRIVATE:
				//TODO allow nested classes? this assumes next element up is package element
				String packageName =
						((PackageElement)typeElement.getEnclosingElement()).getQualifiedName().toString();
				VisibilityInvariant invariant = VisibilityInvariantFactory.getPrivateInvariant(
						packageName,
						qualifiedName);
				invariants.put(qualifiedName, invariant);
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());

		}
	}
}

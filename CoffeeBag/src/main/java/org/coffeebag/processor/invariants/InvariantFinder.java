package org.coffeebag.processor.invariants;

import org.coffeebag.annotations.Access;
import org.coffeebag.domain.VisibilityInvariant;
import org.coffeebag.domain.VisibilityInvariantFactory;
import org.coffeebag.log.Log;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
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
				Access annotation = element.getAnnotation(Access.class);
				if (annotation != null) {
					String qualifiedName = typeElement.getQualifiedName().toString();
					switch (annotation.level()) {
						case PUBLIC:
							invariants.put(qualifiedName,
									VisibilityInvariantFactory.getPublicInvariant());
							break;
						case PRIVATE:
							invariants.put(qualifiedName,
									VisibilityInvariantFactory.getPrivateInvariant());
						default:
							Log.d(TAG, "Unsupported visibility " + annotation.level());
					}
				}
			}
		}

		return Collections.unmodifiableMap(invariants);
	}
}

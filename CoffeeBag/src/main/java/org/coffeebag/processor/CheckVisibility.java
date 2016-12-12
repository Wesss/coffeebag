package org.coffeebag.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import org.coffeebag.domain.AccessElement;
import org.coffeebag.domain.invariant.VisibilityInvariant;
import org.coffeebag.log.Log;
import org.coffeebag.processor.invariants.InvariantFinder;
import org.coffeebag.processor.references.ReferenceFinder;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {
	private static final String TAG = CheckVisibility.class.getSimpleName();

	/**
	 * Maps from a canonical class name to a set of elements that it references
	 */
	private Map<String, Set<AccessElement>> typeReferences;

	/**
	 * Maps an annotated element to its visibility invariant. If an item is not
	 * present in this mapping, it was not annotated
	 */
	private Map<AccessElement, VisibilityInvariant> annotatedMemberToInvariant;

	/**
	 * Creates a new processor that does not log
	 */
	public CheckVisibility() {
		this(false);
	}

	/**
	 * Creates a new processor
	 *
	 * @param log
	 *            if the processor should output log information
	 */
	public CheckVisibility(boolean log) {
		typeReferences = new HashMap<>();
		annotatedMemberToInvariant = new HashMap<>();
		Log.getInstance().setEnabled(log);
	}

	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			// build member usage structure
			for (Element element : roundEnv.getRootElements()) {
				Log.v(TAG, "Root element " + element.getSimpleName());
				// Get erased type name
				final TypeMirror erasure = processingEnv.getTypeUtils().erasure(element.asType());

				final ReferenceFinder finder = new ReferenceFinder(processingEnv, element);
				final Set<AccessElement> usedTypes = finder.getTypesUsed();
				// Record usages
				typeReferences.put(erasure.toString(), usedTypes);
			}

			// build visibility invariant structure
			InvariantFinder finder = new InvariantFinder(processingEnv);
			annotatedMemberToInvariant.putAll(finder.getVisibilityInvariants(roundEnv));

			// Output invariants
			for (Map.Entry<AccessElement, VisibilityInvariant> entry : annotatedMemberToInvariant.entrySet()) {
				System.out.println("[Invariant] " + entry.getKey() + ": " + entry.getValue());
			}

		} else {
			Log.d(TAG, "-------- Starting final processing --------");

			Log.d(TAG, "All invariants:");
			for (Entry<AccessElement, VisibilityInvariant> entry : annotatedMemberToInvariant.entrySet()) {
				Log.d(TAG, entry.getKey() + " => " + entry.getValue());
			}

			// compare visibility invariants and their usages
			for (Entry<String, Set<AccessElement>> typeReference : typeReferences.entrySet()) {
				// The class being checked
				final String className = typeReference.getKey();
				final TypeElement usingClass = processingEnv.getElementUtils().getTypeElement(className);
				if (usingClass == null) {
					throw new IllegalStateException("Type element not found for canonical name " + className);
				}
				// The types that the class being checked refers to
				final Set<AccessElement> referencedTypes = typeReference.getValue();

				// Check each referenced type in this context
				for (AccessElement referencedType : referencedTypes) {
					final VisibilityInvariant invariant = annotatedMemberToInvariant.get(referencedType);
					if (invariant != null) {
						if (invariant.isUsageAllowedIn(usingClass)) {
							Log.v(TAG, "Usage of " + referencedType + " OK in " + className);
						} else {
							final Messager messager = processingEnv.getMessager();
							messager.printMessage(Kind.ERROR,
									"Type " + referencedType + " is not visible to " + className, usingClass);
						}
					} else {
						Log.v(TAG, "No visibility invariant for referenced class " + referencedType);
					}
				}
			}
		}
		// Allow other annotations to be processed
		return false;
	}

	/**
	 * For testing, returns the referenced types that were detected
	 *
	 * @return the types analyzed and the types that they refer to
	 */
	Map<String, Set<String>> getTypeReferences() {
		final Map<String, Set<String>> stringified = new HashMap<>(typeReferences.size());
		for (Map.Entry<String, Set<AccessElement>> entry : typeReferences.entrySet()) {
			final Set<String> referencedStrings = new HashSet<>(entry.getValue().size());
			for (AccessElement element : entry.getValue()) {
				referencedStrings.add(element.toString());
			}
			stringified.put(entry.getKey(), referencedStrings);
		}
		return stringified;
	}

	/**
	 * For testing
	 *
	 * @return the invariants generated by the processor
	 */
	Map<String, VisibilityInvariant> getInvariants() {
		final Map<String, VisibilityInvariant> stringified = new HashMap<>(annotatedMemberToInvariant.size());
		for (Map.Entry<AccessElement, VisibilityInvariant> entry : annotatedMemberToInvariant.entrySet()) {
			stringified.put(entry.getKey().toString(), entry.getValue());
		}
		return stringified;
	}
}

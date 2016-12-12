package org.coffeebag.processor;

import java.util.HashMap;
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
import org.coffeebag.processor.references.FieldReferenceFinder;
import org.coffeebag.processor.references.ReferenceFinder;
import org.coffeebag.processor.references.TypeResolver;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {
	private static final String TAG = CheckVisibility.class.getSimpleName();

	/**
	 * Maps from a canonical class name to a set of canonical class/interface/enum names that it references
	 */
	private Map<String, Set<String>> typeReferences;

	/**
	 * Maps an annotated element to its visibility invariant.
	 * If an item is not present in this mapping, it was not annotated
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
	 * @param log if the processor should output log information
	 */
	public CheckVisibility(boolean log) {
		typeReferences = new HashMap<>();
		annotatedMemberToInvariant = new HashMap<>();
		Log.getInstance().setEnabled(log);
		Log.getInstance().setTagFilter((tag) -> tag.startsWith("FieldReference"));
	}

	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			// build member usage structure
			for (Element element : roundEnv.getRootElements()) {
				// Get erased type name
				final TypeMirror erasure = processingEnv.getTypeUtils().erasure(element.asType());
				
				final TypeResolver resolver = new TypeResolver(processingEnv, element);
				
				final ReferenceFinder finder = new ReferenceFinder(processingEnv, resolver, element);
				final Set<String> usedTypes = finder.getTypesUsed();
				// Record usages
				typeReferences.put(erasure.toString(), usedTypes);

				StringBuilder message = new StringBuilder()
						.append("Element ")
						.append(element)
						.append(" used these types:");
				for (String used : usedTypes) {
					message.append("\n\t").append(used);
				}
				Log.d(TAG, message.toString());
				
				// Find usages of fields
				final FieldReferenceFinder fieldReferenceFinder = new FieldReferenceFinder(processingEnv, resolver, element);
				for (AccessElement referencedField : fieldReferenceFinder.getReferencedFields()) {
					Log.d(TAG, "Element " + element + " used field " + referencedField);
				}
			}

			// build visibility invariant structure
			InvariantFinder finder = new InvariantFinder(processingEnv);
			annotatedMemberToInvariant.putAll(finder.getVisibilityInvariants(roundEnv));
		} else {
			Log.d(TAG, "-------- Starting final processing --------");
			
			for (Map.Entry<AccessElement, VisibilityInvariant> entry : annotatedMemberToInvariant.entrySet()) {
				Log.d(TAG, "Invariant: " + entry.getKey() + " => " + entry.getValue());
			}
			
			// compare visibility invariants and their usages
			for (Entry<String, Set<String>> typeReference : typeReferences.entrySet()) {
				// The class being checked
				final String className = typeReference.getKey();
				Log.d(TAG, "Checking types referenced by " + className);
				final TypeElement usingClass = processingEnv.getElementUtils().getTypeElement(className);
				if (usingClass == null) {
					throw new IllegalStateException("Type element not found for canonical name " + className);
				}
				// The types that the class being checked refers to
				final Set<String> referencedTypes = typeReference.getValue();
				
				// Check each referenced type in this context
				for (String referencedType : referencedTypes) {
					Log.d(TAG, "Checking use of type " + referencedType);
					final AccessElement accessElement = AccessElement.type(referencedType);
					final VisibilityInvariant invariant = annotatedMemberToInvariant.get(accessElement);
					if (invariant != null) {
						if (invariant.isUsageAllowedIn(usingClass)) {
							Log.v(TAG, "Usage of " + referencedType + " OK in " + className);
						} else {
							final Messager messager = processingEnv.getMessager();
							messager.printMessage(Kind.ERROR, "Type " + referencedType + " is not visible to " + className, usingClass);
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
		return typeReferences;
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

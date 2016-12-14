package org.coffeebag.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
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
	private Map<String, Set<AccessElement>> typeReferences;
	/**
	 * Maps from a canonical class name to the set of fields that it references
	 */
	private Map<String, Set<AccessElement>> fieldReferences;

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
		fieldReferences = new HashMap<>();
		annotatedMemberToInvariant = new HashMap<>();
		Log.getInstance().setEnabled(log);
	}

	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			// build member usage structure
			for (Element element : roundEnv.getRootElements()) {
				// Ignore packages
				if (element.getKind() == ElementKind.PACKAGE) {
					continue;
				}
				// Get erased type name
				final String cannonicalClassName = processingEnv.getTypeUtils().erasure(element.asType()).toString();

				final TypeResolver resolver = new TypeResolver(processingEnv, element);
				final ReferenceFinder finder = new ReferenceFinder(processingEnv, resolver, element);
				final Set<AccessElement> usedTypes = finder.getTypesUsed();
				// Record usages
				typeReferences.put(cannonicalClassName, usedTypes);
				for (AccessElement referencedType : usedTypes) {
					Log.d(TAG, "Element " + element + " used type " + referencedType);
				}
				
				// Find usages of fields
				final FieldReferenceFinder fieldReferenceFinder = new FieldReferenceFinder(processingEnv, resolver, element);
				final Set<AccessElement> referencedFields = fieldReferenceFinder.getReferencedFields();
				fieldReferences.put(cannonicalClassName, referencedFields);
				for (AccessElement referencedField : referencedFields) {
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

			checkUsages(typeReferences, "class");
			checkUsages(fieldReferences, "field");
		}
		// Do not consume annotations
		return false;
	}

	private void checkUsages(Map<String, Set<AccessElement>> references, String usageType) {
		for (Entry<String, Set<AccessElement>> reference : references.entrySet()) {
			String className = reference.getKey();
			final TypeElement usingClass = processingEnv.getElementUtils().getTypeElement(className);
			if (usingClass == null) {
				throw new IllegalStateException("Type element not found for canonical name " + className);
			}
			for (AccessElement usage : reference.getValue()) {
				Log.d(TAG, "Checking use of " + usageType + " " + usage);
				final VisibilityInvariant invariant = annotatedMemberToInvariant.get(usage);
				if (invariant != null) {
					if (invariant.isUsageAllowedIn(usingClass)) {
						Log.v(TAG, "Usage of " + usage + " OK in " + className);
					} else {
						final Messager messager = processingEnv.getMessager();
						messager.printMessage(Kind.ERROR, usageType.substring(0, 1).toUpperCase() +
								usageType.substring(1) + " " + usage + " is not visible to " + className, usingClass);
					}
				} else {
					Log.v(TAG, "No visibility invariant for referenced " + usageType + " " + usage);
				}
			}
		}
	}

	/**
	 * For testing, returns the referenced types that were detected
	 *
	 * @return the types analyzed and the types that they refer to
	 */
	Map<String, Set<String>> getTypeReferences() {
		final Map<String, Set<String>> stringified = new HashMap<>(typeReferences.size());
		for (Map.Entry<String, Set<AccessElement>> entry : typeReferences.entrySet()) {
			stringified.put(entry.getKey(), entry.getValue().stream()
					.map(AccessElement::getTypeName)
					.collect(Collectors.toSet()));
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
	
	/**
	 * For testing, returns the field references that were detected
	 * 
	 * @return a map from canonical class names to the fields (ClassName.fieldName) referenced
	 */
	Map<String, Set<String>> getFieldReferences() {
		final Map<String, Set<String>> stringified = new HashMap<>(fieldReferences.size());
		for (Map.Entry<String, Set<AccessElement>> entry : fieldReferences.entrySet()) {
			stringified.put(entry.getKey(), entry.getValue().stream()
					.map(AccessElement::toString)
					.collect(Collectors.toSet()));
		}
		return stringified;
	}
}

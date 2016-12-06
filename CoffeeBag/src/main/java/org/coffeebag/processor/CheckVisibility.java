package org.coffeebag.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.coffeebag.domain.VisibilityInvariants;
import org.coffeebag.log.Log;
import org.coffeebag.processor.invariants.InvariantFinder;
import org.coffeebag.processor.references.ReferenceFinder;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {
	private static final String TAG = CheckVisibility.class.getSimpleName();
	
	/**
	 * Creates a processor in test mode
	 * 
	 * The returned processor will record information about its operations. It will return a non-null value from
	 * {@link #getTypeReferences()}.
	 * @return a new processor
	 */
	static CheckVisibility testMode() {
		final CheckVisibility processor = new CheckVisibility();
		processor.typeReferences = new HashMap<>();
		return processor;
	}

	/**
	 * Maps from a class name to a set of class/interface/enum/annotation names that it references
	 * 
	 * This is normally null. It is used in test mode to store results.
	 */
	private Map<String, Set<String>> typeReferences = null;

	private VisibilityInvariants invariants = new VisibilityInvariants();

	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			// build member usage structure
			for (Element element : roundEnv.getRootElements()) {
				final ReferenceFinder finder = new ReferenceFinder(processingEnv, element);
				final Set<String> usedTypes = finder.getTypesUsed();
				
				// Record if in test mode
				if (typeReferences != null) {
					typeReferences.put(element.asType().toString(), usedTypes);
				}

				StringBuilder message = new StringBuilder()
						.append("Element ")
						.append(element)
						.append(" used these types:");
				for (String used : usedTypes) {
					message.append("\n\t").append(used);
				}
				Log.d(TAG, message.toString());
			}

			// build visibility invariant structure
			for (Element element : roundEnv.getRootElements()) {
				InvariantFinder finder = new InvariantFinder(processingEnv, element);
			}
		} else {
			System.out.println("Processing over");
			// compare visibility invariants and their usages
			// processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error msg", element);
		}
		// Allow other annotations to be processed
		return false;
	}

	/**
	 * For testing, returns the referenced types that were detected
	 * @return the types analyzed and the types that they refer to
	 */
	Map<String, Set<String>> getTypeReferences() {
		return typeReferences;
	}
}

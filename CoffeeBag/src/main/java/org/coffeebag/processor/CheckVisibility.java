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

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {
	
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

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			for (Element element : roundEnv.getRootElements()) {
				final ReferenceFinder finder = new ReferenceFinder(processingEnv, element);
				final Set<String> usedTypes = finder.getTypesUsed();
				
				// Record if in test mode
				if (typeReferences != null) {
					typeReferences.put(element.asType().toString(), usedTypes);
				}
				
				String message = "Element " + element + " used these types:";
				for (String used : usedTypes) {
					message +="\n\t" + used;
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
				// processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "DIE COMPILER, DIE!", element);
			}
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

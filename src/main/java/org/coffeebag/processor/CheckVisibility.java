package org.coffeebag.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			
			for (Element element : roundEnv.getRootElements()) {
				final ReferenceFinder finder = new ReferenceFinder(processingEnv, element);
				final Set<String> usedTypes = finder.getTypesUsed();
				System.out.println("Element " + element + " used these types:");
				for (String used : usedTypes) {
					System.out.println("\t" + used);
				}
			}
		}
		// Allow other annotations to be processed
		return false;
	}

}

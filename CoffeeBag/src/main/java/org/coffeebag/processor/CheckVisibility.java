package org.coffeebag.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {

	private Messager messager;

	@Override
	public synchronized void init(ProcessingEnvironment env){
		super.init(env);
		this.messager = env.getMessager();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			for (Element element : roundEnv.getRootElements()) {
				final ReferenceFinder finder = new ReferenceFinder(processingEnv, element);
				final Set<String> usedTypes = finder.getTypesUsed();
				String message = "Element " + element + " used these types:";
				for (String used : usedTypes) {
					message +="\n\t" + used;
				}
				messager.printMessage(Diagnostic.Kind.NOTE, message, element);
				// messager.printMessage(Diagnostic.Kind.ERROR, "DIE COMPILER, DIE!", element);
			}
		}
		// Allow other annotations to be processed
		return false;
	}

}

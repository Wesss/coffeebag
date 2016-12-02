package org.coffeebag.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.util.SimpleElementVisitor8;

import com.sun.source.tree.*;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CheckVisibility extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			final Trees trees = Trees.instance(processingEnv);
			if (trees == null) {
				throw new NullPointerException("Null Trees");
			}
			final Set<? extends Element> elements = roundEnv.getRootElements();
			for (Element element : elements) {
				element.accept(new Visitor(), trees);
			}
		}
		// Allow other annotations to be processed
		return false;
	}

	
	
	private class Visitor extends SimpleElementVisitor8<Void, Trees> {
		
		@Override
		public Void visitPackage(PackageElement e, Trees trees) {
			System.out.println("Visiting package " + e);
			return null;
		}

		@Override
		public Void visitType(TypeElement e, Trees trees) {
			System.out.println("Visiting type " + e);
			final ClassTree tree = trees.getTree(e);
			new ClassReferenceFinder().scan(tree, null);
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Trees trees) {
			if (trees == null) {
				trees = Trees.instance(processingEnv);
			}
			System.out.println("Visiting executable " + e);
			final MethodTree methodTree = trees.getTree(e);
			new ClassReferenceFinder().scan(methodTree, null);
			return null;
		}

		@Override
		public Void visitTypeParameter(TypeParameterElement e, Trees trees) {
			System.out.println("Visiting type parameter " + e);
			return null;
		}
		
	}
}

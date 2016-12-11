package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import static org.truth0.Truth.ASSERT;

public class VisibilityCheckerTest extends AbstractCompilerTest {

	private static final String syntaxErrorCompileExpectation =
			"Syntax Error: line 1 of a VisibilityCheckerTest must contain only \"pass\" or \"fail\"";
	private static final String syntaxErrorTooManyLines = "Syntax Error: line 1 of a VisibilityCheckerTest must " +
			"contain only \"pass\" or \"fail\"";

	private boolean expectPass = true; //pass by default

	public VisibilityCheckerTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws IOException {
		super(sourceFile, referenceFile, testClass);

		// Read expectation from file
		try (final BufferedReader reader = new BufferedReader(new FileReader(referenceFile))) {
			String line;
			int lines = 0;
			while ((line = reader.readLine()) != null) {
				if (lines == 1) {
					throw new RuntimeException(syntaxErrorTooManyLines);
				}
				switch (line) {
					case "pass":
						expectPass = true;
						break;
					case "fail":
						expectPass = false;
						break;
					default:
						throw new RuntimeException(syntaxErrorCompileExpectation);
				}
				lines++;
			}
		}
	}

	@Override
	public void run(CheckVisibility processor) throws Exception {
		File sourceFile = getSource();
		if (sourceFile.isFile()) {
			if (expectPass) {
				ASSERT.about(JavaSourceSubjectFactory.javaSource())
						.that(JavaFileObjects.forResource(getSource().toURI().toURL()))
						.processedWith(processor)
						.compilesWithoutError();
			} else {
				ASSERT.about(JavaSourceSubjectFactory.javaSource())
						.that(JavaFileObjects.forResource(getSource().toURI().toURL()))
						.processedWith(processor)
						.failsToCompile();
			}
		} else {
			File classA = new File(sourceFile.getAbsolutePath() + "/ClassA.java");
			File classB = new File(sourceFile.getAbsolutePath() + "/ClassB.java");

			ArrayList<JavaFileObject> javaFiles = new ArrayList<>();
			javaFiles.add(getJavaFileObject(classA));
			javaFiles.add(getJavaFileObject(classB));

			if (expectPass) {
				ASSERT.about(JavaSourcesSubjectFactory.javaSources())
						.that(javaFiles)
						.processedWith(processor)
						.compilesWithoutError();
			} else {
				ASSERT.about(JavaSourcesSubjectFactory.javaSources())
						.that(javaFiles)
						.processedWith(processor)
						.failsToCompile();
			}
		}
	}

	private JavaFileObject getJavaFileObject(File javaFile) throws MalformedURLException {
		return JavaFileObjects.forResource(javaFile.toURI().toURL());
	}
}

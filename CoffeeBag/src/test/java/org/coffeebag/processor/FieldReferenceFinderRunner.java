package org.coffeebag.processor;

import java.io.File;
import java.io.IOException;

/**
 * Runs {@link FieldReferenceFinderTest} tests
 */
public class FieldReferenceFinderRunner extends AbstractCompilerTestRunner {

	public FieldReferenceFinderRunner(Class<?> testClass) throws IOException {
		super(testClass);
	}

	@Override
	public String getTestPath() {
		return "test-data/FieldReferenceFinder";
	}

	@Override
	public AbstractCompilerTest createTest(File sourceFile, File textFile, Class<?> testClass)
			throws IOException {
		return new FieldReferenceFinderTest(sourceFile, textFile, testClass);
	}
}

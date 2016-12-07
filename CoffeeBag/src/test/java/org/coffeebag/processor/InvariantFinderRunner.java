package org.coffeebag.processor;

import java.io.File;
import java.io.IOException;

/**
 * Runs {@link InvariantFinderTest} tests
 */
public class InvariantFinderRunner extends AbstractCompilerTestRunner {

	public InvariantFinderRunner(Class<?> testClass) throws IOException {
		super(testClass);
	}

	@Override
	public String getTestPath() {
		return "test-data/InvariantFinder";
	}

	@Override
	public AbstractCompilerTest createTest(File sourceFile, File textFile, Class<?> testClass)
			throws IOException {
		return new InvariantFinderTest(sourceFile, textFile, testClass);
	}
}

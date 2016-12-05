package org.coffeebag.processor;

import java.io.File;
import java.io.IOException;

public class ReferenceFinderRunner extends AbstractCompilerTestRunner {

	public ReferenceFinderRunner(Class<?> testClass) throws IOException {
		super(testClass);
	}

	@Override
	public String getTestPath() {
		return "test-data/ReferenceFinder";
	}

	@Override
	public AbstractCompilerTest createTest(File sourceFile, File textFile, Class<?> testClass)
			throws IOException {
		return new ReferenceFinderTest(sourceFile, textFile, testClass);
	}
}

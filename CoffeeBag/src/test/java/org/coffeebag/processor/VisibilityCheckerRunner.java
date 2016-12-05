package org.coffeebag.processor;

import java.io.File;
import java.io.IOException;

public class VisibilityCheckerRunner extends AbstractCompilerTestRunner {

	public VisibilityCheckerRunner(Class<?> testClass) throws IOException {
		super(testClass);
	}

	@Override
	public String getTestPath() {
		return "test-data/VisibilityChecker";
	}

	@Override
	public AbstractCompilerTest createTest(File sourceFile, File textFile, Class<?> testClass)
			throws IOException {
		return new VisibilityCheckerTest(sourceFile, textFile, testClass);
	}
}

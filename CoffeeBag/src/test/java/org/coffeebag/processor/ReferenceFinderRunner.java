package org.coffeebag.processor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Runs tests (read from files) on the ReferenceFinder class
 */
public class ReferenceFinderRunner extends AbstractCompilerTestRunner {

	public ReferenceFinderRunner(Class<?> testClass) throws FileNotFoundException, IOException {
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

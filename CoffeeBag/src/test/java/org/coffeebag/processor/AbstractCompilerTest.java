package org.coffeebag.processor;

import java.io.File;

import org.junit.runner.Description;

/**
 * This represents a single compilation test to be run
 */
public abstract class AbstractCompilerTest {
	/**
	 * A description of this test
	 */
	private final Description description;
	/**
	 * The source file to read
	 */
	private final File source;

	/**
	 * Creates a test
	 * @param sourceFile the Java source file to compile
	 * @param referenceFile a file with information on the expected results
	 * @param testClass the test class being executed
	 */
	public AbstractCompilerTest(File sourceFile, File referenceFile, Class<?> testClass) {
		this.source = sourceFile;

		final String testName = sourceFile.getName().replaceFirst("\\.java$", "");
		this.description = Description.createTestDescription(testClass, testName);
	}

	/**
	 * Runs the test
	 * @throws Exception if the test fails or an error occurs
	 */
	public abstract void run(CheckVisibility processor) throws Exception;

	/**
	 * Returns a description of this test
	 * @return a description
	 */
	public Description getDescription() {
		return description;
	}

	/**
	 * Returns the source file that this test compiles
	 * @return the source file
	 */
	public File getSource() {
		return source;
	}
}

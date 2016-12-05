package org.coffeebag.processor;

import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

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

	public AbstractCompilerTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws IOException {
		this.source = sourceFile;

		final String testName = sourceFile.getName().replaceFirst("\\.java$", "");
		this.description = Description.createTestDescription(testClass, testName);
	}

	/**
	 * run the test
	 */
	public abstract void run() throws MalformedURLException;

	public Description getDescription() {
		return description;
	}

	public File getSource() {
		return source;
	}
}

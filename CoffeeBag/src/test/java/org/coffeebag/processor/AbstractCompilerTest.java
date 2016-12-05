package org.coffeebag.processor;

import org.junit.runner.Description;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Information about a test
 */
public abstract class AbstractCompilerTest {
	/**
	 * A description of this test
	 */
	public final Description description;
	/**
	 * The source file to read
	 */
	public final File source;
	/**
	 * The names of the classes referenced in the code
	 */
	public final Set<String> referencedTypes;

	public AbstractCompilerTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws FileNotFoundException, IOException {
		this.source = sourceFile;

		final String testName = sourceFile.getName().replaceFirst("\\.java$", "");
		this.description = Description.createTestDescription(testClass, testName);

		// Read type names from file
		this.referencedTypes = new HashSet<>();
		try (final BufferedReader reader = new BufferedReader(new FileReader(referenceFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				this.referencedTypes.add(line);
			}
		}
	}

	/**
	 *
	 */
	public abstract void run() throws MalformedURLException;
}

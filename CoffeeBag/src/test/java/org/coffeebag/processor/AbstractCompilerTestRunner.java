package org.coffeebag.processor;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO support for compiling multiple files in a package structure
 *
 * A compilerTestRunner represents a suite of tests to be run against a compiler.
 *
 * This test runner will scan the directory defined by getTestPath() for pairs of files:
 * .java files (for compilation) and .txt files (for expectation). It will then instantiate
 * a {@link AbstractCompilerTest} object and run it as part of a test suite.
 */
public abstract class AbstractCompilerTestRunner extends Runner {

	/**
	 * The description of this group of tests, containing the description of each test
	 */
	private Description description;
	/**
	 * The tests to run
	 */
	private final List<AbstractCompilerTest> tests;

	/**
	 * Creates a test runner
	 * @param testClass the test class being executed
	 * @throws IOException if a file could not be read
	 */
	public AbstractCompilerTestRunner(Class<?> testClass) throws IOException {
		// Find test files
		tests = new ArrayList<>();
		final File dataDir = new File(getTestPath());
		final FileFilter javaFilter = pathname -> pathname.getName().endsWith(".java");
		for (File sourceFile : dataDir.listFiles(javaFilter)) {
			// Find the corresponding text file
			final File textFile = new File(sourceFile.getAbsolutePath().replaceFirst("\\.java$", ".txt"));
			if (textFile.exists()) {
				tests.add(createTest(sourceFile, textFile, testClass));
			}
		}

		// generate description
		description = Description.createSuiteDescription(testClass);
		for (AbstractCompilerTest test : tests) {
			description.addChild(test.getDescription());
		}
	}

	/**
	 * @return the path to the testfiles relative to the root directory
	 */
	public abstract String getTestPath();

	/**
	 * Create a test to run in the test suite
	 * @param sourceFile the .java source file to compile
	 * @param textFile the .txt file that contains expectations
	 * @return a test to be run against the compiler
	 */
	public abstract AbstractCompilerTest createTest(File sourceFile, File textFile, Class<?> testClass) throws IOException;

	@Override
	public Description getDescription() {
		return description;
	}

	/**
	 * Runs the tests
	 */
	@Override
	public void run(RunNotifier notifier) {
		for (AbstractCompilerTest test : tests) {
			notifier.fireTestStarted(test.getDescription());
			try {
				test.run(CheckVisibility.testMode());
			} catch (Exception e) {
				notifier.fireTestFailure(new Failure(test.getDescription(), e));
			}
			notifier.fireTestFinished(test.getDescription());
		}
	}
}

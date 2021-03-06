package org.coffeebag.processor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
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
	 * Tests with missing source files
	 */
	private final List<File> incompleteTests;
	private final Class<?> testClass;

	/**
	 * Creates a test runner
	 * @param testClass the test class being executed
	 * @throws IOException if a file could not be read
	 */
	public AbstractCompilerTestRunner(Class<?> testClass) throws IOException {
		// Find test files (as represented by .txt files)
		tests = new ArrayList<>();
		incompleteTests = new ArrayList<>();
		this.testClass = testClass;

		final File dataDir = new File(getTestPath());
		final FileFilter txtFilter = pathname -> pathname.getName().endsWith(".txt");
		for (File textFile : dataDir.listFiles(txtFilter)) {
			// Find the corresponding file(s) to compile
			File sourceFile = new File(textFile.getAbsolutePath().replaceFirst("\\.txt$", ".java"));
			if (!sourceFile.exists()) {
				// if .java file does not exists, pass in source directory with same name
				sourceFile = new File(textFile.getAbsolutePath().replaceFirst("\\.txt$", ""));
			}
			if (sourceFile.exists()) {
				tests.add(createTest(sourceFile, textFile, testClass));
			} else {
				incompleteTests.add(textFile);
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
		for (File incompleteTest : incompleteTests) {
			String testName = incompleteTest.getName().replaceFirst("\\.txt$", "");
			Description description = Description.createTestDescription(testClass, testName);
			notifier.fireTestFailure(new Failure(description, new AssertionError("Source file missing")));
		}
		for (AbstractCompilerTest test : tests) {
			System.out.println("[CompilerTestRunner] ---- Starting test " + test.getDescription().getDisplayName() + " ----");
			notifier.fireTestStarted(test.getDescription());
			try {
				final CheckVisibility processor = new CheckVisibility(true);
				test.run(processor);
			} catch (Throwable e) {
				notifier.fireTestFailure(new Failure(test.getDescription(), e));
			}
			notifier.fireTestFinished(test.getDescription());
			System.out.println("[CompilerTestRunner] ---- Finished test " + test.getDescription().getDisplayName() + " ----");
		}
	}
}

package org.coffeebag.processor;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompilerTestRunner extends Runner {
	/**
	 * The test class being executed
	 */
	private final Class<?> testClass;

	/**
	 * The tests to run
	 */
	private final List<AbstractCompilerTest> tests;

	public AbstractCompilerTestRunner(Class<?> testClass) throws FileNotFoundException, IOException {
		this.testClass = testClass;

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
	}

	/**
	 * TODO
	 * @return
	 */
	public abstract String getTestPath();

	/**
	 * TODO
	 * @param sourceFile
	 * @param textFile
	 * @return
	 */
	public abstract AbstractCompilerTest createTest(File sourceFile, File textFile, Class<?> testClass) throws IOException;

	@Override
	public Description getDescription() {
		final Description top = Description.createSuiteDescription(testClass);
		for (AbstractCompilerTest test : tests) {
			top.addChild(test.description);
		}
		return top;
	}

	@Override
	public void run(RunNotifier notifier) {
		for (AbstractCompilerTest test : tests) {
			notifier.fireTestStarted(test.description);
			try {
				test.run();
			} catch (Exception e) {
				notifier.fireTestFailure(new Failure(test.description, e));
			}
			notifier.fireTestFinished(test.description);
		}
	}
}

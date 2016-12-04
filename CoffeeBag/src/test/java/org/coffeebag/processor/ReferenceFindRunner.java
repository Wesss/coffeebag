package org.coffeebag.processor;

import static org.truth0.Truth.ASSERT;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coffeebag.processor.CheckVisibility;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

/**
 * Runs tests (read from files) on the ReferenceFinder class
 */
public class ReferenceFindRunner extends Runner {
	
	/**
	 * The test class being executed
	 */
	private final Class<?> testClass;
	
	/**
	 * The tests to run
	 */
	private final List<TestInfo> tests;
	
	public ReferenceFindRunner(Class<?> testClass) throws FileNotFoundException, IOException {
		this.testClass = testClass;
		
		// Find test files
		tests = new ArrayList<>();
		final File dataDir = new File("test-data/ReferenceFinder");
		final FileFilter javaFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".java");
			}
		};
		for (File sourceFile : dataDir.listFiles(javaFilter)) {
			// Find the corresponding text file
			final File textFile = new File(sourceFile.getAbsolutePath().replaceFirst("\\.java$", ".txt"));
			if (textFile.exists()) {
				final TestInfo test = new TestInfo(sourceFile, textFile);
				tests.add(test);
			}
		}
	}

	@Override
	public Description getDescription() {
		final Description top = Description.createSuiteDescription(testClass);
		for (TestInfo test : tests) {
			top.addChild(test.description);
		}
		return top;
	}

	@Override
	public void run(RunNotifier notifier) {
		for (TestInfo test : tests) {
			notifier.fireTestStarted(test.description);
			try {
				test.run();
			} catch (Exception e) {
				notifier.fireTestFailure(new Failure(test.description, e));
			}
			notifier.fireTestFinished(test.description);
		}
	}

	/**
	 * Information about a test
	 */
	private class TestInfo {
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
		
		public TestInfo(File sourceFile, File referenceFile) throws FileNotFoundException, IOException {
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
		 * Runs this test
		 * @throws MalformedURLException 
		 */
		public void run() throws MalformedURLException {
			final CheckVisibility processor = CheckVisibility.testMode();
			ASSERT.about(JavaSourceSubjectFactory.javaSource())
			.that(JavaFileObjects.forResource(source.toURI().toURL()))
			.processedWith(processor)
			.compilesWithoutError();
			
			// Check detected references
			// 1. Flatten into one set
			final Set<String> actualReferences = new HashSet<>();
			for (Set<String> typeReferences : processor.getTypeReferences().values()) {
				actualReferences.addAll(typeReferences);
			}
			
			// Check
			assertEquals("Incorrect references found in test " + this.description, this.referencedTypes, actualReferences);
		}
	}
}

package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.truth0.Truth.ASSERT;

public class ReferenceFinderTest extends AbstractCompilerTest {

	public ReferenceFinderTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws FileNotFoundException, IOException {
		super(sourceFile, referenceFile, testClass);
	}

	/**
	 * Runs this test
	 * @throws MalformedURLException
	 */
	@Override
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
		assertEquals("Incorrect references found in test " + this.description,this.referencedTypes, actualReferences);
	}
}

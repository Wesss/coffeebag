package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.truth0.Truth.ASSERT;

public class ReferenceFinderTest extends AbstractCompilerTest {
	/**
	 * The names of the classes referenced in the code
	 */
	public final Set<String> referencedTypes;

	public ReferenceFinderTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws IOException {
		super(sourceFile, referenceFile, testClass);

		// Read type names from file
		this.referencedTypes = new HashSet<>();
		try (final BufferedReader reader = new BufferedReader(new FileReader(referenceFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				this.referencedTypes.add(line);
			}
		}
	}

	@Override
	public void run(CheckVisibility processor) throws MalformedURLException {
		ASSERT.about(JavaSourceSubjectFactory.javaSource())
				.that(JavaFileObjects.forResource(getSource().toURI().toURL()))
				.processedWith(processor)
				.compilesWithoutError();

		// Check detected references
		// 1. Flatten into one set
		final Set<String> actualReferences = new HashSet<>();
		for (Set<String> typeReferences : processor.getTypeReferences().values()) {
			actualReferences.addAll(typeReferences);
		}

		// Check
		assertEquals("Incorrect references found in test " + this.getDescription(),
				this.referencedTypes, actualReferences);
	}
}

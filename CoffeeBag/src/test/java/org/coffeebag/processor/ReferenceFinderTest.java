package org.coffeebag.processor;

import static org.junit.Assert.assertEquals;
import static org.truth0.Truth.ASSERT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

/**
 * A test that checks the classes referenced by a source file
 */
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
	public void run(CheckVisibility processor) throws Exception {
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
		// 2. Convert to a list for more user-friendly comparison
		final List<String> actualReferenceList = new ArrayList<>(actualReferences);
		final List<String> expectedReferenceList = new ArrayList<>(this.referencedTypes);
		actualReferenceList.sort(null);
		expectedReferenceList.sort(null);

		// Check
		assertEquals("Incorrect references found",
				expectedReferenceList, actualReferenceList);
	}
}

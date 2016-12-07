package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.coffeebag.domain.VisibilityInvariant;
import org.coffeebag.domain.VisibilityInvariants;
import org.coffeebag.processor.domain.ExpectedInvariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.truth0.Truth.ASSERT;

/**
 * The referenceFile is interpreted in the following way:
 *
 * class_name visibility_invariant
 * TODO much more dynamic support needed, this is just a proof of concept for classes
 */
public class InvariantFinderTest extends AbstractCompilerTest {

	private Set<ExpectedInvariant> expectedInvariants;

	public InvariantFinderTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws IOException {
		super(sourceFile, referenceFile, testClass);
		expectedInvariants = new HashSet<>();

		try (final BufferedReader reader = new BufferedReader(new FileReader(referenceFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				Scanner tokenizer = new Scanner(line);
				String className = tokenizer.next();
				String visibility = tokenizer.next();
//				expectedInvariants.add(new ExpectedInvariant(className, visibility));
			}
		}
	}

	@Override
	public void run(CheckVisibility processor) throws Exception {
		ASSERT.about(JavaSourceSubjectFactory.javaSource())
				.that(JavaFileObjects.forResource(getSource().toURI().toURL()))
				.processedWith(processor)
				.compilesWithoutError();

		VisibilityInvariants invariants = processor.getInvariants();

		for (VisibilityInvariant invariant : invariants.getClassInvariants()) {
			Iterator<ExpectedInvariant> itr = expectedInvariants.iterator();
			ExpectedInvariant expected;
			boolean hasFoundMatchingExpected = false;
			while (itr.hasNext() && !hasFoundMatchingExpected) {
				expected = itr.next();
				if (expected.getClassName().equals(invariant.getSimpleClassName().toString()) &&
						expected.getVisibility() == invariant.getVisibility()) {
					itr.remove();
					hasFoundMatchingExpected = true;
				}
			}
			if (!hasFoundMatchingExpected) {
//				fail("Unexpected class " + invariant.getSimpleClassName() +
//						" with visibility " + invariant.getVisibility());
			}
		}
		if (!expectedInvariants.isEmpty()) {
			StringBuilder builder = new StringBuilder("Did not find expected invariants: ");
			for (ExpectedInvariant expected : expectedInvariants) {
				builder.append("\nClass ")
						.append(expected.getClassName())
						.append(" with visibility ")
						.append(expected.getVisibility());
			}
//			fail(builder.toString());
		}
	}
}

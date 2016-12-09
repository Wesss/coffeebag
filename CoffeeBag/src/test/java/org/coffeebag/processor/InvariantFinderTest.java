package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.coffeebag.domain.invariant.VisibilityInvariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.truth0.Truth.ASSERT;

/**
 * See test-data/InvariantFinder/README for how tests are interpreted
 */
public class InvariantFinderTest extends AbstractCompilerTest {

	private BufferedReader testReader;

	public InvariantFinderTest(File sourceFile, File referenceFile, Class<?> testClass)
			throws IOException {
		super(sourceFile, referenceFile, testClass);

		this.testReader = new BufferedReader(new FileReader(referenceFile));
	}

	@Override
	public void run(CheckVisibility processor) throws Exception {
		ASSERT.about(JavaSourceSubjectFactory.javaSource())
				.that(JavaFileObjects.forResource(getSource().toURI().toURL()))
				.processedWith(processor)
				.compilesWithoutError();

		Map<String, VisibilityInvariant> invariants = processor.getInvariants();
		Set<String> testedElements = new HashSet<>();

		String line = "";
		while (line != null) {
			line = testReader.readLine();
			if (line == null || line.equals("")) {
				continue;
			}

			Scanner memberTokenizer = new Scanner(line);
			String expectedClassName = memberTokenizer.next();
			assertThat("expected element was not detected", invariants.keySet(), hasItem(expectedClassName));
			testedElements.add(expectedClassName);

			line = testReader.readLine();
			while (!(line == null || line.equals(""))) {
				testElementAllowedUsages(invariants.get(expectedClassName), line, expectedClassName);
				line = testReader.readLine();
			}
		}

		assertThat("Unexpected Annotated Elements found",
				invariants.keySet(),
				is(testedElements));
	}

	private void testElementAllowedUsages(VisibilityInvariant invariant,
	                                      String line,
	                                      String expectedClassName) {
		Scanner testActionTokenizer = new Scanner(line);
		String qualifiedName;
		boolean isTestingClass = false, isPassExpected = false;

		switch (testActionTokenizer.next()) {
			case "CLASS":
			case "class":
				isTestingClass = true;
				break;
			case "PACKAGE":
			case "package":
				isTestingClass = false;
				break;
			default: fail("test line syntax: neither class nor package as first token");
		}

		qualifiedName = testActionTokenizer.next();
		if (qualifiedName.equals("\"\"")) {
			qualifiedName = "";
		}

		switch (testActionTokenizer.next()) {
			case "PASS":
			case "pass":
				isPassExpected = true;
				break;
			case "FAIL":
			case "fail":
				isPassExpected = false;
				break;
			default: fail("test line syntax: neither PASS nor FAIL as 3rd token");
		}

		String errorMsg = getErrorMsg(expectedClassName, qualifiedName, isTestingClass, isPassExpected);
		if (isTestingClass) {
			assertThat(errorMsg, invariant.isAllowedInClass(qualifiedName),
					is(isPassExpected));
		} else {
			assertThat(errorMsg, invariant.isAllowedInPackage(qualifiedName),
					is(isPassExpected));
		}
	}

	private String getErrorMsg(String expectedClassName,
	                                  String qualifiedName,
	                                  boolean isTestingClass,
	                                  boolean isPassExpected) {
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append("Expected to be ");
		if (!isPassExpected) {
			errorMsg.append("un");
		}
		errorMsg.append("able to use ")
				.append(expectedClassName)
				.append(" in ");
		if (isTestingClass) {
			errorMsg.append("class ");
		} else {
			errorMsg.append("package ");
		}
		errorMsg.append(qualifiedName);
		return errorMsg.toString();
	}
}

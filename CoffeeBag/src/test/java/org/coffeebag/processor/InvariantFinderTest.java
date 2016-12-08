package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.coffeebag.domain.VisibilityInvariant;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
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

		Elements elementUtils = processor.getElementUtils();
		Map<Element, VisibilityInvariant> invariants = processor.getInvariants();
		Set<Element> testedElements = new HashSet<>();

		String line = "";
		while (line != null) {
			line = testReader.readLine();
			if (line == null || line.equals("")) {
				continue;
			}

			Scanner memberTokenizer = new Scanner(line);
			String className = memberTokenizer.next();
			TypeElement element = elementUtils.getTypeElement(className);
			assertThat(invariants.keySet(), contains(element));
			testedElements.add(element);

			line = testReader.readLine();
			while (!(line == null || line.equals(""))) {
				testElementAllowedUsages(invariants, line, element);
				line = testReader.readLine();
			}
		}

		assertThat("Unexpected Annotated Elements found",
				invariants.keySet(),
				is(testedElements));
	}

	private void testElementAllowedUsages(Map<Element, VisibilityInvariant> invariants, String line, TypeElement element) {
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

		if (isTestingClass) {
			assertThat(invariants.get(element).isAllowedInClass(qualifiedName),
					is(isPassExpected));
		} else {
			assertThat(invariants.get(element).isAllowedInPackage(qualifiedName),
					is(isPassExpected));
		}
	}
}

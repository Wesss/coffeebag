package org.coffeebag.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.coffeebag.domain.invariant.VisibilityInvariant;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
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
import static org.mockito.Mockito.*;
import static org.truth0.Truth.ASSERT;

/**
 * See test-data/InvariantFinder/README for how tests are interpreted
 */
public class InvariantFinderTest extends AbstractCompilerTest {

	private BufferedReader testReader;

	public InvariantFinderTest(File sourceFile, File referenceFile, Class<?> testClass) throws IOException {
		super(sourceFile, referenceFile, testClass);

		this.testReader = new BufferedReader(new FileReader(referenceFile));
	}

	@Override
	public void run(CheckVisibility processor) throws Exception {
		ASSERT.about(JavaSourceSubjectFactory.javaSource())
				.that(JavaFileObjects.forResource(getSource().toURI().toURL())).processedWith(processor)
				.compilesWithoutError();

		Map<String, VisibilityInvariant> invariants = processor.getInvariants();
		Set<String> testedElements = new HashSet<>();

		String line = testReader.readLine();
		while (line != null) {
			if (line.equals("")) {
				continue;
			}

			try (Scanner memberTokenizer = new Scanner(line)) {
				String expectedClassName = memberTokenizer.next();
				assertThat("expected element was not detected", invariants.keySet(), hasItem(expectedClassName));
				testedElements.add(expectedClassName);

				line = testReader.readLine();
				while (!(line == null || line.equals(""))) {
					testElementAllowedUsages(invariants.get(expectedClassName), line, expectedClassName);
					line = testReader.readLine();
				}
				line = testReader.readLine();
			}
		}

		assertThat("Unexpected Annotated Elements found", invariants.keySet(), is(testedElements));
	}

	private void testElementAllowedUsages(VisibilityInvariant invariant,
	                                      String line,
	                                      String testedClassName) {
		try (Scanner testActionTokenizer = new Scanner(line)) {
			String packageName, simpleClassName;
			boolean isSubclass = false;
			boolean isPassExpected = false;

			packageName = testActionTokenizer.next();
			if (packageName.equals("\"\"")) {
				packageName = "";
			}
			simpleClassName = testActionTokenizer.next();
			if (simpleClassName.contains("<subclass>")) {
				isSubclass = true;
				simpleClassName = simpleClassName.replace("<subclass>", "");
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
				default:
					fail("test line syntax: neither PASS nor FAIL as 3rd token");
			}

			TypeElement mockElement = getMockElement(packageName, simpleClassName, isSubclass);
			String errorMsg = getErrorMsg(testedClassName, packageName, simpleClassName,
					isSubclass, isPassExpected);

			assertThat(errorMsg, invariant.isUsageAllowedIn(mockElement), is(isPassExpected));
		}
	}

	private TypeElement getMockElement(String packageName,
	                                   String simpleClassName,
	                                   boolean isSubclass) {
		TypeElement mockElement = mock(TypeElement.class);
		Name mockName = mock(Name.class);
		when(mockName.toString()).thenReturn(getQualifiedName(packageName, simpleClassName));
		when(mockElement.getQualifiedName()).thenReturn(mockName);

		return mockElement;
	}

	private String getErrorMsg(String testedClassName,
	                           String usingPackageName,
	                           String usingClassName,
	                           boolean isPassExpected,
	                           boolean isSubclass) {
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append("Expected to be ");
		if (!isPassExpected) {
			errorMsg.append("un");
		}
		errorMsg.append("able to use ").append(testedClassName).append(" from within ")
				.append(getQualifiedName(usingPackageName, usingClassName));
		if (isSubclass) {
			errorMsg.append(" extends ")
					.append(testedClassName);
		}
		return errorMsg.toString();
	}

	private String getQualifiedName(String packageName, String simpleClassName) {
		String mockStringName;
		if (packageName.equals("")) {
			mockStringName = simpleClassName;
		} else {
			mockStringName = packageName + "." + simpleClassName;
		}
		return mockStringName;
	}
}

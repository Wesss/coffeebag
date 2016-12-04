package org.coffeebag.processor;

import static org.truth0.Truth.ASSERT;

import org.junit.Test;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class ReferenceFinderTest {

	@Test
	public void test() {
		ASSERT.about(JavaSourceSubjectFactory.javaSource())
			.that(JavaFileObjects.forSourceString("TestClass", "public class TestClass {  }"))
			.compilesWithoutError();
	}

}

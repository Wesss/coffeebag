package com.example.company;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

@Access(level = Visibility.PUBLIC)
public class ClassB {

	private static ClassB lastCreated;

	public ClassB() {
		lastCreated = this;
	}
}

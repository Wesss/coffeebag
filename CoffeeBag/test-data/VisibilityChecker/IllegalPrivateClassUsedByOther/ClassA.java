package org.example;

import  org.other.ClassB;

public class ClassA {

	private ClassB myB;

	public ClassA() {
		ClassB localB = new ClassB();
		myB = localB;
	}
}


package org.example;

import  org.example.ClassB;

public class ClassA {

	private ClassB myB;

	public ClassA() {
		ClassB localB = new ClassB();
		myB = localB;
	}
}


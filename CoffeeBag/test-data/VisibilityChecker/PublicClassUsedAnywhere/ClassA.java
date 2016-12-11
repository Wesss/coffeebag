package org.local.util;

import  com.example.company.ClassB;

public class ClassA {

	private ClassB myB;

	public ClassA() {
		ClassB localB = new ClassB();
		myB = localB;
	}
}


package com.example.company;

import org.local.util.ClassA;

public class ClassB extends ClassA {

	private static ClassA someReference;

	public ClassB() {
		someReference = new ClassA();
	}
}

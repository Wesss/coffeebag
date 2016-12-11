package org.coffeebag.TestUser.p2;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * Demonstrates that a static method and a static field can have the same name
 * 
 * Also provides a private class in another package
 */
@Access(level = Visibility.PRIVATE)
public class Collision {

	@Access(level = Visibility.PRIVATE)
	public static int name;

	@Access(level = Visibility.PRIVATE)
	public static void name() {
		
	}
}

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

	public static int name;
	
	public static void name() {
		
	}
}

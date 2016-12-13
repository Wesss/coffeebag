package org.local.util;

import org.coffeebag.annotations.Visibility;
import org.coffeebag.annotations.Access;

public class ClassA {

	@Access(level = Visibility.SUBCLASS)
	public int x;

}

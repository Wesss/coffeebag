package org.library.detail;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * This is the relatively public interface of an implementation detail.
 * 
 * PublicInterface can access this class, but client code in other packages cannot.
 * 
 * CoffeeBag allows detail packages to be nested, which is not possible with the standard Java visibility options.
 */
@Access(level = Visibility.SCOPED, scope = "org.library")
public class DetailInterface {
	
	/**
	 * Use an implementation detail
	 */
	private Detail detail;
}

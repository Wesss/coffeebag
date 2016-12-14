package org.library;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;
import org.library.detail.DetailInterface;

/**
 * The public interface to this library
 * 
 * This is the only class in this library that client code can access.
 */
@Access(level = Visibility.PUBLIC)
public class PublicInterface {

	/**
	 * Use the interface of an implementation detail
	 */
	private DetailInterface detail;
	
	/**
	 * The detail class, which is private, cannot be accessed here
	 */
//	private Detail innerDetail;
}

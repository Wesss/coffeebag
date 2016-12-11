package org.coffeebag.domain.invariant;

import org.coffeebag.annotations.Access;
import org.coffeebag.log.Log;

import javax.lang.model.element.TypeElement;

public class VisibilityInvariantFactory {

	private static String TAG = VisibilityInvariantFactory.class.getSimpleName();

	// TODO add testmode to this class to enable generating mock subclasses?

	/**
	 * @requires element has an Access annotation
	 */
	public static VisibilityInvariant getInvariant(TypeElement element) {
		Access annotation = element.getAnnotation(Access.class);
		switch (annotation.level()) {
			case PUBLIC:
				return new PublicVisibilityInvariant();
			case PRIVATE:
				return new PrivateVisibilityInvariant(element.getQualifiedName().toString());
			default:
				Log.d(TAG, "Unsupported visibility " + annotation.level());
				return null;
		}
	}
}

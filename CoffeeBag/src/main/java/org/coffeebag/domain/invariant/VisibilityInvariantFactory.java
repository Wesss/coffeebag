package org.coffeebag.domain.invariant;

public class VisibilityInvariantFactory {

	public static VisibilityInvariant getPublicInvariant() {
		//TODO
		return new PublicVisibilityInvariant();
	}

	public static VisibilityInvariant getPrivateInvariant(String fullPackageName, String qualifiedClassName) {
		//TODO
		return new PrivateVisibilityInvariant(fullPackageName, qualifiedClassName);
	}
}

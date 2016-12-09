package org.coffeebag.processor.invariants;

import org.coffeebag.annotations.Visibility;
import org.coffeebag.log.Log;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;

/**
 * Scans the element tree to look for annotated elements
 */
public class InvariantScanner extends ElementScanner8<Void, Void> {

	private static String TAG = InvariantScanner.class.getSimpleName();

	private InvariantFinder invariantFinder;

	public InvariantScanner(InvariantFinder invariantFinder) {
		this.invariantFinder = invariantFinder;
	}

	@Override
	public Void visitType(TypeElement e, Void p) {
		Log.d(TAG, "Visiting type " + e);
		invariantFinder.storeInvariant(e, Visibility.PUBLIC); //TODO
		return super.visitType(e, p);
	}
}

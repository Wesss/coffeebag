package org.coffeebag.processor;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Name;

import com.sun.source.tree.ImportTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

/**
 * Processes Java source and returns the set of class names that it refers to
 * @author Sam Crow
 *
 */
public class ClassReferenceFinder extends TreeScanner<Set<Name>, Void> {

	@Override
	public Set<Name> reduce(Set<Name> refs1, Set<Name> refs2) {
		if (refs1 != null && refs2 != null) {
			refs1.addAll(refs2);
			return refs1;
		} else if (refs1 != null && refs2 == null) {
			return refs1;
		} else if (refs1 == null && refs2 != null) {
			return refs2;
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public Set<Name> visitImport(ImportTree importTree, Void arg1) {
		System.out.println("Visiting import" + importTree);
		return null;
	}

	@Override
	public Set<Name> visitTypeCast(TypeCastTree cast, Void arg1) {
		System.out.println("Visiting cast to " + cast.getType());
		return null;
	}

	@Override
	public Set<Name> visitTypeParameter(TypeParameterTree typeParam, Void arg1) {
		System.out.println("Visiting type parameter " + typeParam);
		return null;
	}

	@Override
	public Set<Name> visitVariable(VariableTree var, Void arg1) {
		System.out.println("Visiting variable " + var);
		return null;
	}

	
}

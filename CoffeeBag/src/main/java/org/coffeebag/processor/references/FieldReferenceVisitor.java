package org.coffeebag.processor.references;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coffeebag.domain.AccessElement;
import org.coffeebag.log.Log;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

/**
 * A tree scanner that detects usage of fields
 */
class FieldReferenceVisitor extends TreeScanner<Void, Void> {
	
	private static final String TAG = FieldReferenceVisitor.class.getSimpleName();
	
	/**
	 * The type resolver
	 */
	private final TypeResolver resolver;
	
	/**
	 * The fields that have been referenced in the scanned code
	 */
	private final Set<AccessElement> referencedFields;
	
	/**
	 * The block scopes available in the current processing situation
	 * 
	 * Each entry is a map from a variable name to a canonical type name
	 * 
	 * The deepest scope contains the fields of the current type
	 */
	private final Deque<Map<String, String>> scopes;

	public FieldReferenceVisitor(TypeResolver resolver) {
		this.resolver = resolver;
		this.referencedFields = new HashSet<>();
		this.scopes = new ArrayDeque<>();
		// Push a scope for the fields
		this.scopes.push(new HashMap<>());
	}

	@Override
	public Void visitBlock(BlockTree arg0, Void arg1) {
		Log.d(TAG, "Entering block " + arg0);
		scopes.push(new HashMap<>());
		super.visitBlock(arg0, arg1);
		scopes.pop();
		Log.d(TAG, "Exiting block");
		return null;
	}
	
	

	@Override
	public Void visitVariable(VariableTree arg0, Void arg1) {
		if (!scopes.isEmpty()) {
			Log.d(TAG, "Visiting variable " + arg0);
			final String typeName = resolver.resolveUnqualifiedType(arg0.getType().toString());
			final String varName = arg0.getName().toString();
			Log.d(TAG, "Variable " + varName + " has type " + typeName);
			scopes.getFirst().put(varName, typeName);
		}
		return super.visitVariable(arg0, arg1);
	}

	@Override
	public Void visitMemberSelect(MemberSelectTree arg0, Void arg1) {
		if (!scopes.isEmpty()) {
			// TODO: Nested fields (a.b.c)
			final ExpressionTree variable = arg0.getExpression();
			final String fieldName = arg0.getExpression().toString();
			final String varType = resolveVariable(variable.toString());
			if (varType != null) {
				final AccessElement accessElement = AccessElement.field(varType, fieldName);
				Log.d(TAG, "Used " + accessElement);
				referencedFields.add(accessElement);
			} else {
				Log.i(TAG, "Variable " + variable + " not resolved");
			}
			
		}
		return super.visitMemberSelect(arg0, arg1);
	}
	
	
	
	/**
	 * Resolves a variable in the current scopes and returns its canonical type name
	 * @param name the variable name
	 * @return the canonical type of the variable, or null if none could be found
	 */
	private String resolveVariable(String name) {
		// Iterate from beginning (top) to end (bottom)
		for (Map<String, String> blockScope : scopes) {
			for (Map.Entry<String, String> entry : blockScope.entrySet()) {
				if (name.equals(entry.getKey())) {
					// Variable found
					return entry.getValue();
				}
			}
		}
		return null;
	}

	public Set<AccessElement> getReferencedFields() {
		return Collections.unmodifiableSet(referencedFields);
	}
	
}

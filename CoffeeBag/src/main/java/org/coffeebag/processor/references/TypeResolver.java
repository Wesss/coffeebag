package org.coffeebag.processor.references;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import org.coffeebag.domain.Import;
import org.coffeebag.domain.Import.ImportType;
import org.coffeebag.log.Log;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * Resolves unqualified types based on imports
 */
public class TypeResolver {
	private static final String TAG = TypeResolver.class.getSimpleName();
	
	/**
	 * The processing environment
	 */
	private final ProcessingEnvironment env;
	/**
	 * The imports in the file being processed
	 */
	private final Set<Import> imports;

	/**
	 * Creates a TypeResolver that will use the imports available in the provided element
	 * @param env the processing environment
	 * @param element the element to get imports from
	 */
	public TypeResolver(ProcessingEnvironment env, Element element) {
		final Trees trees = Trees.instance(env);
		final TreePath elementPath = trees.getPath(element);
		final CompilationUnitTree compilationUnit = elementPath.getCompilationUnit();
		
		// Inspect imports
		final Set<Import> parsedImports = new HashSet<>();
		final List<? extends ImportTree> imports = compilationUnit.getImports();
		
		for (ImportTree importTree : imports) {
			final Tree qualifiedId = importTree.getQualifiedIdentifier();
			Log.d(TAG, "Import tree kind: " + importTree.getKind());
			Log.d(TAG, "Import tree qualified ID kind: " + qualifiedId.getKind());
			if (qualifiedId.getKind().equals(Tree.Kind.MEMBER_SELECT)) {
				final MemberSelectTree idReference = (MemberSelectTree) qualifiedId;
				if (idReference.getIdentifier().contentEquals("*")) {
					// Glob import
					Log.d(TAG, "Glob import of package " + idReference.getExpression());
					parsedImports.add(new Import(ImportType.GLOB, idReference.getExpression().toString()));
				} else {
					// Single-class import
					Log.d(TAG, "Single-class import of " + qualifiedId);
					parsedImports.add(new Import(ImportType.TYPE, qualifiedId.toString()));
				}
			}
		}
		
		// Add an implicit import of java.lang
		parsedImports.add(new Import(ImportType.GLOB, "java.lang"));
		
		this.env = env;
		this.imports = parsedImports;
	}
	
	/**
	 * Attempts to resolve an unqualified type name based on the imports in the
	 * file
	 * 
	 * @param unqualifiedName
	 *            the unqualified name
	 * @param currentPackage the name of the package where the type is referenced from (used to resolve references
	 * to classes in the same package)
	 * @return the fully-qualified name of the type, or null if the name could
	 *         not be resolved
	 */
	public String resolveUnqualifiedType(String unqualifiedName, String currentPackage) {
		Objects.requireNonNull(unqualifiedName);
		Objects.requireNonNull(currentPackage);
		Log.v(TAG, "resolveUnqualifiedType(" + unqualifiedName + " in " + currentPackage + ")");
		
		// Check current package
		final String packagePrefix = currentPackage.isEmpty() ? "" : currentPackage + ".";
		final String inCurrentPackage = packagePrefix + unqualifiedName;
		if (env.getElementUtils().getTypeElement(inCurrentPackage) != null) {
			return inCurrentPackage;
		}
		
		if (unqualifiedName.endsWith("[]")) {
			// An array type
			// Resolve the version without the array, then put it back
			final String elementType = resolveUnqualifiedType(unqualifiedName.substring(0, unqualifiedName.length() - 2), currentPackage);
			if (elementType != null) {
				return elementType + "[]";
			} else {
				return null;
			}
		}
		
		// Remove template arguments
		final int templateStartIndex = unqualifiedName.indexOf('<');
		if (templateStartIndex != -1) {
			final String withoutTemplateArgs = unqualifiedName.substring(0, templateStartIndex);
			// Do not put template arguments back
			return resolveUnqualifiedType(withoutTemplateArgs, currentPackage);
		}
		
		for (Import anImport : imports) {
			Log.d(TAG, String.format("Looking for \"%s\" in import \"%s\"", unqualifiedName, anImport));
			switch (anImport.getType()) {
			case TYPE:
				if (anImport.getScope().endsWith("." + unqualifiedName)) {
					return anImport.getScope();
				}
				// Check for importing a class and referencing a class nested in it
				final int dotIndex = unqualifiedName.indexOf('.');
				if (dotIndex != -1) {
					final String outer = unqualifiedName.substring(0, dotIndex);
					final String inner = unqualifiedName.substring(dotIndex + 1);
					if (anImport.getScope().endsWith("." + outer)) {
						return anImport.getScope() + "." + inner;
					}
				}
				break;
			case GLOB:
				final String qualified = resolveClassInPackage(anImport.getScope(), unqualifiedName);
				if (qualified != null) {
					return qualified;
				}
				break;
			}
		}
		return null;
	}

	/**
	 * Attempts to resolve an unqualified class name in a package
	 * 
	 * @param packageName
	 *            the package in which to look for classes
	 * @param className
	 *            the class name to match
	 * @return the fully-qualified name of the class, or null if it could not be
	 *         resolved in this package
	 */
	private String resolveClassInPackage(String packageName, String className) {
		final PackageElement packageElement = env.getElementUtils().getPackageElement(packageName);
		if (packageElement != null) {
			for (Element innerType : packageElement.getEnclosedElements()) {
				switch (innerType.getKind()) {
				// Intentional fallthrough
				case CLASS:
				case ENUM:
				case INTERFACE:
					if (innerType.getSimpleName().contentEquals(className)) {
						// Found
						return innerType.asType().toString();
					}
				default:
					break;
				}
			}
		}
		final TypeElement typeElement = env.getElementUtils().getTypeElement(packageName);
		if (typeElement != null) {
			for (Element inner : typeElement.getEnclosedElements()) {
				switch (inner.getKind()) {
				// Intentional fallthrough
				case CLASS:
				case ENUM:
				case INTERFACE:
					Log.d(TAG, "Looking in class " + typeElement.getQualifiedName() + " for inner class " + className);
					if (inner.getSimpleName().contentEquals(className)) {
						// Found
						// Erase type to remove template arguments
						final Types types = env.getTypeUtils();
						return types.erasure(inner.asType()).toString();
					}
					break;
				default:
					break;
				}
			}
		}
		return null;
	}
	
}

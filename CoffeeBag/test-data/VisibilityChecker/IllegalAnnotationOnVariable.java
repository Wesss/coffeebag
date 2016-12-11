import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * Illegal to put annotation on local variable type
 */
public class IllegalAnnotationOnVariable {
	public IllegalAnnotationOnVariable() {
		@Access(level = Visibility.PUBLIC)
		int i = 32;
	}
}

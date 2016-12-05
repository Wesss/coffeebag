import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * A class that annotates public members but doesn't reuse members
 */
@Access(level = Visibility.PUBLIC)
public class AnnotationDeclarations {

	@Access(level = Visibility.PUBLIC)
	public int magicNumber;

	@Access(level = Visibility.PUBLIC)
	public AnnotationDeclarations() {
		magicNumber = 32;
	}

	@Access(level = Visibility.PUBLIC)
	public int getMagicNumber() {
		return magicNumber;
	}
}

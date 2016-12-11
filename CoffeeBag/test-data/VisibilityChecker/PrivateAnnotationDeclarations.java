import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * A class that annotates public members but doesn't reuse members
 */
@Access(level = Visibility.PRIVATE)
public class PrivateAnnotationDeclarations {

	@Access(level = Visibility.PRIVATE)
	public int magicNumber;

	@Access(level = Visibility.PRIVATE)
	public PrivateAnnotationDeclarations() {
		magicNumber = 32;
	}

	@Access(level = Visibility.PRIVATE)
	public int getMagicNumber() {
		return magicNumber;
	}
}

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * A private class that references itself
 */
@Access(level = Visibility.PRIVATE)
public class PrivateClassUsedBySelf {

	private static PrivateClassUsedBySelf selfReference;

	public PrivateClassUsedBySelf() {
		selfReference = this;
	}
}

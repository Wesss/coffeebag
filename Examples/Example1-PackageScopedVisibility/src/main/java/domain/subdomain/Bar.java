package domain.subdomain;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

/**
 * Only classes in the domain package or any of its subpackages can access
 * this class.
 * This allows classes to be visible to their immediate super package but not
 * to the world.
 */
@Access(level = Visibility.SCOPED, scope = "domain")
public class Bar {

}

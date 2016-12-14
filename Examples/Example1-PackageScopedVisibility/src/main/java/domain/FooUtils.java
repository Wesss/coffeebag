package domain;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

import domain.Foo;

/**
 * Only classes in the service package or any of its subpackages can access
 * this class.
 * As FooUtils cannot be accessed from its own domain package, it acts purely
 * as an accessor into the domain package from the service package
 */
@Access(level = Visibility.SCOPED, scope = "service")
public class FooUtils {

    public Foo generateFoo() {
        return new Foo();
    }
}

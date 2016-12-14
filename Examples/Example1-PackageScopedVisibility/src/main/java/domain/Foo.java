package domain;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

import domain.subdomain.Bar;
import domain.FooUtils;

public class Foo {

    // We are allowed access to Bar as we are in the domain package
    private Bar bar;

    public Foo() {
        bar = new Bar();
    }

    @Override
    public String toString() {
        return "I am a " + super.toString() +
                " with " + bar.toString();
    }

    private void useFooUtils() {
        // We are not allowed to use FooUtils, as we are not in the service package
        // Foo foo = new FooUtils().generateFoo();
    }
}

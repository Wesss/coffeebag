package domain;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

public class Foo {

    // Only this class or its subclasses are allowed to access description
    @Access(level = Visibility.SUBCLASS)
    protected String description;

    public Foo() {
        description = "normal description";
    }

    public String getDescription() {
        return description;
    }
}

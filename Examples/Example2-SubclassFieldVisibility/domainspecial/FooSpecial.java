package domainspecial;

import domain.Foo;

public class FooSpecial extends Foo {

    public void makeDescriptionFancy() {
        /* access to Foo.description is allowed here, as FooSpecial
            subclasses Foo */
        this.description = "*~* Fany Description *~*";
    }
}

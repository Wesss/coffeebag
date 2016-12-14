package service;

import domain.Bar;
import domain.Foo;
import domainspecial.FooSpecial;

public class ServiceMain {

    public static void main(String[] args) {
        Foo foo = new Foo();
        System.out.println("foo: " + foo.getDescription());

        /* Subclasses have access to Foo.description */
        FooSpecial foosp = new FooSpecial();
        System.out.println("foosp: " + foosp.getDescription());

        foosp.makeDescriptionFancy();
        System.out.println("foosp: " + foosp.getDescription());

        /* ServiceMain isn't allowed to access Foo.description, as it is
            declared protected */
        // foosp.description = "ServiceMain was here";
        // System.out.println(foosp.getDescription());

        /* Bar isn't allowed to access Foo.description either, event though
            it is in the same domain package */
        Foo hackedFoo = Bar.getHackedFoo();
        System.out.println("hackedFoo: " + hackedFoo.getDescription());
    }
}

package domain;

import domain.Foo;

public class Bar {

    public static Foo getHackedFoo() {
        Foo foo = new Foo();

        /* This accessing foo.description, as we do not subclass Foo.
            Note that just a protected modifier would not prevent this, as
            protected gives access to package and subclasses.
        */
        // foo.description = "Bar was here";
        
        return foo;
    }

}

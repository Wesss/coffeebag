package service;

import domain.Foo;
import domain.subdomain.Bar;
import domain.FooUtils;

public class ServiceMain {

    public static void main(String[] args) {

        /* We are not allowed use Bar, as it is limited
            to the domain scope*/
        // Bar bar = new Bar();

        /* FooUtils is allowed as we are in
            the service package scope */
        FooUtils utils = new FooUtils();
        System.out.println(utils.generateFoo());
    }
}

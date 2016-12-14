# CoffeeBag: Better visibility modifiers for Java #

## Documents ##

[Pre-proposal](https://docs.google.com/document/d/1VbdeT52qQ6Lc27SXGpw_SCbBUz5aExhsh9eNKjL32RE/edit?usp=sharing)

[Proposal](https://docs.google.com/document/d/1M12hD6MdgVvwwoywW_ghVS-D0cci7WS2AN9XKKLX604/edit?usp=sharing)

[Design Document](https://docs.google.com/document/d/1jgYfXWYt1QIQuDhg2gwF4q13cHTlYnNsELAJd7IUf6E/edit?usp=sharing)

[Project Report](https://docs.google.com/document/d/1qFW4KRdD5IEZ9t9j3Z7XdpyMS7NVKR9nrZcZWHj91-M/edit?usp=sharing)

## Tutorial ##

### Compile CoffeeBag From Source ###

CoffeeBag uses Maven for compilation.

To install CoffeeBag in your local Maven repository, run `mvn install` while in
${proj-root}/CoffeeBag

To generate a standalone .jar file: Run `mvn package`. The file will be created
at `CoffeeBag/target/CoffeeBag-1.0-SNAPSHOT.jar`.

### Compile Java Source Code with CoffeeBag ###

If your project uses Maven, add the following to the `<dependencies>` section
of your `pom.xml`:


    <dependency>
    	<groupId>org.coffeebag</groupId>
    	<artifactId>CoffeeBag</artifactId>
    	<version>1.0-SNAPSHOT</version>
    </dependency>

Otherwise, add the CoffeeBag jar to your classpath when compiling:

	javac -cp CoffeeBag-1.0-SNAPSHOT.jar File1.java

CoffeeBag will run during the compilation process and will check your code.

### Writing CoffeeBag Visibility Modifiers ###

CoffeeBag visibility modifiers are declared through the `@Access` annotation.

    @Access(level = Visibility.PUBLIC)
    public class Foo {

The `@Access` annotation is allowed on classes, interfaces, enums, and fields.
(methods and constructors are also legal, but do nothing at the moment)

Four visibility levels are currently supported:
- PUBLIC: the annotated member can be accessed anywhere.
- PRIVATE: the annotated member can only be accessed within its parent class.
    If the member is a class, enum, or interface, it may only be accessed in
    its package.
- SUBCLASS: The annotated member may only be accessed by in classes that subclass
    the annotated member's class.
- SCOPE: To use SCOPE, an additional scope argument must be passed in the
    annotation as shown below. The annotated member may only be accessed by
    classes that are either in the package or in a sub-package represented by the
    scope argument.


    @Access(level = Visibility.SCOPED, scope = "org.some.package")
    public class Foo {

## Examples ##

Examples are available in the [examples folder](Examples).

# CoffeeBag: Better visibility modifiers for Java #

## Documents ##

[Pre-proposal](https://docs.google.com/document/d/1VbdeT52qQ6Lc27SXGpw_SCbBUz5aExhsh9eNKjL32RE/edit?usp=sharing)

[Proposal](https://docs.google.com/document/d/1M12hD6MdgVvwwoywW_ghVS-D0cci7WS2AN9XKKLX604/edit?usp=sharing)

[Design Document](https://docs.google.com/document/d/1jgYfXWYt1QIQuDhg2gwF4q13cHTlYnNsELAJd7IUf6E/edit?usp=sharing)

[Project Report](https://docs.google.com/document/d/1qFW4KRdD5IEZ9t9j3Z7XdpyMS7NVKR9nrZcZWHj91-M/edit?usp=sharing)

## Tutorial ##

### Compiling ###

CoffeeBag uses Maven for compilation.

To install CoffeeBag in your local Maven repository: `mvn install`

To generate a standalone .jar file: Run `mvn package`. The file will be created
at `target/CoffeeBag-1.0-SNAPSHOT.jar`.

### Using ###

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

## Examples ##

Examples are available in the [examples folder](Examples).

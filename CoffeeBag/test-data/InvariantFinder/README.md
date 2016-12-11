# Invariant Finder Tests

Each `.java` file is scanned for invariants and is expected to match the
expectation in the corresponding `.txt` file of the same name.

Format is as follows:

First line: `[fully.qualified.class.name.Myclass] //trailing tokens are ignored`

Subsequent lines: `[package] [className] [expectedResult]` where
* `[package]` is the full package name
                    "" is interpreted as the empty package
* `[className]` is the simple class name of a class hypothetically using the class declared above

* `[expectedResult]` is either `PASS` or `FAIL`, representing whether the
                    class in the given package should be allowed usage

A blank line is then interpreted to stop testing the last class declared and prepare
to accept a new expected class to test on.

Example:

	MyClass //comments
	    org MyClass PASS //we can access ourselves
	    org.some.fictional Other FAIL
	    "" MyClass FAIL //not allowed in default package
	
	MyClass2 //nextClass
	    class org.MyClass FAIL

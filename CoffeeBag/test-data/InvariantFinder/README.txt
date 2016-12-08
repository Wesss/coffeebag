# Invariant Finder Tests

Each .java file is scanned for invariants and is expected to match the
expectation in the corresponding .txt file of the same name.

Format is as follows:

First line: [fully.qualified.class.name.Myclass] //trailing tokens are ignored

Subsequent lines: [visibilityCheck] [nameToCheck] [expectedResult]
    where [visibilityCheck] is either "class" or "package",
    [nameToCheck] is the fully qualified class/package name that is testing usage
    [expectedResult] is either "PASS" or "FAIL", representing whether the class/package declared previously should be allowed usage

A blank line is then interpreted to stop testing the last class declared and prepare
to accept a new expected class to test on.

Example:
MyClass //comments
    class org.MyClass PASS //we can access ourselved
    class org.some.fictional.Other Class FAIL //different class can't access this
    package "" FAIL //not allowed in default package
    package org.some.fictional.dependant PASS //allowed in this package

MyClass2 //nextClass
    class org.MyClass FAIL

<EOF>
# VisibilityChecker Tests

Each Test consists of source file(s) and an expectation .txt file, where:

The source files are either the single file <testname>.java or two files in
<testname>/ClassA.java and <testname>/ClassB.java.

The expectation file is a single .txt file of name <testname>.txt, that contains
the single token "PASS" or "FAIL" depending on if the sources should compile
or error respectively.

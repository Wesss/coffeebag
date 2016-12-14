#!/bin/bash

# clean
./clean.sh

# compile processor
echo 'getting CoffeeBag pre-processor'
cd ../../CoffeeBag/
mvn package > /dev/null
# move jar
cp target/CoffeeBag-1.0-SNAPSHOT.jar ../Examples/Example1-PackageScopedVisibility/
cd ../Examples/Example1-PackageScopedVisibility

# compile
echo 'compiling java code'
javac -cp CoffeeBag-1.0-SNAPSHOT.jar service/ServiceMain.java domain/Foo.java \
domain/FooUtils.java domain/subdomain/Bar.java


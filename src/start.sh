#!/bin/bash


# check java version, we need java8
if type -p java; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME
    _java="$JAVA_HOME/bin/java"
else
    echo "no java"
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" > "1.8" ]]; then
        echo JAVA version is more than 1.8, it works
    else         
        echo please use install JDK1.8 and switch JAVA_HOME to JAVA 8
    fi
fi


echo "Compile source code ...."
javac -source 1.8 ./Main.java

echo "Run test case ..."
$_java Main

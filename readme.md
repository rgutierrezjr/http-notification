# Plugin Example

This is an base for a Rundeck Notification plugin implementation.

## Building

    ./gradlew build

Runs all tests and produces:

	build/libs/plugin-example-0.1.0-SNAPSHOT.jar

## Testing

    ./gradlew check

Runs all tests

## Install

First, download the "Rundeck Launcher" [Rundeck](http://rundeck.org/downloads.html)

    mkdir rundeck
    cd rundeck
    cp ../rundeck-launcher-2.10.2.jar .
    mkdir libext
    cp ../build/libs/plugin-example-0.1.0-SNAPSHOT.jar libext/

## Run rundeck

    java -jar rundeck-launcher-2.10.2.jar


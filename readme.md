# Http Notification Plugin

This is my implementation for candidate consideration. Specification: HttpNotificationPlugin.

## Description

The following plugin provides lightweight Http "webhook-like" notification functionality. 

Given a url, the user can notify the recipient via HTTP (POST or PUT) with xml, json, or plain text.

The user can also instantiate an instance of the utility with various HTTP client configurations like connection and socket timeouts.

The plugin provides logging and user friendly exception messaging.

## Build

    gradle clean
    gradle build
    
## Test
Run test suite class.

    gradle test
    
## Fat Jar
Jar file located in /build/libs
    
    gradle jar

## Dependencies
    compile( [group: 'com.mashape.unirest', name: 'unirest-java', version: '1.4.9'])
    compile( [group: 'org.apache.httpcomponents', name: 'httpclient', version: '4.3.6'])
    compile( [group: 'org.apache.httpcomponents', name: 'httpasyncclient', version: '4.0.2'])
    compile( [group: 'org.apache.httpcomponents', name: 'httpmime', version: '4.3.6'])
    compile( [group: 'org.json', name: 'json', version: '20140107'])

    implementation 'org.dom4j:dom4j:2.1.1'
    implementation 'com.google.code.gson:gson:2.8.5'

    testCompile(
        [group: 'junit', name: 'junit', version: '4.10',ext:'jar']
    )
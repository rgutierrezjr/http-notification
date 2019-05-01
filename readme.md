# Http Notification Plugin

This is my implementation for candidate consideration. Specification: HttpNotificationPlugin.

## Description

The following plugin provides lightweight Http "webhook-like" notification functionality. 

Given a url, the user can notify the recipient via HTTP (POST or PUT) with xml, json, or plain text.

The user can also instantiate an instance of the utility with various HTTP client configurations like connection and socket timeouts.

The plugin provides logging and user friendly exception messaging.
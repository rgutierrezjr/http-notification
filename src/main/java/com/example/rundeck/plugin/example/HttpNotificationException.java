package com.example.rundeck.plugin.example;

/**
 * Custom exception for HttpNotificationPlugin.
 */
public class HttpNotificationException extends Exception {
    public HttpNotificationException(String s) {
        super(s);
    }
}

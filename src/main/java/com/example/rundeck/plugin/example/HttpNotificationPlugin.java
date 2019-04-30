package com.example.rundeck.plugin.example;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The following implementation of NotificationPlugin will post HTTP notification requests.
 * The user can pass body content, content type, and HTTP method type.
 */
@Plugin(service = "Notification", name = "my-example")
@PluginDescription(title = "HTTP Notification Plugin", description = "An implementation of Rundeck's Notification Interface.")
public class HttpNotificationPlugin {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /*
        Maximum number of attempts in which to make a successful request.
     */
    public static final Integer DEFAULT_MAX_ATTEMPTS = 5;

    private Integer maxAttempts = DEFAULT_MAX_ATTEMPTS;

    /*
        Plugin default request timeout lengths.
     */
    public static final Long DEFAULT_CONNECTION_TIMEOUT = 10000L;
    public static final Long DEFAULT_SOCKET_TIMEOUT = 60000L;

    private Long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private Long socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    /*
        Supported HTTP methods.
     */
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";

    public static final List<String> SUPPORTED_HTTP_METHODS = Arrays.asList(HTTP_METHOD_POST, HTTP_METHOD_PUT);

    /*
        Supported content types.
     */
    public static final String CONTENT_JSON = "application/json";
    public static final String CONTENT_TEXT = "application/text";

    public static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(CONTENT_JSON, CONTENT_TEXT);

    public HttpNotificationPlugin(Long connectionTimeout, Long socketTimeout, Integer maxAttempts) {
        if (connectionTimeout != null && connectionTimeout > 0L) {
            this.connectionTimeout = connectionTimeout;
        }

        if (socketTimeout != null && socketTimeout > 0L) {
            this.socketTimeout = socketTimeout;
        }

        if (maxAttempts != null && maxAttempts > 0) {
            this.maxAttempts = maxAttempts;
        }

        Unirest.setTimeouts(this.connectionTimeout, this.socketTimeout);
    }

    /**
     * The following method will post a HTTP notification to the passed in url.
     *
     * @param url         The designated endpoint accepting the notification.
     * @param httpMethod  The HTTP method to use.
     * @param contentType Request header content type.
     * @param body        The notification body.
     * @return
     */
    public Boolean sendNotification(String url, String httpMethod, String contentType, String body) throws HttpNotificationException {
        if (url == null || url.isEmpty()) {
            throw new HttpNotificationException("Error: URL is required.");
        }

        if (httpMethod == null || httpMethod.isEmpty()) {
            throw new HttpNotificationException("Error: HTTP method is required.");
        } else if (!SUPPORTED_HTTP_METHODS.contains(httpMethod)) {
            throw new HttpNotificationException("Error: HTTP method not supported. The following methods are supported: " + SUPPORTED_HTTP_METHODS.toString());
        }

        if (contentType == null || contentType.isEmpty()) {
            throw new HttpNotificationException("Error: Content type is required.");
        } else if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
            throw new HttpNotificationException("Error: Content type not supported. The following methods are supported: " + SUPPORTED_CONTENT_TYPES.toString());
        }

        if (body == null || body.isEmpty()) {
            throw new HttpNotificationException("Error: Notification body is required.");
        }

        if (httpMethod == HTTP_METHOD_POST) {
            return postNotification(url, contentType, body);
        } else if (httpMethod == HTTP_METHOD_PUT) {
            return putNotification(url, contentType, body);
        }

        return false;
    }

    /**
     * The following helper method will POST an HttpNotification (body) to the designated url.
     * @param url
     * @param contentType
     * @param body
     * @return True if successful, False if not.
     */
    private Boolean postNotification(String url, String contentType, String body) {

        try {
            if (contentType == CONTENT_JSON) {
                HttpResponse<JsonNode> jsonResponse = Unirest.post(url)
                        .header("content-type", "application/json")
                        .body(body)
                        .asJson();

            } else if (contentType == CONTENT_TEXT) {
                HttpResponse response = Unirest.post(url)
                        .body(body)
                        .asString();
            }
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, "Failed to POST Http Notification.");

            return false;
        }

        return true;
    }

    /**
     * The following helper method will PUT an HttpNotification (body) to the designated url.
     * @param url
     * @param contentType
     * @param body
     * @return True if successful, False if not.
     */
    private Boolean putNotification(String url, String contentType, String body) {

        try {
            if (contentType == CONTENT_JSON) {
                HttpResponse<JsonNode> jsonResponse = Unirest.put(url)
                        .header("content-type", "application/json")
                        .body(body)
                        .asJson();
            } else if (contentType == CONTENT_TEXT) {
                HttpResponse response = Unirest.put(url)
                        .body(body)
                        .asString();
            }
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, "Failed to PUT Http Notification.");

            return false;
        }


        return true;
    }
}
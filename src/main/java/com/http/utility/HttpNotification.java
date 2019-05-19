package com.http.utility;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.google.gson.Gson;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The following implementation Http notification plugin will post requests to the target url.
 * The user of this plugin can pass body content, content type, and HTTP method type.
 */
public class HttpNotification {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Gson gson = new Gson();

    /*
        Plugin default request timeout lengths.
     */
    public static final Long DEFAULT_CONNECTION_TIMEOUT = 10000L;
    public static final Long DEFAULT_SOCKET_TIMEOUT = 60000L;

    private Long connectionTimeout;
    private Long socketTimeout;

    private Unirest unirest = new Unirest();

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
    public static final String CONTENT_TEXT = "text/plain";
    public static final String CONTENT_XML = "text/xml";

    public static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(CONTENT_JSON, CONTENT_TEXT, CONTENT_XML);

    public HttpNotification(Long connectionTimeout, Long socketTimeout) {

        this.connectionTimeout = connectionTimeout != null && connectionTimeout > 0L ? connectionTimeout : DEFAULT_CONNECTION_TIMEOUT;

        this.socketTimeout = socketTimeout != null && socketTimeout > 0L ? socketTimeout : DEFAULT_SOCKET_TIMEOUT;

        this.unirest.setTimeouts(this.connectionTimeout, this.socketTimeout);

    }

    /**
     * Getter method for connectionTimout.
     *
     * @return Current connection timeout value.
     */
    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Setter method for connectionTimout.
     *
     * @param connectionTimeout New connection timeout value.
     */
    public void setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.unirest.setTimeouts(this.connectionTimeout, this.socketTimeout);
    }

    /**
     * Getter method for socketTimeout.
     *
     * @return Current socket timeout value.
     */
    public Long getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Setter method for socketTimout.
     *
     * @param socketTimeout New socket timeout value.
     */
    public void setSocketTimeout(Long socketTimeout) {
        this.socketTimeout = socketTimeout;
        this.unirest.setTimeouts(this.connectionTimeout, this.socketTimeout);

    }

    /**
     * The following method will post an HTTP notification to the passed in url.
     *
     * @param url         The designated endpoint accepting the notification.
     * @param httpMethod  The HTTP method to use.
     * @param contentType Request header content type.
     * @param body        The notification body.
     * @return True if successful, False if not.
     * @throws HttpNotificationException
     */
    public Boolean sendNotification(String url, String httpMethod, String contentType, String body) throws HttpNotificationException {
        if (url == null || url.isEmpty()) {
            throw new HttpNotificationException("Error: URL is required.");
        } else {
            String[] schemes = {"http","https"};
            UrlValidator urlValidator = new UrlValidator(schemes);

            if (!urlValidator.isValid(url)) {
                throw new HttpNotificationException("Error: URL is invalid.");
            }
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

        if (contentType == CONTENT_JSON && !isValidJson(body)) {
            throw new HttpNotificationException("Error: Json is invalid.");
        }

        if (contentType == CONTENT_XML && !isValidXml(body)) {
            throw new HttpNotificationException("Error: Xml is invalid.");
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
     *
     * @param url         The designated endpoint accepting the notification.
     * @param contentType Request header content type.
     * @param body        The notification body.
     * @return True if successful, False if not.
     * @throws HttpNotificationException
     */
    private Boolean postNotification(String url, String contentType, String body) throws HttpNotificationException {
        Integer statusCode = 0;
        String statusText = "";

        try {
            if (contentType == CONTENT_JSON) {
                HttpResponse<JsonNode> jsonResponse = unirest.post(url)
                        .header("Content-Type", contentType)
                        .body(body)
                        .asJson();

                statusCode = jsonResponse.getStatus();
                statusText = jsonResponse.getStatusText();

            } else if (contentType == CONTENT_TEXT || contentType == CONTENT_XML) {
                HttpResponse response = unirest.post(url)
                        .header("Content-Type", contentType)
                        .body(body)
                        .asString();

                statusCode = response.getStatus();
                statusText = response.getStatusText();

            }
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, "Error: Failed to POST Http Notification.");

            return false;
        }

        if (statusCode >= 300) {
            throwStatusCodeException(statusCode, statusText);
        }

        return true;
    }

    /**
     * The following helper method will PUT an HttpNotification (body) to the designated url.
     *
     * @param url         The designated endpoint accepting the notification.
     * @param contentType Request header content type.
     * @param body        The notification body.
     * @return True if successful, False if not.
     * @throws HttpNotificationException
     */
    private Boolean putNotification(String url, String contentType, String body) throws HttpNotificationException {
        Integer statusCode = 0;
        String statusText = "";

        try {
            if (contentType == CONTENT_JSON) {
                HttpResponse<JsonNode> jsonResponse = unirest.put(url)
                        .header("Content-Type", contentType)
                        .body(body)
                        .asJson();

                statusCode = jsonResponse.getStatus();
                statusText = jsonResponse.getStatusText();

            } else if (contentType == CONTENT_TEXT || contentType == CONTENT_XML) {
                HttpResponse response = unirest.put(url)
                        .header("Content-Type", contentType)
                        .body(body)
                        .asString();

                statusCode = response.getStatus();
                statusText = response.getStatusText();

            }
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, "Error: Failed to PUT Http Notification.");

            return false;
        }

        if (statusCode >= 300) {
            throwStatusCodeException(statusCode, statusText);
        }

        return true;
    }

    /**
     * This helper method will derive and throw a new HttpNotificationException based on the Http status code.
     *
     * @param code The Http status code returned by the client.
     * @throws HttpNotificationException
     */
    private void throwStatusCodeException(Integer code, String codeText) throws HttpNotificationException {
        if (code >= 300 && code < 400) {
            throw new HttpNotificationException("Error: Server responded with redirection. Code: " + code + ", Text: " + codeText);
        } else if (code >= 400 && code < 500) {
            throw new HttpNotificationException("Error: Server responded with client side error. Code: " + code + ", Text: " + codeText);
        } else if (code >= 500 && code < 600) {
            throw new HttpNotificationException("Error: Server responded with server error. Code: " + code + ", Text: " + codeText);
        }
    }

    /**
     * Simple helper method which determines if the given json (string) is valid.
     *
     * @param jsonString The json object as a string.
     * @return True if valid, False if invalid.
     */
    public static boolean isValidJson(String jsonString) {
        try {
            gson.fromJson(jsonString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    /**
     * Simple helper method which determines if the given xml (string) is valid.
     *
     * @param xmlString The xml object as a string.
     * @return True if valid, False if invalid.
     */
    public static boolean isValidXml(String xmlString) {
        StringWriter sw;

        try {
            final OutputFormat format = OutputFormat.createPrettyPrint();
            final Document document = DocumentHelper.parseText(xmlString);
            sw = new StringWriter();
            final XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
        } catch (DocumentException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
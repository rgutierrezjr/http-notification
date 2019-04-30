package com.example.rundeck.plugin.example;

import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HttpNotificationPluginTest {

    final String blank_url = "";
    final String null_url = null;
    final String valid_url_post = "https://postman-echo.com/post";
    final String valid_url_put = "https://postman-echo.com/put";

    final String blank_content_type = "";
    final String null_content_type = null;
    final String unsupported_content_type = "pdf";

    final String unsupported_method = "DELETE";
    final String blank_method = "";
    final String null_method = null;

    final String blank_body = "";
    final String null_body = "";

    final String valid_json = "{\"name\":\"Ruben Gutierrez\", \"message\":\"This is a notification.\"}";
    final String valid_text = "This is a notification";

    HttpNotificationPlugin notificationPlugin = new HttpNotificationPlugin(0L,0L,0);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testBlankURL() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: URL is required.");

        notificationPlugin.sendNotification(blank_url, HttpNotificationPlugin.HTTP_METHOD_PUT, HttpNotificationPlugin.CONTENT_JSON, valid_json);
    }

    @Test
    public void testNullURL() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: URL is required.");

        notificationPlugin.sendNotification(null_url, HttpNotificationPlugin.HTTP_METHOD_PUT, HttpNotificationPlugin.CONTENT_JSON, valid_json);
    }

    @Test
    public void testBlankMethod() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: HTTP method is required.");

        notificationPlugin.sendNotification(valid_url_post, blank_method, HttpNotificationPlugin.CONTENT_JSON, valid_json);
    }

    @Test
    public void testNullMethod() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: HTTP method is required.");

        notificationPlugin.sendNotification(valid_url_post, null_method, HttpNotificationPlugin.CONTENT_JSON, valid_json);
    }

    @Test
    public void testUnsupportedMethod() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: HTTP method not supported. The following methods are supported: " + HttpNotificationPlugin.SUPPORTED_HTTP_METHODS.toString());

        notificationPlugin.sendNotification(valid_url_post, unsupported_method, HttpNotificationPlugin.CONTENT_JSON, valid_json);
    }

    @Test
    public void testBlankContentType() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: Content type is required.");

        notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_PUT, blank_content_type, valid_json);
    }

    @Test
    public void testNullContentType() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: Content type is required.");

        notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_PUT, null_content_type, valid_json);
    }

    @Test
    public void testUnsupportedContentType() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: Content type not supported. The following methods are supported: " + HttpNotificationPlugin.SUPPORTED_CONTENT_TYPES.toString());

        notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_PUT, unsupported_content_type, valid_json);
    }

    @Test
    public void testBlankBody() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: Notification body is required.");

        notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_PUT, HttpNotificationPlugin.CONTENT_JSON, blank_body);
    }

    @Test
    public void testNullBody() throws HttpNotificationException {
        exceptionRule.expect(HttpNotificationException.class);
        exceptionRule.expectMessage("Error: Notification body is required.");

        notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_PUT, HttpNotificationPlugin.CONTENT_JSON, null_body);
    }

    @Test
    public void testPostJsonNotification() throws HttpNotificationException {
        Boolean result = notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_POST, HttpNotificationPlugin.CONTENT_JSON, valid_json);

        assert result == true;
    }

    @Test
    public void testPostTextNotification() throws HttpNotificationException {
        Boolean result = notificationPlugin.sendNotification(valid_url_post, HttpNotificationPlugin.HTTP_METHOD_POST, HttpNotificationPlugin.CONTENT_TEXT, valid_text);

        assert result == true;
    }

    @Test
    public void testPutJsonNotification() throws HttpNotificationException {
        Boolean result = notificationPlugin.sendNotification(valid_url_put, HttpNotificationPlugin.HTTP_METHOD_PUT, HttpNotificationPlugin.CONTENT_JSON, valid_json);

        assert result == true;
    }

    @Test
    public void testPutTextNotification() throws HttpNotificationException {
        Boolean result = notificationPlugin.sendNotification(valid_url_put, HttpNotificationPlugin.HTTP_METHOD_PUT, HttpNotificationPlugin.CONTENT_TEXT, valid_text);

        assert result == true;
    }
}
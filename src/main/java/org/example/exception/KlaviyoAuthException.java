package org.example.exception;

public class KlaviyoAuthException extends KlaviyoApiException {

    public KlaviyoAuthException(String message, int statusCode) {
        super(message, statusCode);
    }
}
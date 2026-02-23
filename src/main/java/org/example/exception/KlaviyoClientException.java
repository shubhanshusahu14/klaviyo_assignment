package org.example.exception;

public class KlaviyoClientException extends KlaviyoApiException {

    public KlaviyoClientException(String message, int statusCode) {
        super(message, statusCode);
    }
}
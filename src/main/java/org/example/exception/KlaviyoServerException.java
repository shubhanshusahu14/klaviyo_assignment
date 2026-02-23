package org.example.exception;

public class KlaviyoServerException extends KlaviyoApiException {

    public KlaviyoServerException(String message, int statusCode) {
        super(message, statusCode);
    }
}
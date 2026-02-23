package org.example.exception;

public class KlaviyoApiException extends RuntimeException {

    private final int statusCode;

    public KlaviyoApiException(String message, int statusCode) {
        super(message);   // passes message to RuntimeException
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
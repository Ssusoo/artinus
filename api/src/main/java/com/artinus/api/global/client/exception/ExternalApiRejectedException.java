package com.artinus.api.global.client.exception;

public class ExternalApiRejectedException extends RuntimeException {
    public ExternalApiRejectedException(String message) {
        super(message);
    }
    public ExternalApiRejectedException(String message, Throwable cause) {
        super(message, cause);
    }
}

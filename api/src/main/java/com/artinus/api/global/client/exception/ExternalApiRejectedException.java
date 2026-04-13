package com.artinus.api.client;

public class ExternalApiRejectedException extends RuntimeException {
    public ExternalApiRejectedException(String message) {
        super(message);
    }
    public ExternalApiRejectedException(String message, Throwable cause) {
        super(message, cause);
    }
}

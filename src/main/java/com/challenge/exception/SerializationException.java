package com.challenge.exception;

public class SerializationException extends RuntimeException {
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
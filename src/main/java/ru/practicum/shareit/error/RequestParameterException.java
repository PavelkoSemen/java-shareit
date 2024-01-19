package ru.practicum.shareit.error;

public class RequestParameterException extends RuntimeException {
    public RequestParameterException() {
    }

    public RequestParameterException(String message) {
        super(message);
    }

    public RequestParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
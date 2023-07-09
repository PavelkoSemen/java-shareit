package ru.practicum.shareit.error;

public class EntityAccessException extends RuntimeException {
    public EntityAccessException() {
    }

    public EntityAccessException(String message) {
        super(message);
    }

    public EntityAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
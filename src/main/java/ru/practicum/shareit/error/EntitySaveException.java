package ru.practicum.shareit.error;

public class EntitySaveException extends RuntimeException {
    public EntitySaveException() {
    }

    public EntitySaveException(String message) {
        super(message);
    }

    public EntitySaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
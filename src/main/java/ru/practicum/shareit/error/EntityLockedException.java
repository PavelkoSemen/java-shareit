package ru.practicum.shareit.error;

public class EntityLockedException extends RuntimeException {
    public EntityLockedException() {
    }

    public EntityLockedException(String message) {
        super(message);
    }

    public EntityLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
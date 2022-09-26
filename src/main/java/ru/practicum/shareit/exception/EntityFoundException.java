package ru.practicum.shareit.exception;

public class EntityFoundException extends RuntimeException {

    public EntityFoundException(String message) {
        super(message);
    }
}
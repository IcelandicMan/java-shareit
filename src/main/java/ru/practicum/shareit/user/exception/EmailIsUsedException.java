package ru.practicum.shareit.user.exception;

public class EmailIsUsedException extends RuntimeException {
    public EmailIsUsedException(String message) {
        super(message);
    }
}


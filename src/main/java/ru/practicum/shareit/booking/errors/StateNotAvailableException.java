package ru.practicum.shareit.booking.errors;

public class StateNotAvailableException extends RuntimeException {
    public StateNotAvailableException(String message) {
        super(message);
    }

}

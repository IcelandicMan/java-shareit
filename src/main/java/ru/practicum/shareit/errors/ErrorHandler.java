package ru.practicum.shareit.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.exception.EmailIsUsedException;
import ru.practicum.shareit.user.exception.UserNotFoundException;


import java.util.List;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class})

public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder("Ошибка валидации по параметру ");

        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField())
                    .append(" (значение: ")
                    .append(fieldError.getRejectedValue())
                    .append("): ")
                    .append(fieldError.getDefaultMessage())
                    .append(", ");
        }
        log.error(errorMessage.toString());
        return new ErrorResponse(errorMessage.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailIsUsedException(final EmailIsUsedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(e.getMessage());
    }
}

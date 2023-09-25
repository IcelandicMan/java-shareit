package ru.practicum.shareit.errors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ErrorResponseTest {

    ErrorResponse errorResponse;

    String error = "Error";

    @Test
    void errorResponse() {
        errorResponse = new ErrorResponse(error);
        assertEquals(error, errorResponse.getError());
    }
}
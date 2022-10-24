package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void defaultHandle() {
        Exception exception = new Exception("INTERNAL_SERVER_ERROR");
        ErrorResponse response = errorHandler.defaultHandle(exception);
        assertNotNull(response);
        assertEquals(response.getError(), exception.getMessage());
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("BAD_REQUEST");
        ErrorResponse response = errorHandler.handleValidationException(exception);
        assertNotNull(response);
        assertEquals(response.getError(), exception.getMessage());
    }

    @Test
    void bookingException() {
        BookingException exception = new BookingException("BAD_REQUEST");
        ErrorResponse response = errorHandler.handelBookingException(exception);
        assertNotNull(response);
        assertEquals(response.getError(), exception.getMessage());
    }

    @Test
    void handelNotFoundException() {
        NotFoundException exception = new NotFoundException("NOT_FOUND");
        ErrorResponse response = errorHandler.handelNotFoundException(exception);
        assertNotNull(response);
        assertEquals(response.getError(), exception.getMessage());
    }
}
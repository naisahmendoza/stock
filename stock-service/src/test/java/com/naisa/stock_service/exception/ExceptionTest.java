package com.naisa.stock_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void unsupportedVendorException_withMessage_shouldCreateException() {
        // Act
        UnsupportedVendorException exception = new UnsupportedVendorException("Invalid vendor");

        // Assert
        assertNotNull(exception);
        assertEquals("Invalid vendor", exception.getMessage());
    }

    @Test
    void unsupportedVendorException_withMessageAndCause_shouldCreateException() {
        // Arrange
        RuntimeException cause = new RuntimeException("Root cause");

        // Act
        UnsupportedVendorException exception = new UnsupportedVendorException("Invalid vendor", cause);

        // Assert
        assertNotNull(exception);
        assertEquals("Invalid vendor", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void unsupportedVendorException_shouldBeInstanceOfRuntimeException() {
        // Act
        UnsupportedVendorException exception = new UnsupportedVendorException("Test");

        // Assert
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void csvParseException_withMessage_shouldCreateException() {
        // Act
        CsvParseException exception = new CsvParseException("Invalid CSV format");

        // Assert
        assertNotNull(exception);
        assertEquals("Invalid CSV format", exception.getMessage());
    }

    @Test
    void csvFileNotException_withMessage_shouldCreateException() {
        // Act
        CsvFileNotException exception = new CsvFileNotException("File not found");

        // Assert
        assertNotNull(exception);
        assertEquals("File not found", exception.getMessage());
    }
}

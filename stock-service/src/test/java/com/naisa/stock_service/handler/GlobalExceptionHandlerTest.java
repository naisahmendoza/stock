package com.naisa.stock_service.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleValidation_withFieldErrors_shouldReturnBadRequest() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        WebRequest request = mock(WebRequest.class);

        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("productDto", "sku", "SKU is required"));
        errors.add(new FieldError("productDto", "vendor", "Vendor is required"));

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleValidation_withSingleFieldError_shouldReturnError() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        WebRequest request = mock(WebRequest.class);

        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("stockUpdateDto", "stockQuantity", "Stock quantity must be positive"));

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleValidation_withNoFieldErrors_shouldReturnEmptyMap() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        WebRequest request = mock(WebRequest.class);

        List<FieldError> emptyErrors = new ArrayList<>();

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(emptyErrors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleAll_withGeneralException_shouldReturnInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database connection failed");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/products");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleAll_withNullException_shouldHandleGracefully() {
        // Arrange
        RuntimeException exception = new RuntimeException();
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/products");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleAll_shouldIncludePathInResponse() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/products/123");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleAll_withLongErrorMessage_shouldIncludeFullMessage() {
        // Arrange
        String longError = "This is a very long error message that contains detailed information about the sync failure";
        RuntimeException exception = new RuntimeException(longError);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/sync");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleValidation_withMultipleFieldErrors_shouldIncludeAll() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        WebRequest request = mock(WebRequest.class);

        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("dto", "sku", "Sku required"));
        errors.add(new FieldError("dto", "name", "Name required"));
        errors.add(new FieldError("dto", "vendor", "Vendor required"));

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}


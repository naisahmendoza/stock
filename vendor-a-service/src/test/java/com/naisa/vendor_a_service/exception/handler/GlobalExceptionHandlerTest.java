package com.naisa.vendor_a_service.exception.handler;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
        errors.add(new FieldError("productDto", "name", "Name is required"));

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("sku"));
        assertTrue(response.getBody().toString().contains("name"));
    }

    @Test
    void handleValidation_withSingleFieldError_shouldReturnError() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        WebRequest request = mock(WebRequest.class);

        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("productDto", "stockQuantity", "Stock quantity must be positive"));

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertTrue(body.containsKey("stockQuantity"));
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
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertTrue(body.isEmpty());
    }

    @Test
    void handleAll_withGeneralException_shouldReturnInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database connection failed");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/vendor-a/products");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Database connection failed", body.get("error"));
        assertEquals("uri=/vendor-a/products", body.get("path"));
    }

    @Test
    void handleAll_withNullException_shouldHandleGracefully() {
        // Arrange
        RuntimeException exception = new RuntimeException();
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/vendor-a/products");

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
        when(request.getDescription(false)).thenReturn("uri=/vendor-a/products/123");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("path"));
        assertTrue(body.get("path").toString().contains("/vendor-a/products/123"));
    }

    @Test
    void handleAll_withLongErrorMessage_shouldIncludeFullMessage() {
        // Arrange
        String longError = "This is a very long error message that contains detailed information about the failure";
        RuntimeException exception = new RuntimeException(longError);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/test");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAll(exception, request);

        // Assert
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(longError, body.get("error"));
    }

    @Test
    void handleValidation_withMultipleFieldsAndErrors_shouldReturnAll() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        WebRequest request = mock(WebRequest.class);

        List<FieldError> errors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            errors.add(new FieldError("dto", "field" + i, "Error " + i));
        }

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertEquals(5, body.size());
    }
}


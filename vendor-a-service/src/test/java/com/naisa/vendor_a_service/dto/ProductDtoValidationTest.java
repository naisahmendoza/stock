package com.naisa.vendor_a_service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void productDto_withValidData_shouldPassValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("SKU123");
        dto.setName("Product Name");
        dto.setStockQuantity(100);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void productDto_withNullSku_shouldFailValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku(null);
        dto.setName("Product Name");
        dto.setStockQuantity(100);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sku")));
    }

    @Test
    void productDto_withBlankSku_shouldFailValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("   ");
        dto.setName("Product Name");
        dto.setStockQuantity(100);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void productDto_withNullName_shouldFailValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("SKU123");
        dto.setName(null);
        dto.setStockQuantity(100);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void productDto_withNullStockQuantity_shouldFailValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("SKU123");
        dto.setName("Product Name");
        dto.setStockQuantity(null);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void productDto_withNegativeStockQuantity_shouldFailValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("SKU123");
        dto.setName("Product Name");
        dto.setStockQuantity(-10);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void productDto_withZeroStockQuantity_shouldPassValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("SKU123");
        dto.setName("Product Name");
        dto.setStockQuantity(0);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void productDto_withLargeStockQuantity_shouldPassValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setSku("SKU123");
        dto.setName("Product Name");
        dto.setStockQuantity(999999);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void productDto_withAllFieldsNull_shouldFailValidation() {
        // Arrange
        ProductDto dto = new ProductDto();

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.size() >= 3); // At least 3 violations for sku, name, stockQuantity
    }

    @Test
    void productDto_gettersAndSetters_shouldWorkCorrectly() {
        // Arrange
        ProductDto dto = new ProductDto();
        String sku = "SKU999";
        String name = "Test Product";
        Integer quantity = 50;

        // Act
        dto.setSku(sku);
        dto.setName(name);
        dto.setStockQuantity(quantity);

        // Assert
        assertEquals(sku, dto.getSku());
        assertEquals(name, dto.getName());
        assertEquals(quantity, dto.getStockQuantity());
    }

    @Test
    void productDto_lombokData_shouldGenerateEqualsAndHashCode() {
        // Arrange
        ProductDto dto1 = new ProductDto();
        dto1.setSku("SKU123");
        dto1.setName("Product");
        dto1.setStockQuantity(100);

        ProductDto dto2 = new ProductDto();
        dto2.setSku("SKU123");
        dto2.setName("Product");
        dto2.setStockQuantity(100);

        // Act & Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}


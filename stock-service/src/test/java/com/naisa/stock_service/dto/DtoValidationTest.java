package com.naisa.stock_service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void stockUpdateDto_withValidData_shouldPassValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void stockUpdateDto_withNullSku_shouldFailValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku(null);
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sku")));
    }

    @Test
    void stockUpdateDto_withNullVendor_shouldFailValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor(null);

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void stockUpdateDto_withNullStockQuantity_shouldFailValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(null);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void stockUpdateDto_withBlankSku_shouldFailValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("   ");
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void productDto_withValidData_shouldPassValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setId(UUID.randomUUID());
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(50);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void stockUpdateDto_gettersAndSetters_shouldWorkCorrectly() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        String sku = "SKU999";
        String name = "Product";
        Integer quantity = 75;
        String vendor = "Vendor B";

        // Act
        dto.setSku(sku);
        dto.setName(name);
        dto.setStockQuantity(quantity);
        dto.setVendor(vendor);

        // Assert
        assertEquals(sku, dto.getSku());
        assertEquals(name, dto.getName());
        assertEquals(quantity, dto.getStockQuantity());
        assertEquals(vendor, dto.getVendor());
    }

    @Test
    void stockUpdateDto_withZeroStockQuantity_shouldPassValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(0);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void productDto_withNullId_shouldStillPassValidation() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setId(null);
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);

        // Assert - ID is not required for dto
        assertTrue(violations.isEmpty());
    }

    @Test
    void stockUpdateDto_withSpecialCharactersSku_shouldPassValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("SKU-123-ABC");
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor("Vendor A");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void stockUpdateDto_withLongVendorName_shouldPassValidation() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setSku("SKU123");
        dto.setName("Product");
        dto.setStockQuantity(100);
        dto.setVendor("This is a very long vendor name that contains lots of characters");

        // Act
        Set<ConstraintViolation<StockUpdateDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }
}


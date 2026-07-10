package com.naisa.stock_service.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonConstantsTest {

    @Test
    void csvHeaders_skuConstant_shouldMatchExpectedValue() {
        // Assert
        assertEquals("sku", CommonConstants.CsvHeaders.SKU);
    }

    @Test
    void csvHeaders_nameConstant_shouldMatchExpectedValue() {
        // Assert
        assertEquals("name", CommonConstants.CsvHeaders.NAME);
    }

    @Test
    void csvHeaders_stockQuantityConstant_shouldMatchExpectedValue() {
        // Assert
        assertEquals("stockQuantity", CommonConstants.CsvHeaders.STOCK_QUANTITY);
    }

    @Test
    void csvHeaders_allConstantsAreNotNull() {
        // Assert
        assertNotNull(CommonConstants.CsvHeaders.SKU);
        assertNotNull(CommonConstants.CsvHeaders.NAME);
        assertNotNull(CommonConstants.CsvHeaders.STOCK_QUANTITY);
    }

    @Test
    void csvHeaders_allConstantsAreNotEmpty() {
        // Assert
        assertFalse(CommonConstants.CsvHeaders.SKU.isEmpty());
        assertFalse(CommonConstants.CsvHeaders.NAME.isEmpty());
        assertFalse(CommonConstants.CsvHeaders.STOCK_QUANTITY.isEmpty());
    }

    @Test
    void csvHeaders_constantsAreUnique() {
        // Assert
        assertNotEquals(CommonConstants.CsvHeaders.SKU, CommonConstants.CsvHeaders.NAME);
        assertNotEquals(CommonConstants.CsvHeaders.SKU, CommonConstants.CsvHeaders.STOCK_QUANTITY);
        assertNotEquals(CommonConstants.CsvHeaders.NAME, CommonConstants.CsvHeaders.STOCK_QUANTITY);
    }

    @Test
    void csvHeaders_skuConstant_isLowercase() {
        // Assert
        assertEquals("sku", CommonConstants.CsvHeaders.SKU);
        assertEquals("sku", CommonConstants.CsvHeaders.SKU.toLowerCase());
    }

    @Test
    void csvHeaders_nameConstant_isLowercase() {
        // Assert
        assertEquals("name", CommonConstants.CsvHeaders.NAME);
        assertEquals("name", CommonConstants.CsvHeaders.NAME.toLowerCase());
    }

    @Test
    void csvHeaders_shouldContainAllRequiredFields() {
        // Assert - verify all fields exist
        String[] headers = new String[]{
                CommonConstants.CsvHeaders.SKU,
                CommonConstants.CsvHeaders.NAME,
                CommonConstants.CsvHeaders.STOCK_QUANTITY
        };

        assertEquals(3, headers.length);
        for (String header : headers) {
            assertNotNull(header);
            assertFalse(header.isEmpty());
        }
    }
}


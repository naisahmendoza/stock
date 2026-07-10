package com.naisa.stock_service.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VendorTypeEnumTest {

    @Test
    void vendorTypeEnum_shouldHaveVendorAValue() {
        // Act & Assert
        assertNotNull(VendorTypeEnum.VENDOR_A_API);
        assertEquals("VENDOR_A_API", VendorTypeEnum.VENDOR_A_API.name());
    }

    @Test
    void vendorTypeEnum_shouldHaveVendorBValue() {
        // Act & Assert
        assertNotNull(VendorTypeEnum.VENDOR_B_CSV);
        assertEquals("VENDOR_B_CSV", VendorTypeEnum.VENDOR_B_CSV.name());
    }

    @Test
    void vendorTypeEnum_shouldHaveTwoValues() {
        // Act
        VendorTypeEnum[] values = VendorTypeEnum.values();

        // Assert
        assertEquals(2, values.length);
    }

    @Test
    void vendorTypeEnum_shouldContainVendorA() {
        // Act
        VendorTypeEnum[] values = VendorTypeEnum.values();

        // Assert
        assertTrue(containsEnumValue(values, "VENDOR_A_API"));
    }

    @Test
    void vendorTypeEnum_shouldContainVendorB() {
        // Act
        VendorTypeEnum[] values = VendorTypeEnum.values();

        // Assert
        assertTrue(containsEnumValue(values, "VENDOR_B_CSV"));
    }

    @Test
    void vendorTypeEnum_valueOf_shouldReturnCorrectValue() {
        // Act
        VendorTypeEnum vendorA = VendorTypeEnum.valueOf("VENDOR_A_API");
        VendorTypeEnum vendorB = VendorTypeEnum.valueOf("VENDOR_B_CSV");

        // Assert
        assertEquals(VendorTypeEnum.VENDOR_A_API, vendorA);
        assertEquals(VendorTypeEnum.VENDOR_B_CSV, vendorB);
    }

    @Test
    void vendorTypeEnum_valueOf_withInvalidValue_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> VendorTypeEnum.valueOf("VENDOR_C"));
    }

    @Test
    void vendorTypeEnum_enumConstants_shouldNotBeNull() {
        // Act & Assert
        assertNotNull(VendorTypeEnum.VENDOR_A_API);
        assertNotNull(VendorTypeEnum.VENDOR_B_CSV);
    }

    @Test
    void vendorTypeEnum_shouldBeComparable() {
        // Act & Assert
        assertEquals(VendorTypeEnum.VENDOR_A_API, VendorTypeEnum.VENDOR_A_API);
        assertNotEquals(VendorTypeEnum.VENDOR_A_API, VendorTypeEnum.VENDOR_B_CSV);
    }

    @Test
    void vendorTypeEnum_ordinal_shouldBeConsistent() {
        // Act
        int vendorAOrdinal = VendorTypeEnum.VENDOR_A_API.ordinal();
        int vendorBOrdinal = VendorTypeEnum.VENDOR_B_CSV.ordinal();

        // Assert
        assertNotEquals(vendorAOrdinal, vendorBOrdinal);
        assertEquals(0, vendorAOrdinal);
        assertEquals(1, vendorBOrdinal);
    }

    @Test
    void vendorTypeEnum_name_shouldMatchStringValue() {
        // Act & Assert
        assertEquals("VENDOR_A_API", VendorTypeEnum.VENDOR_A_API.name());
        assertEquals("VENDOR_B_CSV", VendorTypeEnum.VENDOR_B_CSV.name());
    }

    private boolean containsEnumValue(VendorTypeEnum[] values, String name) {
        for (VendorTypeEnum value : values) {
            if (value.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}


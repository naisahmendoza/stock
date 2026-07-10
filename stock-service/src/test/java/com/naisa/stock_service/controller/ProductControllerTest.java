package com.naisa.stock_service.controller;

import com.naisa.stock_service.dto.ProductDto;
import com.naisa.stock_service.manager.ProductSyncManager;
import com.naisa.stock_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductSyncManager productSyncManager;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAllProducts_withValidParams_returnsOk() {
        // Arrange
        ProductDto dto = new ProductDto();
        dto.setVendor("Vendor A");

        List<ProductDto> products = new ArrayList<>();
        products.add(dto);

        when(productService.findProductsByParam(any(ProductDto.class))).thenReturn(products);

        // Act
        ResponseEntity<?> response = productController.getAllProducts(new ProductDto());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(productService, times(1)).findProductsByParam(any(ProductDto.class));
    }

    @Test
    void getAllProducts_withNoResults_returnsNoContent() {
        // Arrange
        when(productService.findProductsByParam(any(ProductDto.class))).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<?> response = productController.getAllProducts(new ProductDto());

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).findProductsByParam(any(ProductDto.class));
    }

    @Test
    void getAllProducts_withServiceException_returns500() {
        // Arrange
        when(productService.findProductsByParam(any(ProductDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = productController.getAllProducts(new ProductDto());

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Database error"));
    }

    @Test
    void syncProducts_withValidVendorType_returnsOk() {
        // Arrange
        doNothing().when(productSyncManager).syncProducts("VENDOR_A_API");

        // Act
        ResponseEntity<?> response = productController.syncProducts("VENDOR_A_API");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("successfully"));
        verify(productSyncManager, times(1)).syncProducts("VENDOR_A_API");
    }

    @Test
    void syncProducts_withSyncException_returns500() {
        // Arrange
        doThrow(new RuntimeException("Sync failed")).when(productSyncManager).syncProducts(any());

        // Act
        ResponseEntity<?> response = productController.syncProducts("VENDOR_A_API");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Sync failed"));
    }

    @Test
    void syncProducts_withUnsupportedVendor_returns500() {
        // Arrange
        doThrow(new RuntimeException("No strategy found for the given vendor type"))
                .when(productSyncManager).syncProducts("INVALID_VENDOR");

        // Act
        ResponseEntity<?> response = productController.syncProducts("INVALID_VENDOR");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("No strategy found"));
    }

    @Test
    void getAllProducts_withMultipleProducts_returnsAll() {
        // Arrange
        List<ProductDto> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ProductDto dto = new ProductDto();
            dto.setSku("SKU00" + i);
            products.add(dto);
        }

        when(productService.findProductsByParam(any(ProductDto.class))).thenReturn(products);

        // Act
        ResponseEntity<?> response = productController.getAllProducts(new ProductDto());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<?> result = (List<?>) response.getBody();
        assertEquals(5, result.size());
    }
}

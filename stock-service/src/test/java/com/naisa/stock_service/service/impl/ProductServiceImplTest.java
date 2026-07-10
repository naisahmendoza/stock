package com.naisa.stock_service.service.impl;

import com.naisa.stock_service.dto.ProductDto;
import com.naisa.stock_service.dto.StockUpdateDto;
import com.naisa.stock_service.model.Product;
import com.naisa.stock_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void insertProductList_withNewProducts_shouldSaveAll() {
        // Arrange
        StockUpdateDto dto1 = new StockUpdateDto();
        dto1.setVendor("Vendor A");
        dto1.setSku("SKU001");
        dto1.setName("Product 1");
        dto1.setStockQuantity(100);

        StockUpdateDto dto2 = new StockUpdateDto();
        dto2.setVendor("Vendor B");
        dto2.setSku("SKU002");
        dto2.setName("Product 2");
        dto2.setStockQuantity(50);

        Set<StockUpdateDto> productSet = new HashSet<>();
        productSet.add(dto1);
        productSet.add(dto2);

        // Mock repository to return no existing products
        when(productRepository.findByVendorAndSku(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Create saved products with IDs
        Product savedProduct1 = new Product();
        savedProduct1.setId(UUID.randomUUID());
        savedProduct1.setVendor("Vendor A");
        savedProduct1.setSku("SKU001");
        savedProduct1.setName("Product 1");
        savedProduct1.setStockQuantity(100);

        Product savedProduct2 = new Product();
        savedProduct2.setId(UUID.randomUUID());
        savedProduct2.setVendor("Vendor B");
        savedProduct2.setSku("SKU002");
        savedProduct2.setName("Product 2");
        savedProduct2.setStockQuantity(50);

        when(productRepository.saveAll(anyList()))
                .thenReturn(List.of(savedProduct1, savedProduct2));

        // Act
        List<ProductDto> result = productService.insertProductList(productSet);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(2)).findByVendorAndSku(anyString(), anyString());
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    void insertProductList_withExistingProduct_shouldUpdateAndFetchId() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        Product existingProduct = new Product();
        existingProduct.setId(existingId);
        existingProduct.setVendor("Vendor A");
        existingProduct.setSku("SKU001");
        existingProduct.setName("Product 1");
        existingProduct.setStockQuantity(50); // old stock

        StockUpdateDto dto = new StockUpdateDto();
        dto.setVendor("Vendor A");
        dto.setSku("SKU001");
        dto.setName("Product 1");
        dto.setStockQuantity(150); // new stock

        Set<StockUpdateDto> productSet = new HashSet<>();
        productSet.add(dto);

        // Mock repository to return existing product
        when(productRepository.findByVendorAndSku("Vendor A", "SKU001"))
                .thenReturn(Optional.of(existingProduct));

        // After update, the existing product should be returned with new stock
        Product updatedProduct = new Product();
        updatedProduct.setId(existingId);
        updatedProduct.setVendor("Vendor A");
        updatedProduct.setSku("SKU001");
        updatedProduct.setName("Product 1");
        updatedProduct.setStockQuantity(150); // updated stock

        when(productRepository.saveAll(anyList()))
                .thenReturn(List.of(updatedProduct));

        // Act
        List<ProductDto> result = productService.insertProductList(productSet);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(150, result.get(0).getStockQuantity());
        assertEquals(existingId, result.get(0).getId());
        verify(productRepository, times(1)).findByVendorAndSku("Vendor A", "SKU001");
    }

    @Test
    void insertProductList_withStockQuantityZero_shouldLogWarning() {
        // Arrange
        StockUpdateDto dto = new StockUpdateDto();
        dto.setVendor("Vendor A");
        dto.setSku("SKU001");
        dto.setName("Product 1");
        dto.setStockQuantity(0); // stock is 0

        Set<StockUpdateDto> productSet = new HashSet<>();
        productSet.add(dto);

        when(productRepository.findByVendorAndSku(anyString(), anyString()))
                .thenReturn(Optional.empty());

        Product savedProduct = new Product();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setVendor("Vendor A");
        savedProduct.setSku("SKU001");
        savedProduct.setName("Product 1");
        savedProduct.setStockQuantity(0);

        when(productRepository.saveAll(anyList()))
                .thenReturn(List.of(savedProduct));

        // Act
        List<ProductDto> result = productService.insertProductList(productSet);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStockQuantity());
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    void insertProductList_withEmptySet_shouldReturnEmptyList() {
        // Arrange
        Set<StockUpdateDto> emptySet = new HashSet<>();

        // Act
        List<ProductDto> result = productService.insertProductList(emptySet);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, never()).findByVendorAndSku(anyString(), anyString());
        verify(productRepository, never()).saveAll(anyList());
    }

    @Test
    void insertProductList_withNullSet_shouldReturnEmptyList() {
        // Act
        List<ProductDto> result = productService.insertProductList(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, never()).findByVendorAndSku(anyString(), anyString());
        verify(productRepository, never()).saveAll(anyList());
    }
}

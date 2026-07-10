package com.naisa.stock_service.manager;

import com.naisa.stock_service.dto.ProductDto;
import com.naisa.stock_service.dto.StockUpdateDto;
import com.naisa.stock_service.exception.UnsupportedVendorException;
import com.naisa.stock_service.service.ProductService;
import com.naisa.stock_service.strategy.StockSyncStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSyncManagerTest {

    @Mock
    private ProductService productService;

    @Mock
    private StockSyncStrategy vendorAStrategy;

    @Mock
    private StockSyncStrategy vendorBStrategy;

    @InjectMocks
    private ProductSyncManager productSyncManager;

    @Test
    void syncProducts_withVendorA_shouldCallCorrectStrategy() {
        // Arrange
        Set<StockUpdateDto> stockUpdates = new HashSet<>();
        StockUpdateDto dto = new StockUpdateDto();
        dto.setVendor("Vendor A");
        dto.setSku("SKU001");
        dto.setName("Product 1");
        dto.setStockQuantity(100);
        stockUpdates.add(dto);

        List<ProductDto> savedProducts = new ArrayList<>();
        ProductDto savedDto = new ProductDto();
        savedDto.setId(UUID.randomUUID());
        savedProducts.add(savedDto);

        when(vendorAStrategy.supports("VENDOR_A_API")).thenReturn(true);
        when(vendorAStrategy.fetchLatestStock()).thenReturn(stockUpdates);
        when(productService.insertProductList(stockUpdates)).thenReturn(savedProducts);

        List<StockSyncStrategy> strategies = List.of(vendorAStrategy, vendorBStrategy);
        org.springframework.test.util.ReflectionTestUtils.setField(productSyncManager, "strategies", strategies);

        // Act
        productSyncManager.syncProducts("VENDOR_A_API");

        // Assert
        verify(vendorAStrategy, times(1)).supports("VENDOR_A_API");
        verify(vendorAStrategy, times(1)).fetchLatestStock();
        verify(productService, times(1)).insertProductList(stockUpdates);
    }

    @Test
    void syncProducts_withVendorB_shouldCallCorrectStrategy() {
        // Arrange
        Set<StockUpdateDto> stockUpdates = new HashSet<>();
        StockUpdateDto dto = new StockUpdateDto();
        dto.setVendor("Vendor B");
        dto.setSku("SKU002");
        dto.setName("Product 2");
        dto.setStockQuantity(50);
        stockUpdates.add(dto);

        List<ProductDto> savedProducts = new ArrayList<>();
        ProductDto savedDto = new ProductDto();
        savedDto.setId(UUID.randomUUID());
        savedProducts.add(savedDto);

        when(vendorBStrategy.supports("VENDOR_B_CSV")).thenReturn(true);
        when(vendorAStrategy.supports("VENDOR_B_CSV")).thenReturn(false);
        when(vendorBStrategy.fetchLatestStock()).thenReturn(stockUpdates);
        when(productService.insertProductList(stockUpdates)).thenReturn(savedProducts);

        List<StockSyncStrategy> strategies = List.of(vendorAStrategy, vendorBStrategy);
        org.springframework.test.util.ReflectionTestUtils.setField(productSyncManager, "strategies", strategies);

        // Act
        productSyncManager.syncProducts("VENDOR_B_CSV");

        // Assert
        verify(vendorBStrategy, times(1)).supports("VENDOR_B_CSV");
        verify(vendorBStrategy, times(1)).fetchLatestStock();
        verify(productService, times(1)).insertProductList(stockUpdates);
    }

    @Test
    void syncProducts_withUnsupportedVendor_shouldThrowException() {
        // Arrange
        when(vendorAStrategy.supports("UNKNOWN_VENDOR")).thenReturn(false);
        when(vendorBStrategy.supports("UNKNOWN_VENDOR")).thenReturn(false);

        List<StockSyncStrategy> strategies = List.of(vendorAStrategy, vendorBStrategy);
        org.springframework.test.util.ReflectionTestUtils.setField(productSyncManager, "strategies", strategies);

        // Act & Assert
        assertThrows(UnsupportedVendorException.class, () -> productSyncManager.syncProducts("UNKNOWN_VENDOR"));
        verify(productService, never()).insertProductList(any());
    }

    @Test
    void syncProducts_withEmptyStrategyList_shouldThrowException() {
        // Arrange
        List<StockSyncStrategy> emptyStrategies = new ArrayList<>();
        org.springframework.test.util.ReflectionTestUtils.setField(productSyncManager, "strategies", emptyStrategies);

        // Act & Assert
        assertThrows(UnsupportedVendorException.class, () -> productSyncManager.syncProducts("VENDOR_A_API"));
        verify(productService, never()).insertProductList(any());
    }

    @Test
    void syncProducts_withEmptyStockUpdates_shouldStillInsert() {
        // Arrange
        Set<StockUpdateDto> emptyUpdates = new HashSet<>();
        List<ProductDto> emptyResult = new ArrayList<>();

        when(vendorAStrategy.supports("VENDOR_A_API")).thenReturn(true);
        when(vendorAStrategy.fetchLatestStock()).thenReturn(emptyUpdates);
        when(productService.insertProductList(emptyUpdates)).thenReturn(emptyResult);

        List<StockSyncStrategy> strategies = List.of(vendorAStrategy, vendorBStrategy);
        org.springframework.test.util.ReflectionTestUtils.setField(productSyncManager, "strategies", strategies);

        // Act
        productSyncManager.syncProducts("VENDOR_A_API");

        // Assert
        verify(productService, times(1)).insertProductList(emptyUpdates);
    }

    @Test
    void syncProducts_withStrategyFetchException_shouldPropagateException() {
        // Arrange
        when(vendorAStrategy.supports("VENDOR_A_API")).thenReturn(true);
        when(vendorAStrategy.fetchLatestStock()).thenThrow(new RuntimeException("Fetch failed"));

        List<StockSyncStrategy> strategies = List.of(vendorAStrategy, vendorBStrategy);
        org.springframework.test.util.ReflectionTestUtils.setField(productSyncManager, "strategies", strategies);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productSyncManager.syncProducts("VENDOR_A_API"));
        verify(productService, never()).insertProductList(any());
    }
}

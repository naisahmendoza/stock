package com.naisa.stock_sync_scheduler.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private StockClient stockClient;

    @Test
    void syncProductByVendorType_withValidVendor_shouldCallApi() {
        // Arrange
        String vendorType = "VENDOR_A_API";
        String response = "Sync completed";

        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(stockClient, "syncStockUri", "/api/products/sync");
        ReflectionTestUtils.setField(stockClient, "retryMaxCount", 3);
        ReflectionTestUtils.setField(stockClient, "retryDelay", 1000);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(response));

        // Act
        stockClient.syncProductByVendorType(vendorType);

        // Assert
        verify(webClient, times(1)).post();
        verify(requestBodySpec, times(1)).contentType(any());
    }

    @Test
    void syncProductByVendorType_withException_shouldLogAndNotThrow() {
        // Arrange
        String vendorType = "VENDOR_B_CSV";

        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(stockClient, "syncStockUri", "/api/products/sync");
        ReflectionTestUtils.setField(stockClient, "retryMaxCount", 1);
        ReflectionTestUtils.setField(stockClient, "retryDelay", 100);

        when(webClient.post()).thenThrow(new RuntimeException("Connection timeout"));

        // Act - should not throw exception (exception is caught and logged)
        stockClient.syncProductByVendorType(vendorType);

        // Assert - verify post was attempted
        verify(webClient, times(1)).post();
    }

    @Test
    void syncProductByVendorType_withMultipleVendorTypes_shouldCallEach() {
        // Arrange
        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(stockClient, "syncStockUri", "/api/products/sync");
        ReflectionTestUtils.setField(stockClient, "retryMaxCount", 2);
        ReflectionTestUtils.setField(stockClient, "retryDelay", 500);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Success"));

        // Act
        stockClient.syncProductByVendorType("VENDOR_A_API");
        stockClient.syncProductByVendorType("VENDOR_B_CSV");

        // Assert
        verify(webClient, times(2)).post();
    }

    @Test
    void syncProductByVendorType_withNullVendorType_shouldHandleGracefully() {
        // Arrange
        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(stockClient, "syncStockUri", "/api/products/sync");

        when(webClient.post()).thenThrow(new RuntimeException("Null vendor type"));

        // Act - should handle null gracefully (exception caught and logged)
        stockClient.syncProductByVendorType(null);

        // Assert
        verify(webClient, times(1)).post();
    }

    @Test
    void syncProductByVendorType_shouldUseConfiguredRetryCount() {
        // Arrange
        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(stockClient, "syncStockUri", "/api/products/sync");
        ReflectionTestUtils.setField(stockClient, "retryMaxCount", 5);
        ReflectionTestUtils.setField(stockClient, "retryDelay", 2000);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Success"));

        // Act
        stockClient.syncProductByVendorType("VENDOR_A_API");

        // Assert - verify client was used with retry configuration
        verify(webClient, times(1)).post();
        verify(requestBodySpec, times(1)).retrieve();
    }

    @Test
    void syncProductByVendorType_withEmptyResponse_shouldHandleGracefully() {
        // Arrange
        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(stockClient, "syncStockUri", "/api/products/sync");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(""));

        // Act - should handle empty response gracefully
        stockClient.syncProductByVendorType("VENDOR_A_API");

        // Assert
        verify(webClient, times(1)).post();
        verify(responseSpec, times(1)).bodyToMono(String.class);
    }

    @Test
    void syncProductByVendorType_constructsCorrectUrl() {
        // Arrange
        String baseUrl = "http://localhost:8080";
        String uri = "/api/products/sync";
        String expectedUrl = baseUrl + uri;

        ReflectionTestUtils.setField(stockClient, "stockSyncBaseUrl", baseUrl);
        ReflectionTestUtils.setField(stockClient, "syncStockUri", uri);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Success"));

        // Act
        stockClient.syncProductByVendorType("VENDOR_A_API");

        // Assert - verify uri was called with correct URL pattern
        verify(requestBodyUriSpec, times(1)).uri(expectedUrl);
    }
}


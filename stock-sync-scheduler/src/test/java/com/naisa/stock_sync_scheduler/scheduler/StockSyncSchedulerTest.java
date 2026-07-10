package com.naisa.stock_sync_scheduler.scheduler;

import com.naisa.stock_sync_scheduler.client.StockClient;
import com.naisa.stock_sync_scheduler.constants.VendorTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockSyncSchedulerTest {

    @Mock
    private StockClient stockClient;

    @InjectMocks
    private StockSyncScheduler stockSyncScheduler;

    @Test
    void syncVendorAStock_shouldCallStockClientWithVendorA() {
        // Act
        stockSyncScheduler.syncVendorAStock();

        // Assert
        verify(stockClient, times(1)).syncProductByVendorType(VendorTypeEnum.VENDOR_A_API.name());
        verifyNoMoreInteractions(stockClient);
    }

    @Test
    void syncVendorBStock_shouldCallStockClientWithVendorB() {
        // Act
        stockSyncScheduler.syncVendorBStock();

        // Assert
        verify(stockClient, times(1)).syncProductByVendorType(VendorTypeEnum.VENDOR_B_CSV.name());
        verifyNoMoreInteractions(stockClient);
    }

    @Test
    void syncMethods_shouldPassCorrectEnumValues() {
        // Act
        stockSyncScheduler.syncVendorAStock();
        stockSyncScheduler.syncVendorBStock();

        // Assert
        verify(stockClient, times(1)).syncProductByVendorType("VENDOR_A_API");
        verify(stockClient, times(1)).syncProductByVendorType("VENDOR_B_CSV");
    }

    @Test
    void syncVendorAStock_calledMultipleTimes_shouldCallClientEachTime() {
        // Act
        stockSyncScheduler.syncVendorAStock();
        stockSyncScheduler.syncVendorAStock();
        stockSyncScheduler.syncVendorAStock();

        // Assert
        verify(stockClient, times(3)).syncProductByVendorType(VendorTypeEnum.VENDOR_A_API.name());
    }
}


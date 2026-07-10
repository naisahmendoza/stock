package com.naisa.stock_sync_scheduler.scheduler;

import com.naisa.stock_sync_scheduler.client.StockClient;
import com.naisa.stock_sync_scheduler.constants.VendorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockSyncScheduler {

    @Autowired
    StockClient stockClient;

    @Scheduled(cron = "${stock.sync.vendorA.cron}", zone = "${stock.sync.vendorA.timezone}")
    public void syncVendorAStock() {
        stockClient.syncProductByVendorType(VendorTypeEnum.VENDOR_A_API.name());
    }

    @Scheduled(cron = "${stock.sync.vendorB.cron}", zone = "${stock.sync.vendorB.timezone}")
    public void syncVendorBStock() {
        stockClient.syncProductByVendorType(VendorTypeEnum.VENDOR_B_CSV.name());
    }
}

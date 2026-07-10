package com.naisa.stock_service.strategy.impl;

import com.naisa.stock_service.client.VendorAClient;
import com.naisa.stock_service.constants.VendorTypeEnum;
import com.naisa.stock_service.dto.StockUpdateDto;
import com.naisa.stock_service.strategy.StockSyncStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ApiSyncStrategy implements StockSyncStrategy {

    @Autowired
    private VendorAClient vendorAClient;

    private static final String VENDOR_NAME = "Vendor A";


    @Override
    public boolean supports(String vendorType) {
        return VendorTypeEnum.VENDOR_A_API.name().equalsIgnoreCase(vendorType);
    }

    @Override
    public Set<StockUpdateDto> fetchLatestStock() {
        /*--- validations ---*/

        /*--- default values ---*/
        Set<StockUpdateDto> stockUpdateDtos = new HashSet<>(); // to remove duplicates

        /*--- business logic ---*/
        vendorAClient.getAllProducts().forEach(product -> {
            // validate records are not null, if null remove from save and log details
            if (product.getSku() == null || product.getName() == null || product.getStockQuantity() == null) {
                log.warn("Skipping record with null values in API response");
                return;
            }
            // do not save if invalid
            if (product.getStockQuantity() < 0) {
                log.warn("Invalid stock quantity for SKU: {} in API response", product.getSku());
                return;
            }
            // Map the product to StockUpdateDto
            StockUpdateDto stockUpdateDto = new StockUpdateDto();
            stockUpdateDto.setSku(product.getSku());
            stockUpdateDto.setStockQuantity(product.getStockQuantity());
            stockUpdateDto.setVendor(VENDOR_NAME);
            // Add to the set
            boolean isAdded = stockUpdateDtos.add(stockUpdateDto);
            log.warn("Adding stock update from API for SKU: {} from Vendor: {}. Duplicate: {}", product.getSku(), VENDOR_NAME, isAdded);
        });
        return stockUpdateDtos;
    }
}

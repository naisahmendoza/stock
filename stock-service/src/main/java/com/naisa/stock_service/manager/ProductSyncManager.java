package com.naisa.stock_service.manager;

import com.naisa.stock_service.dto.StockUpdateDto;
import com.naisa.stock_service.exception.UnsupportedVendorException;
import com.naisa.stock_service.service.ProductService;
import com.naisa.stock_service.strategy.StockSyncStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ProductSyncManager {

    @Autowired
    private ProductService productService;

    @Autowired
    private List<StockSyncStrategy> strategies;


    @Transactional
    public void syncProducts(@Valid @NotBlank String vendorType) {
        /*--- validations ---*/
        StockSyncStrategy strategy = strategies.stream()
                .filter(s -> s.supports(vendorType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVendorException("No strategy found for the given vendor type"));

        /*--- default values ---*/

        /*--- business logic ---*/
        Set<StockUpdateDto> stockUpdates = strategy.fetchLatestStock();
        int updateCount = productService.insertProductList(stockUpdates).size();
        log.info("Updated stocks in the database: {} vs initial fetched stock: {}", updateCount, stockUpdates.size());
    }
}

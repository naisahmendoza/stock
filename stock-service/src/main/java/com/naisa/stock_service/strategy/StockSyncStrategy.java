package com.naisa.stock_service.strategy;

import com.naisa.stock_service.dto.StockUpdateDto;

import java.util.Set;

public interface StockSyncStrategy {

    boolean supports(String vendorType);

    Set<StockUpdateDto> fetchLatestStock();
}

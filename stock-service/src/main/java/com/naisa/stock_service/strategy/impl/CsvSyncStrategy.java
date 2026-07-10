package com.naisa.stock_service.strategy.impl;

import com.naisa.stock_service.constants.CommonConstants;
import com.naisa.stock_service.constants.VendorTypeEnum;
import com.naisa.stock_service.dto.StockUpdateDto;
import com.naisa.stock_service.exception.CsvParseException;
import com.naisa.stock_service.strategy.StockSyncStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CsvSyncStrategy implements StockSyncStrategy {

    @Value("${file.vendor.b.csv.path}")
    private Resource csvResource;

    private static final String VENDOR_NAME = "Vendor B";


    @Override
    public boolean supports(String vendorType) {
        return VendorTypeEnum.VENDOR_B_CSV.name().equalsIgnoreCase(vendorType);
    }

    @Override
    public Set<StockUpdateDto> fetchLatestStock() {
        /*--- validations ---*/
        Reader in;
        try {
            in = new InputStreamReader(csvResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file at path: " + csvResource, e);
        }

        /*--- default values ---*/
        Set<StockUpdateDto> stockUpdateDtos = new HashSet<>(); // to remove duplicates
        // add headers to the csv
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        Iterable<CSVRecord> records;
        try {
            records = csvFormat.parse(in);
        } catch (IOException e) {
            throw new CsvParseException("Error parsing CSV file at path: " + csvResource, e);
        }

        /*--- business logic ---*/
        records.forEach(record -> {
            String sku = record.get(CommonConstants.CsvHeaders.SKU);
            String name = record.get(CommonConstants.CsvHeaders.NAME);
            String stockQuantityStr = record.get(CommonConstants.CsvHeaders.STOCK_QUANTITY);

            // validate records are not null, if null remove from save and log details
            if (sku == null || name == null || stockQuantityStr == null) {
                log.warn("Skipping record with null values in CSV file at path: {}", csvResource);
                return;
            }

            // validate stockQuantity is a number
            int stockQuantity;
            try {
                stockQuantity = Integer.parseInt(stockQuantityStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid stock quantity for SKU: {} in CSV file at path: {}", sku, csvResource);
                return;
            }
            // do not save if invalid
            if (stockQuantity < 0) {
                log.warn("Invalid stock quantity for SKU: {} in CSV file at path: {}", sku, csvResource);
                return;
            }
            // Create StockUpdateDto object
            StockUpdateDto stockUpdateDto = new StockUpdateDto();
            stockUpdateDto.setSku(sku);
            stockUpdateDto.setName(name);
            stockUpdateDto.setStockQuantity(stockQuantity);
            stockUpdateDto.setVendor(VENDOR_NAME);

            boolean isAdded = stockUpdateDtos.add(stockUpdateDto);
            log.warn("Adding stock update from CSV for SKU: {} from Vendor: {}. Duplicate: {}", sku, VENDOR_NAME, isAdded);
        });
        return stockUpdateDtos;
    }
}

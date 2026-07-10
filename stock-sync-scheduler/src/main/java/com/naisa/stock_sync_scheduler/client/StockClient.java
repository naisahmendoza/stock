package com.naisa.stock_sync_scheduler.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Component
public class StockClient {

    @Autowired
    private WebClient webClient;

    @Value("${api.stock.sync.service.base-url}")
    private String stockSyncBaseUrl;

    @Value("${api.stock.sync.service.sync-products-uri}")
    private String syncStockUri;

    @Value("${api.stock.sync.service.retry.max-count:3}")
    private int retryMaxCount;

    @Value("${api.stock.sync.service.retry.delay.millis:1000}")
    private int retryDelay;

    public void syncProductByVendorType(String vendorType) {
        try {
            String url = stockSyncBaseUrl + syncStockUri;
            String response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(Mono.just(vendorType), String.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(retryMaxCount, Duration.ofMillis(retryDelay)))
                    .block();
            log.info("Response from Stock Service: {}, From Vendor: {}", response, vendorType);
        } catch (Exception e) {
            log.error("Error occurred while fetching products from stock sync service: {}", e.getMessage(), e);
        }
    }
}

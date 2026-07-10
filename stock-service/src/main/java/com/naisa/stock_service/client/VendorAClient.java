package com.naisa.stock_service.client;

import com.naisa.stock_service.dto.VendorProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class VendorAClient {

    @Autowired
    private WebClient webClient;

    @Value("${api.vendor.a.service.base-url}")
    private String vendorABaseUrl;

    @Value("${api.vendor.a.service.get-products-uri}")
    private String getProductsUri;

    @Value("${api.vendor.a.service.retry.max-count:3}")
    private int retryMaxCount;

    @Value("${api.vendor.a.service.retry.delay.millis:1000}")
    private int retryDelay;

    public List<VendorProductDto> getAllProducts() {
        String url = vendorABaseUrl + getProductsUri;
        log.info("Fetching all products from Vendor A at URL: {}", url);
        List<VendorProductDto> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<VendorProductDto>>() {})
                .retryWhen(Retry.fixedDelay(retryMaxCount, Duration.ofMillis(retryDelay))
                        .filter(throwable -> throwable instanceof TimeoutException)
                        .doBeforeRetry(retrySignal -> log.warn("Retrying due to timeout: attempt {}", retrySignal.totalRetries() + 1))
                ).block();

        log.info("Received response from Vendor A: {}", response);
        return response;
    }
}

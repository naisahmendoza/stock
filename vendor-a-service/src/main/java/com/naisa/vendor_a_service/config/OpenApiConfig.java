package com.naisa.vendor_a_service.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI vendorAOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vendor A Service API")
                        .version("v1")
                        .description("API for vendor A product data"));
    }
}


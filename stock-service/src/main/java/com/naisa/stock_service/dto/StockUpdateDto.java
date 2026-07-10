package com.naisa.stock_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateDto {
    @NotBlank(message = "SKU is required")
    private String sku;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Stock quantity is required")
    private Integer stockQuantity;
    @NotBlank(message = "Vendor is required")
    private String vendor;
}

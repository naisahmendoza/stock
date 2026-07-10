package com.naisa.stock_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema( description = "Vendor Product Data Transfer Object")
public class VendorProductDto {
    @Schema(description = "Unique identifier for the product", type = "string", example = "ABC123")
    private String sku;
    @Schema(description = "Name of the product", type = "string", example = "Sample Product")
    private String name;
    @Schema(description = "Stock quantity of the product", type = "integer", example = "100")
    private Integer stockQuantity;
}

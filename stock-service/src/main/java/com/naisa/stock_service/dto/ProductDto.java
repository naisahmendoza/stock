package com.naisa.stock_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema( description = "Product Data Transfer Object")
public class ProductDto {
    @Schema(description = "Unique identifier for the product", type = "string", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "Unique identifier for the product", type = "string", example = "ABC123")
    private String sku;
    @Schema(description = "Name of the product", type = "string", example = "Sample Product")
    private String name;
    @Schema(description = "Stock quantity of the product", type = "integer", example = "100")
    private Integer stockQuantity;
    @Schema(description = "Vendor of the product", type = "string", example = "Vendor A")
    private String vendor;
}

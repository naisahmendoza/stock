package com.naisa.vendor_a_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Product Data Transfer Object")
public class ProductDto {
    @NotBlank
    @Schema(description = "Unique identifier for the product", type = "string", example = "ABC123")
    private String sku;
    @NotBlank
    @Schema(description = "Name of the product", type = "string", example = "Sample Product")
    private String name;
    @NotNull
    @Min(0)
    @Schema(description = "Available stock quantity", type = "integer", example = "100", minimum = "0")
    private Integer stockQuantity;
}

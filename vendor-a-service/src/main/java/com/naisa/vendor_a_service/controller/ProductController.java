package com.naisa.vendor_a_service.controller;

import com.naisa.vendor_a_service.dto.ProductDto;
import com.naisa.vendor_a_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vendor-a/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products for Vendor A")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        log.info("Request received: GET /vendor-a/products");
        List<ProductDto> productDtoList = productService.getAllProducts();
        log.info("Returning {} products", productDtoList == null ? 0 : productDtoList.size());
        return ResponseEntity.ok(productDtoList);
    }
}

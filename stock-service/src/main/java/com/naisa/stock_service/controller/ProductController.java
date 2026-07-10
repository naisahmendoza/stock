package com.naisa.stock_service.controller;

import com.naisa.stock_service.dto.ProductDto;
import com.naisa.stock_service.manager.ProductSyncManager;
import com.naisa.stock_service.service.ProductService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSyncManager productSyncManager;

    /**
     * get all products by param, will return all list if all params are blank
     *
     * @param dto - fields can be blank or can have parameters
     * @return List of Products
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(@ModelAttribute ProductDto dto) {
        try {
            List<ProductDto> products = productService.findProductsByParam(dto);
            if (products.isEmpty()) {
                log.info("Request received: GET /api/products with params {}, no products found", dto);
                return ResponseEntity.noContent().build();
            } else {
                log.debug("Request received: GET /api/products with params {}, found {} products", dto, products.size());
                return ResponseEntity.ok(products);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body("Invalid parameters: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving products: " + e.getMessage());
        }
    }

    /**
     * Sync product by vendor type
     * @return
     */
    @PostMapping(value = "/sync", consumes = "text/plain")
    public ResponseEntity<?> syncProducts(@Valid @RequestBody @NotBlank String vendorType) {
        try {
            productSyncManager.syncProducts(vendorType);
            return ResponseEntity.ok("Product synchronization completed successfully.");
        } catch (Exception e) {
            log.error("Error during product synchronization: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error during product synchronization: " + e.getMessage());
        }
    }
}

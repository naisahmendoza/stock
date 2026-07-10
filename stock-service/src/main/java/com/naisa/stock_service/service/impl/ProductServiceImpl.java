package com.naisa.stock_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naisa.stock_service.dto.ProductDto;
import com.naisa.stock_service.dto.StockUpdateDto;
import com.naisa.stock_service.model.Product;
import com.naisa.stock_service.repository.ProductRepository;
import com.naisa.stock_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> findProductsByParam(ProductDto productDto) {
        /*--- validations ---*/

        /*--- default values ---*/

        /*--- business logic ---*/
        List<Product> products = productRepository.findProductsByParam(
                productDto.getId(),
                productDto.getSku(),
                productDto.getName(),
                productDto.getStockQuantity(),
                productDto.getVendor()
        );
        return products.stream()
                .map(product -> new ObjectMapper().convertValue(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProductDto> insertProductList(Set<StockUpdateDto> productDtoList) {
        /*--- validations ---*/
        if (productDtoList == null || productDtoList.isEmpty()) {
            log.warn("Product list is empty or null");
            return List.of();
        }

        /*--- default values ---*/
        List<Product> productsToSave = new ArrayList<>();

        /*--- business logic ---*/
        for (StockUpdateDto stockUpdateDto : productDtoList) {
            // Check for unique constraint: vendor + sku
            Optional<Product> existingProduct = productRepository.findByVendorAndSku(
                    stockUpdateDto.getVendor(),
                    stockUpdateDto.getSku()
            );

            Product product;
            if (existingProduct.isPresent()) {
                // if existing, fetch the id and update stock quantity
                product = existingProduct.get();
                log.info("Product already exists: vendor={}, sku={}, id={}",
                        stockUpdateDto.getVendor(), stockUpdateDto.getSku(), product.getId());
                product.setStockQuantity(stockUpdateDto.getStockQuantity());
            } else {
                // if not existing, create a new product
                product = new Product();
                product.setVendor(stockUpdateDto.getVendor());
                product.setSku(stockUpdateDto.getSku());
                product.setName(stockUpdateDto.getName());
                product.setStockQuantity(stockUpdateDto.getStockQuantity());
                log.info("Creating new product: vendor={}, sku={}",
                        stockUpdateDto.getVendor(), stockUpdateDto.getSku());
            }

            // log if stock quantity is 0
            if (product.getStockQuantity() != null && product.getStockQuantity() == 0) {
                log.warn("Stock quantity dropped to 0: vendor={}, sku={}, name={}",
                        product.getVendor(), product.getSku(), product.getName());
            }

            productsToSave.add(product);
        }

        // use saveAll to persist all products
        List<Product> savedProducts = productRepository.saveAll(productsToSave);
        log.info("Successfully saved {} products", savedProducts.size());

        // convert saved products to DTOs and return
        return savedProducts.stream()
                .map(product -> new ObjectMapper().convertValue(product, ProductDto.class))
                .collect(Collectors.toList());
    }
}

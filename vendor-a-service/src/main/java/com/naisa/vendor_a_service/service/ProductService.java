package com.naisa.vendor_a_service.service;

import com.naisa.vendor_a_service.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
}

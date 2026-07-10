package com.naisa.stock_service.service;

import com.naisa.stock_service.dto.ProductDto;
import com.naisa.stock_service.dto.StockUpdateDto;

import java.util.List;
import java.util.Set;

public interface ProductService {

    /**
     * Find products according to the optional fields in the provided ProductDto.
     *
     * @param productDto
     * @return
     */
    List<ProductDto> findProductsByParam(ProductDto productDto);

    /**
     * Insert a list of products.
     *
     * @param productDtoList
     * @return
     */
    List<ProductDto> insertProductList(Set<StockUpdateDto> productDtoList);
}

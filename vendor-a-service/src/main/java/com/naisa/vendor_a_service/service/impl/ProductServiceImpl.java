package com.naisa.vendor_a_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naisa.vendor_a_service.dto.ProductDto;
import com.naisa.vendor_a_service.repository.ProductRepository;
import com.naisa.vendor_a_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public List<ProductDto> getAllProducts() {
        log.info("Fetching all products from repository");
        List<ProductDto> list = productRepository.findAll().stream()
                .map(product -> objectMapper.convertValue(product, ProductDto.class))
                .collect(Collectors.toList());
        log.debug("Fetched {} products", list == null ? 0 : list.size());
        return list;
    }
}

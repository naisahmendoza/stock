package com.naisa.vendor_a_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naisa.vendor_a_service.dto.ProductDto;
import com.naisa.vendor_a_service.model.Product;
import com.naisa.vendor_a_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts_returnsMappedList() {
        Product p = new Product();
        p.setId(UUID.randomUUID());
        p.setSku("SKU1");
        p.setName("Name 1");
        p.setStockQuantity(10);

        Mockito.when(productRepository.findAll()).thenReturn(List.of(p));

        ProductDto dto = new ProductDto();
        dto.setSku("SKU1");
        dto.setName("Name 1");
        dto.setStockQuantity(10);

        Mockito.when(objectMapper.convertValue(p, ProductDto.class)).thenReturn(dto);

        List<ProductDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SKU1", result.get(0).getSku());
        assertEquals("Name 1", result.get(0).getName());
        assertEquals(10, result.get(0).getStockQuantity());
    }
}


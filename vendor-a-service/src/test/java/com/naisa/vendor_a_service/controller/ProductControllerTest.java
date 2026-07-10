package com.naisa.vendor_a_service.controller;

import com.naisa.vendor_a_service.dto.ProductDto;
import com.naisa.vendor_a_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getAllProducts_returnsJsonArray() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setSku("SKU1");
        dto.setName("Name 1");
        dto.setStockQuantity(5);

        Mockito.when(productService.getAllProducts()).thenReturn(List.of(dto));

        mockMvc.perform(get("/vendor-a/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].sku").value("SKU1"))
                .andExpect(jsonPath("$[0].name").value("Name 1"))
                .andExpect(jsonPath("$[0].stockQuantity").value(5));
    }
}


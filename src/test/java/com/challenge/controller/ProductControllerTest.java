package com.challenge.controller;

import com.challenge.dto.ProcessingResponse;
import com.challenge.dto.ProductDTO;
import com.challenge.dto.ProductRequest;
import com.challenge.service.ProductProducerService;
import com.challenge.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductProducerService productProducerService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @Test
    public void testCreateProduct() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        ProductRequest request = new ProductRequest();
        UUID requestId = UUID.randomUUID();

        when(productProducerService.sendProductForProcessing(ArgumentMatchers.any(ProductRequest.class))).thenReturn(requestId);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\"}"))
                .andExpect(status().isAccepted())
                .andExpect(content().json("{\"description\":\"Processing request and create product: \",\"requestId\":\"" + requestId + "\"}"));
    }

    @Test
    public void testGetProductById() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        ProductDTO product = ProductDTO.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Test Product\"}"));
    }

}
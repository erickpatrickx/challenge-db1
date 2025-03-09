package com.challenge.controller;

import com.challenge.dto.ProcessingResponse;
import com.challenge.dto.ProductDTO;
import com.challenge.dto.ProductProcessingResponseDTO;
import com.challenge.dto.ProductRequest;
import com.challenge.service.ProductProducerService;
import com.challenge.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductProducerService productProducerService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProcessingResponse> createProduct(@RequestBody ProductRequest request) {
        UUID requestId = productProducerService.sendProductForProcessing(request);
        return ResponseEntity.accepted().body(ProcessingResponse.builder().description("Processing request and create product: ").requestId(requestId).build());
    }

    @GetMapping("processing/{id}")
    public ResponseEntity<ProductProcessingResponseDTO> getProductByIdProcessing(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findByIdProcessing(id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<ProductDTO> products = productService.getProducts(filter, PageRequest.of(page, size), sortBy, sortDirection);
        return ResponseEntity.ok(products);
    }

}

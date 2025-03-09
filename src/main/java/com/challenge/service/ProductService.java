package com.challenge.service;
import com.challenge.dto.ProductDTO;
import com.challenge.dto.ProductProcessingResponseDTO;
import com.challenge.model.Product;
import com.challenge.model.ProductProcessing;
import com.challenge.model.enums.ProcessingStatus;
import com.challenge.model.enums.Status;
import com.challenge.repository.ProductProcessingRepository;
import com.challenge.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductProcessingRepository productProcessingRepository;


    public Page<ProductDTO> getProducts(String filter, PageRequest pageRequest, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest sortedPageRequest = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);

        Page<Product> products;
        if (filter != null) {
            products = productRepository.findByNameContaining(filter, sortedPageRequest);
        } else {
            products = productRepository.findAll(sortedPageRequest);
        }
        return products.map(this::convertToDTO);
    }

    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return convertToDTO(product);
    }


    public ProductProcessingResponseDTO findByIdProcessing(UUID id) {
        ProductProcessing productProcessing = productProcessingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Processing not found"));

        if (productProcessing.getStatus().equals(ProcessingStatus.PROCESSING)) {
            return new ProductProcessingResponseDTO(null, "Processing not completed");
        }

        if (productProcessing.getStatus().equals(ProcessingStatus.FAILED)) {
            return new ProductProcessingResponseDTO(null, "Processing failed" + productProcessing.getErrorMessage());
        }

        ProductDTO productDTO = convertToDTO(productProcessing.getProduct());
        return new ProductProcessingResponseDTO(productDTO, "Processing successful");
    }


    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategories(product.getCategories().stream()
                .map(category -> category.getName())
                .collect(Collectors.toSet()));
        return dto;
    }
}
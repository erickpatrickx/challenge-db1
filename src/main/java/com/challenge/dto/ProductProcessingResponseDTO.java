package com.challenge.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductProcessingResponseDTO {
    private ProductDTO product;
    private String message;
}
package com.challenge.service;

import com.challenge.dto.AssetFileDTO;
import com.challenge.dto.AssetPresignedUrlDTO;
import com.challenge.dto.AssetUploadRequestDTO;
import com.challenge.model.Asset;
import com.challenge.model.Product;
import com.challenge.model.enums.Status;
import com.challenge.repository.AssetRepository;
import com.challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AssetService assetService;

    private Product product;
    private AssetUploadRequestDTO request;

    @BeforeEach
    public void setup() {

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        AssetFileDTO fileDTO = AssetFileDTO.builder()
                .fileName("testFile")
                .fileExtension("jpg")
                .build();

        request = AssetUploadRequestDTO.builder()
                .productId(1L)
                .files(List.of(fileDTO))
                .build();
    }

}
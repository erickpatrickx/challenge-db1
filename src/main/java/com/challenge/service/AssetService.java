package com.challenge.service;

import com.challenge.dto.AssetDTO;
import com.challenge.dto.AssetPresignedUrlDTO;
import com.challenge.dto.AssetUploadRequestDTO;
import com.challenge.model.Asset;
import com.challenge.model.Product;
import com.challenge.model.enums.AssetType;
import com.challenge.model.enums.Status;
import com.challenge.repository.AssetRepository;
import com.challenge.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final ProductRepository productRepository;
    private final S3Service s3Service;

    public List<AssetPresignedUrlDTO> generatePreSignedUrls(AssetUploadRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.getProductId()));

        long completedCount = assetRepository.countByProductIdAndStatus(request.getProductId(), Status.COMPLETED);
        if (completedCount >= 15) {
            throw new IllegalArgumentException("Cannot generate more URLs. The limit of 15 completed files has been reached.");
        }

        return request.getFiles().stream()
                .map(file -> {
                    String key = getFileKey(request.getProductId(), file.getFileExtension(), file.getFileName());
                    Asset asset = Asset.builder()
                            .product(product)
                            .fileName(file.getFileName())
                            .fileExtension(file.getFileExtension())
                            .type(AssetType.fromFileExtension(file.getFileExtension()))
                            .status(Status.PENDING)
                            .build();
                    assetRepository.save(asset);
                    return new AssetPresignedUrlDTO(asset.getId(),s3Service.generateUploadPreSignedUrl(key));
                })
                .collect(Collectors.toList());
    }

    public void confirmUpload(Long productId, List<Long> assetIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        for (Long assetId : assetIds) {
            Asset asset = assetRepository.findById(assetId)
                    .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + assetId));
            String key = getFileKey(productId, asset.getFileExtension(), asset.getFileName());
            if (!s3Service.doesObjectExist(key)) {
                throw new RuntimeException("Object with key " + key + " does not exist in bucket ");
            }
        }
        assetRepository.updateStatusToUploaded(productId, assetIds, Status.COMPLETED);
        product.setStatus(Status.COMPLETED);
        productRepository.save(product);
        updateUnconfirmedAssetsToAbandoned(productId);
        log.info("Assets {} for product {} are confirmed as uploaded.", assetIds, productId);
    }

    @Async
    public void updateUnconfirmedAssetsToAbandoned(Long productId) {
        List<Asset> unconfirmedAssets = assetRepository.findByProductIdAndAndStatus(productId,Status.PENDING);
        for (Asset asset : unconfirmedAssets) {
            asset.setStatus(Status.ABANDONED);
            assetRepository.save(asset);
        }
        log.info("Unconfirmed assets for product {} have been updated to ABANDONED.", productId);
    }


    public List<AssetDTO> getAssetsForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        List<Asset> assets = assetRepository.findByProductId(productId);
        return assets.stream()
                .map(asset -> {
                    String key = getFileKey(productId, asset.getFileExtension(), asset.getFileName());
                    String url = s3Service.generateViewPreSignedUrl(key);
                    return AssetDTO.builder()
                            .id(asset.getId())
                            .type(asset.getType())
                            .fileName(asset.getFileName())
                            .fileExtension(asset.getFileExtension())
                            .status(asset.getStatus())
                            .productId(productId)
                            .url(url)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static String getFileKey(Long productId, String fileExtension, String fileName) {
        return "products/" + productId + "/" + fileName + "." + fileExtension;
    }
}
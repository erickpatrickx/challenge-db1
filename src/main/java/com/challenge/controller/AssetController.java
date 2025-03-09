package com.challenge.controller;

import com.challenge.dto.AssetDTO;
import com.challenge.dto.AssetPresignedUrlDTO;
import com.challenge.dto.AssetUploadRequestDTO;
import com.challenge.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/generate-presigned-urls")
    public ResponseEntity<List<AssetPresignedUrlDTO>> generatePreSignedUrls(@RequestBody AssetUploadRequestDTO request) {
        return ResponseEntity.ok(assetService.generatePreSignedUrls(request));
    }

    @PostMapping("/confirm-upload/{productId}")
    public ResponseEntity<Void> confirmUpload(@PathVariable Long productId, @RequestBody List<Long> assetIds) {
        assetService.confirmUpload(productId, assetIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<AssetDTO>> getAssetsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(assetService.getAssetsForProduct(productId));
    }
}

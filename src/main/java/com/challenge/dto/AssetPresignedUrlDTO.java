package com.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssetPresignedUrlDTO {
    private Long assetId;
    private String presignedUrl;
}
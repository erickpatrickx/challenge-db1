package com.challenge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AssetUploadRequestDTO {
    private Long productId;
    private List<AssetFileDTO> files;
}


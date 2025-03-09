package com.challenge.dto;

import com.challenge.model.enums.AssetType;
import com.challenge.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssetDTO {
    private Long id;
    private AssetType type;
    private String fileName;
    private String fileExtension;
    private Status status;
    private Long productId;
    private String url;
}
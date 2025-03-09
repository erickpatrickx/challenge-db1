package com.challenge.dto;

import com.challenge.model.enums.AssetType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AssetFileDTO {

    private AssetType type;
    private String fileExtension;
    private String fileName;
}

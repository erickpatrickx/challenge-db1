package com.challenge.dto;

import com.challenge.model.enums.AttributeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAttributeRequest {
    private String name;
    private AttributeType type;
    private String value;
}

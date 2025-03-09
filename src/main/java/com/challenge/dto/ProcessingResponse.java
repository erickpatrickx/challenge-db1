package com.challenge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ProcessingResponse {
    private String description;
    private UUID requestId;
}

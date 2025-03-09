package com.challenge.model;

import com.challenge.model.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_processing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductProcessing {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status;

    private String errorMessage;

    @Column(nullable = false)
    private String request;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;
}

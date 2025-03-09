package com.challenge.repository;

import com.challenge.model.ProductProcessing;
import com.challenge.model.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductProcessingRepository extends JpaRepository<ProductProcessing, UUID> {

}

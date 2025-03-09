package com.challenge.repository;

import com.challenge.model.Asset;
import com.challenge.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Asset a SET a.status = :status WHERE a.id IN :assetIds and a.product.id = :productId")
    void updateStatusToUploaded(Long productId,List<Long> assetIds, Status status);

    List<Asset> findByProductId(Long productId);

    List<Asset> findByProductIdAndAndStatus(Long productId, Status status);

    long countByProductIdAndStatus(Long productId, Status status);
}

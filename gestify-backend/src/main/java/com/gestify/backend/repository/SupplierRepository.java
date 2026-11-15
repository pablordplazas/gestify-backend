package com.gestify.backend.repository;

import com.gestify.backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products WHERE s.business.id = :businessId ORDER BY s.id DESC")
    List<Supplier> findByBusinessIdWithProducts(@Param("businessId") Long businessId);
}
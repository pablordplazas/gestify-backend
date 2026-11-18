package com.gestify.backend.repository;

import com.gestify.backend.model.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
    List<SupplierOrder> findByBusinessIdOrderByIdDesc(Long businessId);
    Optional<SupplierOrder> findByIdAndBusinessId(Long id, Long businessId);
}
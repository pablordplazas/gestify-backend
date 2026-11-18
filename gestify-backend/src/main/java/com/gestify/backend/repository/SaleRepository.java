package com.gestify.backend.repository;

import com.gestify.backend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByBusinessIdOrderByIdDesc(Long businessId);
}